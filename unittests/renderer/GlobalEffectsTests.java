package renderer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for the global effects (reflection and transparency) of {@link SimpleRayTracer}.
 * The expected values are computed by hand. They are compared on the internal color values,
 * before the conversion to {@link java.awt.Color}, so clipping and rounding can't hide a mistake.
 * The scenes have no lights and no ambient light unless stated otherwise, and all the materials
 * have kA=1, so the color of a hit point is its emission.
 * <p>
 * Assumptions that were not yet verified against the course material:
 * the ambient light is added once, at the first hit only ({@link #testAmbientOnlyAtFirstHit},
 * {@link #testTangentHit}), and a path has at most 10 surfaces ({@link #testRecursionDepth}).
 */
class GlobalEffectsTests {
   /** Default constructor to satisfy JavaDoc generator */
   GlobalEffectsTests() { /* to satisfy JavaDoc generator */ }

   /** Tolerance for the comparison of colors */
   private static final double EPS = 1e-6;

   /** Viewer ray that hits the plane z=0 at the origin, going down and to the left */
   private final Ray viewer = new Ray(new Point(10, 0, 10), new Vector(-1, 0, -1));

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
    * Helper: a plane that is a mirror
    * @param point    a point of the plane
    * @param normal   the normal of the plane
    * @param emission the emission
    * @param kR       the reflection coefficient
    * @return the plane
    */
   private static Geometry mirror(Point point, Vector normal, Color emission, Double3 kR) {
      return new Plane(point, normal).setEmission(emission).setMaterial(new Material().setKR(kR));
   }

   /**
    * Helper: a sphere with a given emission
    * @param center   the center
    * @param radius   the radius
    * @param emission the emission
    * @return the sphere
    */
   private static Geometry ball(Point center, double radius, Color emission) {
      return new Sphere(center, radius).setEmission(emission);
   }

   /**
    * Helper: traces a ray in a scene with the given geometries
    * @param scene      the scene
    * @param ray        the ray
    * @param geometries the geometries to add to the scene
    * @return the traced color
    */
   private static Color trace(Scene scene, Ray ray, Geometry... geometries) {
      scene.geometries.add(geometries);
      return new SimpleRayTracer(scene).traceRay(ray);
   }

   /**
    * Helper: the scene of the reflection tests. The viewer hits the plane z=0 at the origin, and the
    * reflection ray (going up and to the left) hits a ball at (-10,0,10) with radius 1 and emission 60.
    * @param scene    the scene
    * @param planeKr  the reflection coefficient of the plane
    * @param planeEmission the emission of the plane
    * @param ballEmission  the emission of the ball
    * @return the traced color
    */
   private Color traceReflection(Scene scene, Double3 planeKr, Color planeEmission, Color ballEmission) {
      return trace(scene, viewer,
                   mirror(Point.ZERO, Vector.AXIS_Z, planeEmission, planeKr),
                   ball(new Point(-10, 0, 10), 1d, ballEmission));
   }

   // ============ Reflection and transparency ==============

   /** The reflection brings the color of the ball, attenuated per channel by kR */
   @Test
   void testReflection() {
      // R = (-1,0,1)/sqrt(2): from the hit point (0,0,0.1) the ray meets the ball at (-10,0,10)
      Color c = traceReflection(new Scene("Reflection"), new Double3(0.5, 0.25, 1),
                                new Color(20, 20, 20), new Color(60, 60, 60));
      // emission + kR * ball = 20 + 0.5*60, 20 + 0.25*60, 20 + 1*60
      assertRgb(50, 35, 80, c);
   }

   /** The transparency brings the color of the ball that is behind the plane, attenuated per channel by kT */
   @Test
   void testTransparency() {
      // the ray continues in its direction (-1,0,-1)/sqrt(2) from (0,0,-0.1) and meets the ball at (-10,0,-10)
      Scene scene = new Scene("Transparency");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setEmission(new Color(20, 20, 20))
                              .setMaterial(new Material().setKT(new Double3(0.5, 0.25, 1))));
      Color c = trace(scene, viewer, ball(new Point(-10, 0, -10), 1d, new Color(60, 60, 60)));
      assertRgb(50, 35, 80, c);
   }

   // ============ Background ==============

   /** A secondary ray with no hit brings the background, attenuated by the coefficient of the pass */
   @Test
   void testBackground() {
      Color background = new Color(100, 60, 20);
      // TC01: the reflection ray hits nothing - background * kR (the plane has no emission)
      Scene scene = new Scene("Secondary background").setBackground(background);
      assertRgb(50, 15, 20, trace(scene, viewer,
                                  mirror(Point.ZERO, Vector.AXIS_Z, Color.BLACK, new Double3(0.5, 0.25, 1))));
      // TC02: the primary ray hits nothing - the background without attenuation
      Scene scene2 = new Scene("Primary background").setBackground(background);
      assertRgb(100, 60, 20, trace(scene2, new Ray(new Point(10, 0, 10), new Vector(0, 0, 1)),
                                   mirror(Point.ZERO, Vector.AXIS_Z, Color.BLACK, new Double3(0.5, 0.25, 1))));
   }

   // ============ Two passes in a row ==============

   /**
    * The attenuation of two consecutive reflections: kR1 * kR2, once.
    * The path: the viewer hits the mirror z=0 (kR=0.5), the reflection (-1,0,1)/sqrt(2) hits the mirror x=-10
    * (kR=0.4), and its reflection (1,0,1)/sqrt(2) meets the ball at (0,0,20).
    * A double attenuation (e.g. scaling by the accumulated coefficient in every level) gives 100,
    * and a missing attenuation gives 500, 400 or 1000.
    */
   @Test
   void testTwoReflections() {
      Geometry mirror1 = mirror(Point.ZERO, Vector.AXIS_Z, Color.BLACK, new Double3(0.5));
      Geometry mirror2 = mirror(new Point(-10, 0, 0), Vector.AXIS_X, Color.BLACK, new Double3(0.4));
      // TC01: the ball is the last thing the ray sees
      assertRgb(200, 200, 200, trace(new Scene("Two reflections"), viewer, mirror1, mirror2,
                                     ball(new Point(0, 0, 20), 1d, new Color(1000, 1000, 1000))));
      // TC02: nothing is seen - the background is attenuated by both passes
      Scene scene = new Scene("Two reflections, background").setBackground(new Color(100, 100, 100));
      assertRgb(20, 20, 20, trace(scene, viewer, mirror1, mirror2));
   }

   // ============ Recursion depth ==============

   /**
    * A path has exactly 10 surfaces: two parallel mirrors with kR=1 and emission 10 are hit 10 times.
    * One surface too many gives 110, and one too few gives 90.
    */
   @Test
   void testRecursionDepth() {
      Geometry floor = mirror(Point.ZERO, Vector.AXIS_Z, new Color(10, 10, 10), Double3.ONE);
      Geometry ceiling = mirror(new Point(0, 0, 20), Vector.AXIS_Z, new Color(10, 10, 10), Double3.ONE);
      Color c = trace(new Scene("Depth"), new Ray(new Point(0, 0, 10), new Vector(1, 0, -1)), floor, ceiling);
      assertRgb(100, 100, 100, c);
   }

   // ============ Threshold ==============

   /** The path is cut only if the accumulated coefficient is lower than the threshold in all three channels */
   @Test
   void testThreshold() {
      Color huge = new Color(1e5, 1e5, 1e5);
      // TC01: all channels below 0.001 - the ray is not followed
      assertRgb(0, 0, 0, traceReflection(new Scene("Threshold 1"), new Double3(0.0009), Color.BLACK, huge));
      // TC02: one channel above the threshold - the ray is followed, and each channel gets its own coefficient
      assertRgb(90, 90, 200, traceReflection(new Scene("Threshold 2"), new Double3(0.0009, 0.0009, 0.002),
                                             Color.BLACK, huge));
      // TC03: exactly the threshold is not lower than it - the ray is followed
      assertRgb(100, 100, 100, traceReflection(new Scene("Threshold 3"), new Double3(0.001), Color.BLACK, huge));
   }

   /**
    * The threshold is checked against the accumulated coefficient, not against the coefficient of the last pass:
    * 0.03 * 0.03 = 0.0009 cuts the second reflection, while a check of 0.03 alone would let the ball bring 90.
    */
   @Test
   void testThresholdAccumulates() {
      Geometry mirror1 = mirror(Point.ZERO, Vector.AXIS_Z, Color.BLACK, new Double3(0.03));
      Geometry mirror2 = mirror(new Point(-10, 0, 0), Vector.AXIS_X, Color.BLACK, new Double3(0.03));
      assertRgb(0, 0, 0, trace(new Scene("Accumulated threshold"), viewer, mirror1, mirror2,
                               ball(new Point(0, 0, 20), 1d, new Color(1e5, 1e5, 1e5))));
   }

   /**
    * Helper: the viewer is reflected by three mirrors in a row, and the last reflection meets a ball with a huge
    * emission. The path: the mirror z=0, the mirror x=-10, the mirror z=30 (at (10,0,30)),
    * and the ball at (20,0,19.9).
    * @param kR the reflection coefficient of the three mirrors
    * @return the traced color
    */
   private Color traceThreeMirrors(double kR) {
      return trace(new Scene("Three mirrors"), viewer,
                   mirror(Point.ZERO, Vector.AXIS_Z, Color.BLACK, new Double3(kR)),
                   mirror(new Point(-10, 0, 0), Vector.AXIS_X, Color.BLACK, new Double3(kR)),
                   mirror(new Point(0, 0, 30), Vector.AXIS_Z, Color.BLACK, new Double3(kR)),
                   ball(new Point(20, 0, 19.9), 1d, new Color(1e5, 1e5, 1e5)));
   }

   /**
    * The accumulated coefficient is passed on through all the levels, not only to the next one:
    * with kR=0.09 the third mirror has 0.09^2 = 0.0081 before it, and 0.09^3 = 0.000729 after it,
    * so its reflection is cut. If the third mirror got only the coefficient of the second one (0.09),
    * it would reflect the ball. With kR=0.2 (0.008 after the third mirror) the ball is seen.
    */
   @Test
   void testThresholdAccumulatesDeeper() {
      // TC01: 0.09^3 < 0.001 - cut
      assertRgb(0, 0, 0, traceThreeMirrors(0.09));
      // TC02: 0.2^3 = 0.008 - the ball brings 1e5 * 0.008
      assertRgb(800, 800, 800, traceThreeMirrors(0.2));
   }

   /**
    * The transparency is cut by the accumulated coefficient too: the viewer is reflected by the mirror z=0 (kR),
    * the reflection ray (-1,0,1)/sqrt(2) passes through the transparent plane x=-10 (kT) and meets a ball
    * at (-20,0,20.1) with a huge emission.
    * @param kR the reflection coefficient of the mirror
    * @param kT the transparency coefficient of the plane
    * @return the traced color
    */
   private Color traceMirrorThenGlass(double kR, double kT) {
      Geometry glass = new Plane(new Point(-10, 0, 0), Vector.AXIS_X).setMaterial(new Material().setKT(kT));
      return trace(new Scene("Mirror then glass"), viewer,
                   mirror(Point.ZERO, Vector.AXIS_Z, Color.BLACK, new Double3(kR)), glass,
                   ball(new Point(-20, 0, 20.1), 1d, new Color(1e5, 1e5, 1e5)));
   }

   /** The coefficients of a reflection and of a transparency are multiplied for the threshold */
   @Test
   void testThresholdAccumulatesReflectionAndTransparency() {
      // TC01: 0.03 * 0.03 = 0.0009 < 0.001 - cut (checking kT alone would let the ball bring 90)
      assertRgb(0, 0, 0, traceMirrorThenGlass(0.03, 0.03));
      // TC02: 0.03 * 0.2 = 0.006 - the ball brings 1e5 * 0.006
      assertRgb(600, 600, 600, traceMirrorThenGlass(0.03, 0.2));
   }

   // ============ Ambient light ==============

   /** The ambient light is added once, at the first hit, and not again at the hit of the reflection */
   @Test
   void testAmbientOnlyAtFirstHit() {
      Scene scene = new Scene("Ambient").setAmbientLight(new AmbientLight(new Color(10, 10, 10)));
      // plane: 20 (emission) + 10 (ambient) + 1 * ball (60, without ambient) = 90; an ambient light in every hit gives 100
      assertRgb(90, 90, 90, traceReflection(scene, Double3.ONE, new Color(20, 20, 20), new Color(60, 60, 60)));
   }

   // ============ Tangent ray ==============

   /**
    * A geometry for the tangent tests: a "surface" at one point, hit only by rays that start at (-10,0,0),
    * so the rays that leave it don't hit it again. The geometries of the project don't return a hit for
    * a ray that is tangent to them, so this is the only way to get a tangent hit.
    */
   private static class TangentSurface extends Geometry {
      /** The point of the surface */
      private static final Point POINT = Point.ZERO;
      /** The start of the only rays that hit the surface */
      private static final Point START = new Point(-10, 0, 0);
      /** The normal of the surface */
      private final Vector normal;

      /**
       * @param normal the normal of the surface
       */
      TangentSurface(Vector normal) { this.normal = normal; }

      @Override
      public Vector getNormal(Point point) { return normal; }

      @Override
      protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
         return ray.getHead().equals(START) ? List.of(new Intersection(this, POINT, getMaterial())) : null;
      }
   }

   /**
    * Helper: traces a ray along the x axis, from (-10,0,0), to a surface at the origin with the given normal.
    * Both the reflection ray and the transparency ray meet a ball at (10,0,0.1), whose material has kD=kS=0,
    * so the light can't reach the result through it. For an exactly tangent ray (N·V = 0) the two rays start
    * at the point itself (their head is not shifted); for an almost tangent ray they start shifted by 0.1 along the normal.
    * @param normal    the normal of the surface
    * @param withLight whether a strong point light, above the surface, is added to the scene
    * @return the traced color
    */
   private Color traceTangent(Vector normal, boolean withLight) {
      Scene scene = new Scene("Tangent").setAmbientLight(new AmbientLight(new Color(5, 5, 5)));
      if (withLight)
         scene.lights.add(new PointLight(new Color(1000, 1000, 1000), new Point(0, 0, 5)));
      Geometry surface = new TangentSurface(normal).setEmission(new Color(10, 20, 30))
         .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(20).setKR(0.5).setKT(0.25));
      return trace(scene, new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0)), surface,
                   ball(new Point(10, 0, 0.1), 1d, new Color(100, 100, 100)));
   }

   /**
    * A ray that is exactly tangent to the surface (N·V = 0) keeps the emission, the ambient light and the
    * global effects, and gets no local lighting: the light is on one side of the surface, but N·V = 0 has no
    * side, so the light is rejected (otherwise kD=0.5 would add 500).
    */
   @Test
   void testTangentHit() {
      // emission + ambient + kR * ball + kT * ball = (10,20,30) + 5 + 100*0.5 + 100*0.25
      assertRgb(90, 100, 110, traceTangent(new Vector(0, 0, 1), true));
      // the same result without the light: the light adds nothing, neither through the surface nor through the ball
      assertRgb(90, 100, 110, traceTangent(new Vector(0, 0, 1), false));
   }

   /**
    * A ray that is almost tangent to the surface (N·V = -1e-9, not aligned to zero) has no zero vectors in the
    * reflection and the transparency rays. The scene has no lights, so the local lighting is not part of the result.
    */
   @Test
   void testAlmostTangentHit() {
      Color c = assertDoesNotThrow(() -> traceTangent(new Vector(-1e-9, 0, 1), false),
                                   "An almost tangent hit should not throw an exception");
      // the reflection ray starts above the surface, the transparency ray below it, and both meet the ball
      assertRgb(90, 100, 110, c);
   }

   /**
    * A geometry for the secondary tangent test: a "surface" at one point, hit only by rays that start at a given point.
    */
   private static class PointSurface extends Geometry {
      /** The point of the surface */
      private final Point point;
      /** The start of the only rays that hit the surface */
      private final Point start;
      /** The normal of the surface */
      private final Vector normal;

      /**
       * @param point  the point of the surface
       * @param start  the start of the only rays that hit the surface
       * @param normal the normal of the surface
       */
      PointSurface(Point point, Point start, Vector normal) {
         this.point = point;
         this.start = start;
         this.normal = normal;
      }

      @Override
      public Vector getNormal(Point p) { return normal; }

      @Override
      protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
         return ray.getHead().equals(start) ? List.of(new Intersection(this, point, getMaterial())) : null;
      }
   }

   /**
    * A secondary ray that hits a surface tangentially (N·V = 0) keeps the emission of that surface, as the primary
    * tangent hit does: the data of the hit is prepared completely, and the color is not replaced by black.
    * The primary ray (from (-10,0,0) along the x axis) hits the first surface at the origin tangentially,
    * and its reflection ray (not shifted, since the hit is tangent) starts at the origin and hits the second
    * surface at (5,0,0), tangentially again. There are no lights and no ambient light.
    */
   @Test
   void testSecondaryTangentHit() {
      Vector normal = new Vector(0, 0, 1);
      Geometry first = new PointSurface(Point.ZERO, new Point(-10, 0, 0), normal)
         .setEmission(new Color(10, 0, 0)).setMaterial(new Material().setKR(0.5));
      Geometry second = new PointSurface(new Point(5, 0, 0), Point.ZERO, normal)
         .setEmission(new Color(0, 60, 0)).setMaterial(new Material());
      Color c = trace(new Scene("Secondary tangent"), new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0)), first, second);
      // emission of the first surface + kR * emission of the second surface = (10,0,0) + 0.5 * (0,60,0)
      assertRgb(10, 30, 0, c);
   }
}
