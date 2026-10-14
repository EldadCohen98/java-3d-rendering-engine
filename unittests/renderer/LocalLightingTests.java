package renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.Plane;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for the local lighting (diffuse and specular) in {@link SimpleRayTracer}.
 * All scenes use the plane z=0, no ambient light and black emission, so the result is the
 * local lighting only. Expected values are derived from the scene geometry
 * (angles between the light, the surface and the viewer).
 */
class LocalLightingTests {
   /** Default constructor to satisfy JavaDoc generator */
   LocalLightingTests() { /* to satisfy JavaDoc generator */ }

   /** Light intensity used in the tests */
   private final Color white      = new Color(100, 100, 100);
   /** Viewer above the plane looking straight down at the origin */
   private final Ray   fromAbove  = new Ray(new Point(0, 0, 10), new Vector(0, 0, -1));
   /** Viewer below the plane looking straight up at the origin */
   private final Ray   fromBelow  = new Ray(new Point(0, 0, -10), new Vector(0, 0, 1));
   /** Purely diffuse material */
   private final Material diffuse  = new Material().setkD(1).setkS(0).setShininess(20);

   /**
    * Helper: traces a ray in a scene with the plane z=0 and one light source
    * @param material the plane material
    * @param light    the light source
    * @param ray      the viewing ray
    * @return the traced color
    */
   private Color trace(Material material, LightSource light, Ray ray) {
      Scene scene = new Scene("Local lighting test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setMaterial(material));
      scene.lights.add(light);
      return new SimpleRayTracer(scene).traceRay(ray);
   }

   /**
    * Helper: asserts the RGB components of a gray color within 1 unit
    * (the conversion to java.awt.Color truncates the components)
    * @param expected expected value of each component
    * @param actual   the color to check
    */
   static void assertGray(double expected, Color actual) {
      java.awt.Color c = actual.getColor();
      assertEquals(expected, c.getRed(), 1, "Wrong red component");
      assertEquals(expected, c.getGreen(), 1, "Wrong green component");
      assertEquals(expected, c.getBlue(), 1, "Wrong blue component");
   }

   // ============ Diffuse ==============

   /** Light from the viewer's side, hitting the surface head on: full intensity */
   @Test
   void testDiffuseFrontLight() {
      // TC01: directional light travelling down
      assertGray(100, trace(diffuse, new DirectionalLight(white, new Vector(0, 0, -1)), fromAbove));
      // TC02: point light above the plane
      assertGray(100, trace(diffuse, new PointLight(white, new Point(0, 0, 5)), fromAbove));
      // TC03: spotlight above the plane pointing down
      assertGray(100, trace(diffuse, new SpotLight(white, new Point(0, 0, 5), new Vector(0, 0, -1)), fromAbove));
      // TC04: viewer and light both below the plane - the normal orientation must not matter
      assertGray(100, trace(diffuse, new PointLight(white, new Point(0, 0, -5)), fromBelow));
   }

   /** Light at 45 degrees to the normal: intensity * cos(45) */
   @Test
   void testDiffuseAngle() {
      assertGray(100 * Math.sqrt(0.5), trace(diffuse, new DirectionalLight(white, new Vector(1, 0, -1)), fromAbove));
   }

   /** Light on the opposite side of the surface from the viewer: no local lighting */
   @Test
   void testDiffuseBackLight() {
      // TC01: directional light travelling up from below
      assertGray(0, trace(diffuse, new DirectionalLight(white, new Vector(0, 0, 1)), fromAbove));
      // TC02: point light below the plane
      assertGray(0, trace(diffuse, new PointLight(white, new Point(0, 0, -5)), fromAbove));
      // TC03: spotlight below the plane pointing up
      assertGray(0, trace(diffuse, new SpotLight(white, new Point(0, 0, -5), new Vector(0, 0, 1)), fromAbove));
   }

   /** Spotlight on the viewer's side but pointing away from the surface: no lighting */
   @Test
   void testSpotPointingAway() {
      assertGray(0, trace(diffuse, new SpotLight(white, new Point(0, 0, 5), new Vector(0, 0, 1)), fromAbove));
   }

   // ============ Specular ==============

   /** Purely specular material */
   private final Material shiny = new Material().setkD(0).setkS(1).setShininess(2);

   /** Light reflected straight back to the viewer: full intensity */
   @Test
   void testSpecularDirectReflection() {
      // TC01: directional light travelling down, reflected straight up into the viewer
      assertGray(100, trace(shiny, new DirectionalLight(white, new Vector(0, 0, -1)), fromAbove));
      // TC02: point light right above the hit point, same reflection
      assertGray(100, trace(shiny, new PointLight(white, new Point(0, 0, 5)), fromAbove));
   }

   /**
    * Light at 45 degrees: the reflection leaves at 45 degrees from the normal while the
    * viewer is on the normal, so the angle between them is 45 degrees: cos^2(45) = 0.5
    */
   @Test
   void testSpecularAngle() {
      assertGray(50, trace(shiny, new DirectionalLight(white, new Vector(1, 0, -1)), fromAbove));
   }

   /** Reflection leaving away from the viewer (angle above 90 degrees): no highlight */
   @Test
   void testSpecularAwayFromViewer() {
      // the viewer looks from (-10,0,10), the light comes in from the -x side and reflects towards +x
      Ray viewer = new Ray(new Point(-10, 0, 10), new Vector(1, 0, -1));
      assertGray(0, trace(shiny, new DirectionalLight(white, new Vector(2, 0, -1)), viewer));
   }

   /** Light on the opposite side of the surface from the viewer: no highlight */
   @Test
   void testSpecularBackLight() {
      assertGray(0, trace(shiny, new DirectionalLight(white, new Vector(0, 0, 1)), fromAbove));
   }

   // ============ Grazing light ==============

   /** Light parallel to the surface: no exception and no local lighting */
   @Test
   void testGrazingLight() {
      Material both = new Material().setkD(1).setkS(1).setShininess(2);
      // TC01: directional light parallel to the plane
      assertGray(0, assertDoesNotThrow(() -> trace(both, new DirectionalLight(white, new Vector(1, 0, 0)), fromAbove)));
      // TC02: point light lying in the plane
      assertGray(0, assertDoesNotThrow(() -> trace(both, new PointLight(white, new Point(5, 0, 0)), fromAbove)));
   }
}
