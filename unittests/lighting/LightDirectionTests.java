package lighting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import primitives.*;

/**
 * Unit tests for the light direction convention of {@link LightSource#getL(Point)}:
 * a normalized vector from the light toward the lit point.
 */
class LightDirectionTests {
   /** Default constructor to satisfy JavaDoc generator */
   LightDirectionTests() { /* to satisfy JavaDoc generator */ }

   /** Delta for comparing doubles */
   private static final double DELTA = 1e-10;
   /** Light intensity used in the tests */
   private final Color         white = new Color(100, 100, 100);

   /**
    * Helper: asserts that a vector equals the expected (x,y,z) and is normalized
    * @param x      expected x
    * @param y      expected y
    * @param z      expected z
    * @param actual the vector to check
    */
   private void assertVector(double x, double y, double z, Vector actual) {
      assertEquals(1, actual.length(), DELTA, "getL must be normalized");
      assertEquals(x, actual.dotProduct(Vector.AXIS_X), DELTA, "Wrong x component");
      assertEquals(y, actual.dotProduct(Vector.AXIS_Y), DELTA, "Wrong y component");
      assertEquals(z, actual.dotProduct(Vector.AXIS_Z), DELTA, "Wrong z component");
   }

   /** Test method for {@link PointLight#getL(Point)} */
   @Test
   void testPointLightGetL() {
      PointLight light = new PointLight(white, new Point(0, 0, 5));
      // TC01: point straight below the light - L points down
      assertVector(0, 0, -1, light.getL(Point.ZERO));
      // TC02: point at distance 5 in direction (3,4,0)/5 from the light
      assertVector(0.6, 0.8, 0, light.getL(new Point(3, 4, 5)));
   }

   /** Test method for {@link SpotLight#getL(Point)} */
   @Test
   void testSpotLightGetL() {
      SpotLight light = new SpotLight(white, new Point(0, 0, 5), new Vector(0, 0, -1));
      // TC01: point straight below the light - L points down
      assertVector(0, 0, -1, light.getL(Point.ZERO));
      // TC02: point at distance 5 in direction (3,4,0)/5 from the light
      assertVector(0.6, 0.8, 0, light.getL(new Point(3, 4, 5)));
   }

   /** Test method for {@link DirectionalLight#getL(Point)} */
   @Test
   void testDirectionalLightGetL() {
      // TC01: light travelling down - L is the travel direction, not inverted
      assertVector(0, 0, -1, new DirectionalLight(white, new Vector(0, 0, -2)).getL(Point.ZERO));
      // TC02: non-axis direction, normalized
      double h = Math.sqrt(0.5);
      assertVector(h, h, 0, new DirectionalLight(white, new Vector(1, 1, 0)).getL(new Point(7, -3, 2)));
   }

   /** Test method for {@link SpotLight#getIntensity(Point)} */
   @Test
   void testSpotLightIntensity() {
      Point position = new Point(0, 0, 5);
      // TC01: point on the spot axis - full intensity (no attenuation by default)
      assertEquals(100, new SpotLight(white, position, new Vector(0, 0, -1)).getIntensity(Point.ZERO).getColor().getRed(),
                   "Point on the spot axis should get full intensity");
      // TC02: point at 45 degrees from the axis, narrow beam 2 - intensity * cos^2(45) = 50,
      // attenuation is 1 since kC=1, kL=kQ=0
      int red = new SpotLight(white, position, new Vector(0, 0, -1)).setNarrowBeam(2)
         .getIntensity(new Point(5, 0, 0)).getColor().getRed();
      assertEquals(50, red, 1, "Wrong narrow beam intensity at 45 degrees");
      // TC03: point behind the spot - no light
      assertEquals(0, new SpotLight(white, position, new Vector(0, 0, 1)).getIntensity(Point.ZERO).getColor().getRed(),
                   "Point behind the spot should get no light");
   }
}
