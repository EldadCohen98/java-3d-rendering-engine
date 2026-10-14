package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for the choice of the closest intersection in {@link SimpleRayTracer#traceRay(Ray)}.
 * The scenes have no lights and no ambient light, so the traced color is the emission of the geometry
 * that the ray hits first.
 */
class ClosestIntersectionTests {
   /** Default constructor to satisfy JavaDoc generator */
   ClosestIntersectionTests() { /* to satisfy JavaDoc generator */ }

   /** Ray from above going down along the z axis */
   private final Ray down = new Ray(new Point(0, 0, 10), new Vector(0, 0, -1));

   /** Red plane z=0 */
   private Geometry red()   { return new Plane(Point.ZERO, Vector.AXIS_Z).setEmission(new Color(100, 0, 0)); }
   /** Green plane z=-5 */
   private Geometry green() { return new Plane(new Point(0, 0, -5), Vector.AXIS_Z).setEmission(new Color(0, 100, 0)); }
   /** Blue sphere around the origin, with radius 1 - the ray crosses it at z=1 and at z=-1 */
   private Geometry blue()  { return new Sphere(Point.ZERO, 1d).setEmission(new Color(0, 0, 100)); }

   /**
    * Helper: traces the ray in a scene with the given geometries
    * @param geometries the geometries of the scene, in this order
    * @return the traced color
    */
   private java.awt.Color trace(Geometry... geometries) {
      Scene scene = new Scene("Closest intersection test");
      scene.geometries.add(geometries);
      return new SimpleRayTracer(scene).traceRay(down).getColor();
   }

   /**
    * Helper: asserts the RGB components of a color
    * @param r      expected red
    * @param g      expected green
    * @param b      expected blue
    * @param actual the color to check
    */
   private void assertRgb(int r, int g, int b, java.awt.Color actual) {
      assertEquals(r, actual.getRed(), "Wrong red component");
      assertEquals(g, actual.getGreen(), "Wrong green component");
      assertEquals(b, actual.getBlue(), "Wrong blue component");
   }

   /** The nearest of two geometries is chosen, whatever their order in the scene */
   @Test
   void testNearestOfTwoGeometries() {
      // TC01: the nearer geometry is added first
      assertRgb(100, 0, 0, trace(red(), green()));
      // TC02: the nearer geometry is added last
      assertRgb(100, 0, 0, trace(green(), red()));
   }

   /** A geometry with two intersections (entry and exit): the entry point is the closest one */
   @Test
   void testNearestOfSeveralIntersections() {
      // the red plane z=0 is inside the sphere, between its entry point (z=1) and its exit point (z=-1)
      // TC01: the sphere is added first
      assertRgb(0, 0, 100, trace(blue(), red()));
      // TC02: the sphere is added last
      assertRgb(0, 0, 100, trace(red(), blue()));
      // TC03: all three geometries
      assertRgb(0, 0, 100, trace(green(), red(), blue()));
   }

   /** A ray that meets no geometry gets the background color, even if the scene is not empty */
   @Test
   void testNoIntersection() {
      Scene scene = new Scene("No intersection test").setBackground(new Color(12, 34, 56));
      scene.geometries.add(red(), blue());
      java.awt.Color c = new SimpleRayTracer(scene).traceRay(new Ray(new Point(0, 0, 10), new Vector(0, 0, 1))).getColor();
      assertRgb(12, 34, 56, c);
   }
}
