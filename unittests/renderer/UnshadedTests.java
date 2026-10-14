package renderer;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.*;
import geometries.Intersectable.Intersection;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for the private method {@code unshaded} of {@link SimpleRayTracer} (the version of part 2 of stage 7,
 * kept for the review of the lecturer). The method is invoked by reflection, so the public interface of the class
 * is not extended for the tests.
 * The tested surface is the plane z=0, lit at the origin from above. The viewer looks at the origin obliquely
 * from (10,0,10), so the viewing ray hits the plane and passes by all the blockers ({@link #testViewerHitsPlane}).
 * The method returns {@code false} only if a blocker before the light has kT lower than the minimal coefficient
 * (0.001) in all three channels.
 */
class UnshadedTests {
   /** Default constructor to satisfy JavaDoc generator */
   UnshadedTests() { /* to satisfy JavaDoc generator */ }

   /** Light intensity used in the tests */
   private final Color white = new Color(100, 100, 100);
   /** Oblique viewer looking at the origin */
   private final Ray viewer = new Ray(new Point(10, 0, 10), new Vector(-1, 0, -1));
   /** Light position for point lights and spotlights - right above the origin */
   private final Point lightPos = new Point(0, 0, 5);

   // ============ Helpers ==============

   /**
    * Helper: a triangle in the plane z, that covers the vertical line above the origin and not the viewing ray
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
    * Helper: calls the private method {@code unshaded} for the hit point of the viewer on the plane z=0
    * @param light    the light source
    * @param blockers the geometries between the plane and the light, or beyond it
    * @return the result of {@code unshaded}
    * @throws Exception if the reflection fails
    */
   private boolean unshaded(LightSource light, Geometry... blockers) throws Exception {
      Scene scene = new Scene("Unshaded test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setMaterial(new Material().setKD(1).setKS(0)));
      scene.geometries.add(blockers);
      SimpleRayTracer tracer = new SimpleRayTracer(scene);

      List<Intersection> hits = scene.geometries.calculateIntersections(viewer);
      Intersection gp = viewer.findClosestIntersection(hits);
      assertEquals(Point.ZERO, gp.point, "The viewing ray should hit the plane at the origin");
      assertTrue(tracer.preprocessIntersection(gp, viewer.getDirection()), "The hit should not be tangent");
      assertTrue(tracer.setLightSource(gp, light), "The light should be on the side of the viewer");

      Method method = SimpleRayTracer.class.getDeclaredMethod("unshaded", Intersection.class);
      method.setAccessible(true);
      return (boolean) method.invoke(tracer, gp);
   }

   /** @return a point light right above the origin */
   private LightSource pointLight() { return new PointLight(white, lightPos); }

   /** @return a spotlight right above the origin pointing down */
   private LightSource spotLight() { return new SpotLight(white, lightPos, new Vector(0, 0, -1)); }

   /** @return a directional light that shines down */
   private LightSource directionalLight() { return new DirectionalLight(white, new Vector(0, 0, -1)); }

   // ============ Sanity ==============

   /** Sanity: the viewing ray must hit the plane at the origin and none of the blockers of the tests */
   @Test
   void testViewerHitsPlane() {
      Scene scene = new Scene("Viewer test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z),
                           triangle(2.5, Double3.ZERO), triangle(3, Double3.ZERO),
                           sphere(2.5, 1, Double3.ZERO), sphere(8, 1, Double3.ZERO), sphere(1000, 10, Double3.ZERO));
      assertEquals(1, scene.geometries.findIntersections(viewer).size(),
                   "The viewing ray should hit the plane only");
   }

   // ============ Required cases ==============

   /** Without a blocker the point is unshaded */
   @Test
   void testNoBlocker() throws Exception {
      assertTrue(unshaded(pointLight()), "Nothing is in the way of the light");
   }

   /** An opaque blocker (kT=0) between the point and the light casts a shadow */
   @Test
   void testOpaqueBlockerBeforeLight() throws Exception {
      assertFalse(unshaded(pointLight(), sphere(2.5, 1, Double3.ZERO)), "An opaque blocker should cast a shadow");
   }

   /** A partly transparent blocker (kT=0.5) does not cast a shadow in this version */
   @Test
   void testHalfTransparentBlocker() throws Exception {
      assertTrue(unshaded(pointLight(), triangle(2.5, new Double3(0.5))), "kT=0.5 should not cast a shadow");
   }

   /** The coefficient is compared to the minimal one with {@code lowerThan}: all three channels must be lower */
   @Test
   void testThreshold() throws Exception {
      // TC01: 0.0009 in all channels - all are lower than 0.001, the blocker casts a shadow
      assertFalse(unshaded(pointLight(), triangle(2.5, new Double3(0.0009))), "kT=0.0009 should cast a shadow");
      // TC02: exactly 0.001 in all channels - not lower than the threshold, no shadow
      assertTrue(unshaded(pointLight(), triangle(2.5, new Double3(0.001))), "kT=0.001 should not cast a shadow");
      // TC03: one channel above the threshold - not all channels are lower, no shadow
      assertTrue(unshaded(pointLight(), triangle(2.5, new Double3(0.0009, 0.0009, 0.002))),
                 "One channel above the threshold should not cast a shadow");
   }

   /** An opaque blocker beyond a point light does not cast a shadow (the distance filter) */
   @Test
   void testOpaqueBlockerBeyondPointLight() throws Exception {
      assertTrue(unshaded(pointLight(), sphere(8, 1, Double3.ZERO)), "A blocker beyond the light should not cast a shadow");
   }

   /** A directional light has no position, so a far opaque blocker casts a shadow */
   @Test
   void testDirectionalLightFarBlocker() throws Exception {
      assertFalse(unshaded(directionalLight(), sphere(1000, 10, Double3.ZERO)),
                  "A far opaque blocker should cast a shadow for a directional light");
   }

   // ============ Additional cases ==============

   /** A spotlight behaves like a point light for the shadow ray */
   @Test
   void testSpotLight() throws Exception {
      // TC01: opaque blocker before the light
      assertFalse(unshaded(spotLight(), sphere(2.5, 1, Double3.ZERO)), "An opaque blocker should cast a shadow");
      // TC02: opaque blocker beyond the light
      assertTrue(unshaded(spotLight(), sphere(8, 1, Double3.ZERO)), "A blocker beyond the light should not cast a shadow");
   }

   /**
    * Every geometry is considered by itself. An opaque geometry among partly transparent ones casts a shadow,
    * and the coefficients are not multiplied: two triangles with kT=0.03 (product 0.0009) do not cast a shadow here.
    * (The method {@code transparency} used by the rendering multiplies them and gives a full shadow.)
    */
   @Test
   void testSeveralBlockers() throws Exception {
      // TC01: a partly transparent triangle and an opaque one
      assertFalse(unshaded(pointLight(), triangle(2.5, new Double3(0.5)), triangle(3, Double3.ZERO)),
                  "One opaque blocker among others should cast a shadow");
      // TC02: two partly transparent triangles whose product is below the threshold
      assertTrue(unshaded(pointLight(), triangle(2.5, new Double3(0.03)), triangle(3, new Double3(0.03))),
                 "The coefficients should not be multiplied");
   }

   /** Both intersections of a sphere count: a sphere that the light is inside of casts a shadow if opaque */
   @Test
   void testLightInsideOpaqueSphere() throws Exception {
      // the sphere contains the light (center z=5.5, radius 1): the entry point at z=4.5 is before the light
      assertFalse(unshaded(pointLight(), sphere(5.5, 1, Double3.ZERO)),
                  "The entry point of an opaque sphere before the light should cast a shadow");
   }
}
