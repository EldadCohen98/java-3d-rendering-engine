package renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.Plane;
import lighting.AmbientLight;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for the ambient light and emission contribution in {@link SimpleRayTracer}.
 * The scenes contain no local light sources, so the resulting color is
 * {@code emission + ambient * kA} only.
 */
class AmbientEmissionTests {
   /** Default constructor to satisfy JavaDoc generator */
   AmbientEmissionTests() { /* to satisfy JavaDoc generator */ }

   /** Ray from above hitting the plane z=0 at the origin */
   private final Ray hitRay  = new Ray(new Point(0, 0, 10), new Vector(0, 0, -1));
   /** Ray going away from the plane z=0 - no intersection */
   private final Ray missRay = new Ray(new Point(0, 0, 10), new Vector(0, 0, 1));

   /**
    * Helper: builds a scene with a single plane z=0 and traces the hit ray
    * @param ambient  the ambient light intensity
    * @param kA       the ambient attenuation factor of the plane material
    * @param emission the emission color of the plane
    * @return the traced color
    */
   private Color trace(Color ambient, Double3 kA, Color emission) {
      Scene scene = new Scene("Ambient test").setAmbientLight(new AmbientLight(ambient));
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z)
         .setEmission(emission)
         .setMaterial(new Material().setkA(kA)));
      return new SimpleRayTracer(scene).traceRay(hitRay);
   }

   /**
    * Helper: asserts the RGB components of a color
    * @param r      expected red
    * @param g      expected green
    * @param b      expected blue
    * @param actual the color to check
    */
   private void assertRgb(int r, int g, int b, Color actual) {
      java.awt.Color c = actual.getColor();
      assertEquals(r, c.getRed(), "Wrong red component");
      assertEquals(g, c.getGreen(), "Wrong green component");
      assertEquals(b, c.getBlue(), "Wrong blue component");
   }

   /** TC01: kA = 0 - only the emission remains */
   @Test
   void testKaZero() {
      assertRgb(50, 0, 0, trace(new Color(100, 100, 100), new Double3(0), new Color(50, 0, 0)));
   }

   /** TC02: kA = 1 - full ambient plus emission */
   @Test
   void testKaOne() {
      assertRgb(150, 100, 100, trace(new Color(100, 100, 100), new Double3(1), new Color(50, 0, 0)));
   }

   /** TC03: kA = 0.5 - half ambient plus emission */
   @Test
   void testKaHalf() {
      assertRgb(100, 50, 50, trace(new Color(100, 100, 100), new Double3(0.5), new Color(50, 0, 0)));
   }

   /** TC04: kA differs per component */
   @Test
   void testKaPerComponent() {
      assertRgb(30, 60, 90, trace(new Color(100, 80, 60), new Double3(0.2, 0.5, 1), new Color(10, 20, 30)));
   }

   /** TC05: a ray with no intersection - background color */
   @Test
   void testBackground() {
      Scene scene = new Scene("Background test")
         .setBackground(new Color(12, 34, 56))
         .setAmbientLight(new AmbientLight(new Color(100, 100, 100)));
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setEmission(new Color(50, 0, 0)));

      assertRgb(12, 34, 56, new SimpleRayTracer(scene).traceRay(missRay));
   }
}
