package renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for the partial shadows that transparent geometries cast.
 * The tested surface is the plane z=0 (kD=1, kS=0) lit at the origin from above by a light of intensity 100,
 * so a light that reaches the point without any attenuation gives 100 in all three channels.
 * The viewer looks at the origin obliquely from (10,0,10), so the viewing ray hits the plane and passes by
 * all the blockers ({@link #testViewerHitsPlane} checks it).
 * The expected values are computed by hand and compared on the internal color values,
 * before the conversion to {@link java.awt.Color}.
 */
class TransparencyShadowTests {
   /** Default constructor to satisfy JavaDoc generator */
   TransparencyShadowTests() { /* to satisfy JavaDoc generator */ }

   /** Tolerance for the comparison of colors */
   private static final double EPS = 1e-6;
   /** Light intensity used in the tests */
   private final Color white = new Color(100, 100, 100);
   /** Oblique viewer looking at the origin */
   private final Ray viewer = new Ray(new Point(10, 0, 10), new Vector(-1, 0, -1));
   /** Light position for point lights - right above the origin */
   private final Point lightPos = new Point(0, 0, 5);

   // ============ Helpers ==============

   /**
    * Helper: the internal RGB values of a color (the color has no getters for them)
    * @param color the color
    * @return the red, green and blue components
    */
   private static double[] rgb(Color color) {
      String s = color.toString(); // "rgb:(r,g,b)"
      String[] parts = s.substring(s.indexOf('(') + 1, s.lastIndexOf(')')).split(",");
      return new double[] { Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]) };
   }

   /**
    * Helper: asserts the internal RGB values of a color
    * @param r      expected red
    * @param g      expected green
    * @param b      expected blue
    * @param actual the color to check
    */
   private static void assertRgb(double r, double g, double b, Color actual) {
      double[] c = rgb(actual);
      assertEquals(r, c[0], EPS, "Wrong red component");
      assertEquals(g, c[1], EPS, "Wrong green component");
      assertEquals(b, c[2], EPS, "Wrong blue component");
   }

   /**
    * Helper: a triangle in the plane z, that covers the vertical line above the origin and not the viewing ray
    * (the viewing ray crosses the plane z=2.5 at (2.5,0), where x+y>1)
    * @param z  the height of the triangle
    * @param kT the transparency coefficient
    * @return the triangle
    */
   private static Geometry triangle(double z, Double3 kT) {
      return new Triangle(new Point(-1, -1, z), new Point(2, -1, z), new Point(-1, 2, z))
         .setMaterial(new Material().setKT(kT));
   }

   /**
    * Helper: a sphere above the origin
    * @param z      the height of the center
    * @param radius the radius
    * @param kT     the transparency coefficient
    * @return the sphere
    */
   private static Geometry sphere(double z, double radius, Double3 kT) {
      return new Sphere(new Point(0, 0, z), radius).setMaterial(new Material().setKT(kT));
   }

   /**
    * Helper: traces the viewer ray in a scene with the plane z=0, one light and the blockers
    * @param light    the light source
    * @param blockers the geometries between the plane and the light, or beyond it
    * @return the traced color
    */
   private Color trace(LightSource light, Geometry... blockers) {
      Scene scene = new Scene("Transparency shadow test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setMaterial(new Material().setKD(1).setKS(0)));
      scene.geometries.add(blockers);
      scene.lights.add(light);
      return new SimpleRayTracer(scene).traceRay(viewer);
   }

   /** @return a point light right above the origin */
   private LightSource pointLight() { return new PointLight(white, lightPos); }

   /** @return a directional light that shines down */
   private LightSource directionalLight() { return new DirectionalLight(white, new Vector(0, 0, -1)); }

   // ============ Sanity ==============

   /** Sanity: the viewing ray must hit the plane at the origin and none of the blockers of the tests */
   @Test
   void testViewerHitsPlane() {
      Scene scene = new Scene("Viewer test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z),
                           triangle(2.5, Double3.ONE), triangle(3, Double3.ONE),
                           sphere(2.5, 1, Double3.ONE), sphere(8, 1, Double3.ONE), sphere(1000, 10, Double3.ONE));
      assertEquals(Point.ZERO, viewer.findClosestPoint(scene.geometries.findIntersections(viewer)),
                   "The viewing ray should hit the plane at the origin");
      assertEquals(1, scene.geometries.findIntersections(viewer).size(),
                   "The viewing ray should not hit any of the blockers");
   }

   // ============ Partial shadows ==============

   /** Without a blocker the light reaches the point as it is */
   @Test
   void testNoBlocker() {
      assertRgb(100, 100, 100, trace(pointLight()));
   }

   /** One triangle with kT=0.5 between the point and the light halves the light */
   @Test
   void testTriangleHalfTransparent() {
      assertRgb(50, 50, 50, trace(pointLight(), triangle(2.5, new Double3(0.5))));
   }

   /** The coefficient of every channel is applied to its own channel */
   @Test
   void testTrianglePerChannel() {
      assertRgb(50, 20, 100, trace(pointLight(), triangle(2.5, new Double3(0.5, 0.2, 1))));
   }

   /**
    * The shadow ray enters and leaves a sphere with kT=0.5, and both intersections are before the light
    * (at z=1.5 and z=3.5, the light is at z=5): 0.5 * 0.5
    */
   @Test
   void testSphereTwoIntersections() {
      assertRgb(25, 25, 25, trace(pointLight(), sphere(2.5, 1, new Double3(0.5))));
   }

   /** An opaque blocker (kT=0) gives no direct light, but the emission and the ambient light remain */
   @Test
   void testOpaqueBlocker() {
      Scene scene = new Scene("Opaque blocker").setAmbientLight(new AmbientLight(new Color(40, 40, 40)));
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setEmission(new Color(10, 20, 30))
                              .setMaterial(new Material().setKD(1).setKS(0)),
                           sphere(2.5, 1, Double3.ZERO));
      scene.lights.add(pointLight());
      // the direct contribution is 0: emission + ambient only
      assertRgb(50, 60, 70, new SimpleRayTracer(scene).traceRay(viewer));
   }

   /** A transparent blocker beyond a point light and beyond a spotlight does not attenuate the light */
   @Test
   void testBlockerBeyondLight() {
      // TC01: point light
      assertRgb(100, 100, 100, trace(pointLight(), sphere(8, 1, new Double3(0.5))));
      // TC02: spotlight
      assertRgb(100, 100, 100, trace(new SpotLight(white, lightPos, new Vector(0, 0, -1)),
                                     sphere(8, 1, new Double3(0.5))));
      // TC03: the light is between the two intersections of the sphere - only the one before the light counts
      assertRgb(50, 50, 50, trace(pointLight(), sphere(5.5, 1, new Double3(0.5))));
   }

   /** A directional light has no position: both intersections of a sphere count, at any distance */
   @Test
   void testDirectionalLightSphere() {
      // TC01: near sphere
      assertRgb(25, 25, 25, trace(directionalLight(), sphere(2.5, 1, new Double3(0.5))));
      // TC02: far sphere
      assertRgb(25, 25, 25, trace(directionalLight(), sphere(1000, 10, new Double3(0.5))));
   }

   /** The coefficients of several blockers are multiplied, per channel */
   @Test
   void testSeveralBlockers() {
      // TC01: 0.5 * 0.4
      assertRgb(20, 20, 20, trace(pointLight(), triangle(2.5, new Double3(0.5)), triangle(3, new Double3(0.4))));
      // TC02: per channel, (0.5,1,0.2) * (0.4,0.5,0.5)
      assertRgb(20, 50, 10, trace(pointLight(), triangle(2.5, new Double3(0.5, 1, 0.2)),
                                  triangle(3, new Double3(0.4, 0.5, 0.5))));
      // TC03: a transparent triangle and an opaque one - the light is blocked
      assertRgb(0, 0, 0, trace(pointLight(), triangle(2.5, new Double3(0.5)), triangle(3, Double3.ZERO)));
      // TC04: the same blockers in the opposite order of the scene
      assertRgb(20, 20, 20, trace(pointLight(), triangle(3, new Double3(0.4)), triangle(2.5, new Double3(0.5))));
   }

   // ============ Threshold ==============

   /** The path is cut only if the accumulated coefficient is lower than the threshold in all three channels */
   @Test
   void testThreshold() {
      // TC01: all the channels are lower - no light
      assertRgb(0, 0, 0, trace(pointLight(), triangle(2.5, new Double3(0.0009))));
      // TC02: one channel is above the threshold - the light continues, and each channel gets its own coefficient
      assertRgb(0.09, 0.09, 0.2, trace(pointLight(), triangle(2.5, new Double3(0.0009, 0.0009, 0.002))));
      // TC03: exactly the threshold is not lower than it
      assertRgb(0.1, 0.1, 0.1, trace(pointLight(), triangle(2.5, new Double3(0.001))));
      // TC04: the coefficients of two blockers are accumulated before the check: 0.03 * 0.03 = 0.0009
      assertRgb(0, 0, 0, trace(pointLight(), triangle(2.5, new Double3(0.03)), triangle(3, new Double3(0.03))));
   }

   // ============ Accumulated coefficient of the recursion and transparency of the shadow ==============

   /**
    * Helper: the viewer ray is reflected by the mirror z=0 (kR) and the reflection ray (-1,0,1)/sqrt(2) hits the
    * plane x=-10 at (-10,0,10.1), at level 9 with the accumulated coefficient kR. That plane (kD=1, kS=0) is lit
    * by a point light at (-5,0,10.1), through a triangle in the plane x=-7.5 with the coefficient kT.
    * The mirror has no local lighting (kD=kS=0), and no emission, so the result is what the plane brings back:
    * the local color of the plane, scaled by kR once.
    * @param kR the reflection coefficient of the mirror
    * @param kT the transparency coefficient of the triangle in front of the light
    * @return the traced color
    */
   private Color traceMirrorToLitPlane(double kR, double kT) {
      Scene scene = new Scene("Mirror to a lit plane");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setMaterial(new Material().setKR(kR)),
                           new Plane(new Point(-10, 0, 0), Vector.AXIS_X)
                              .setMaterial(new Material().setKD(1).setKS(0)),
                           new Triangle(new Point(-7.5, -1, 9.1), new Point(-7.5, 2, 9.1), new Point(-7.5, -1, 12.1))
                              .setMaterial(new Material().setKT(kT)));
      scene.lights.add(new PointLight(white, new Point(-5, 0, 10.1)));
      return new SimpleRayTracer(scene).traceRay(viewer);
   }

   /**
    * The light of a point that is reached after a reflection is skipped by the product of the accumulated
    * coefficient and the transparency of the shadow, and when it is not skipped, it is attenuated by the
    * transparency only (the accumulated coefficient is applied once, on the way back from the recursion).
    */
   @Test
   void testAccumulatedCoefficientWithShadowTransparency() {
      // TC01: 0.03 (kR) * 0.03 (kT) = 0.0009 < 0.001 - the light is skipped, although kT alone is above the threshold
      assertRgb(0, 0, 0, traceMirrorToLitPlane(0.03, 0.03));
      // TC02: 0.03 * 0.2 = 0.006 - the local color is 100 * 0.2 = 20, scaled by kR on the way back: 0.6
      assertRgb(0.6, 0.6, 0.6, traceMirrorToLitPlane(0.03, 0.2));
      // TC03: just above the threshold: 0.03 * 0.04 = 0.0012 - 100 * 0.04 * 0.03
      assertRgb(0.12, 0.12, 0.12, traceMirrorToLitPlane(0.03, 0.04));
      // TC04: kR=1 - the light of the plane comes back as it is, attenuated by the shadow only
      assertRgb(20, 20, 20, traceMirrorToLitPlane(1, 0.2));
   }
}
