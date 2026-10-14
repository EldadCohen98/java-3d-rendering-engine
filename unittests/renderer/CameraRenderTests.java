package renderer;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometries.Plane;
import lighting.PointLight;
import primitives.*;
import scene.Scene;

/**
 * Regression tests for {@link Camera#renderImage()} with rectangular (wide and tall) and square images.
 * Scene: the camera at the origin looks towards -z with up = +y, the view plane is at distance 1
 * and its size equals the resolution, so every pixel is 1x1. The plane z=-1 (kD=1) is lit by a point
 * light at (0.3,0.2,-0.5) of intensity 100 with no attenuation, so a pixel whose ray hits (x,y,-1)
 * gets the gray level 100 * cos(angle) = 100 * 0.5 / d, where d is the distance to the light.
 * The light is off-center, so the colors differ from pixel to pixel and a swap of rows and
 * columns is detected.
 */
class CameraRenderTests {
   /** Default constructor to satisfy JavaDoc generator */
   CameraRenderTests() { /* to satisfy JavaDoc generator */ }

   /** Light position */
   private static final Point LIGHT = new Point(0.3, 0.2, -0.5);

   /**
    * Helper: renders the test scene with a new camera builder at the given resolution
    * @param nX number of columns
    * @param nY number of rows
    * @return the rendered image
    * @throws ReflectiveOperationException if the image cannot be read from the image writer
    */
   private BufferedImage render(int nX, int nY) throws ReflectiveOperationException {
      Scene scene = new Scene("Camera render test");
      scene.geometries.add(new Plane(new Point(0, 0, -1), Vector.AXIS_Z).setMaterial(new Material().setkD(1)));
      scene.lights.add(new PointLight(new Color(100, 100, 100), LIGHT));

      ImageWriter imageWriter = new ImageWriter(nX, nY);
      Camera.getBuilder()
         .setLocation(Point.ZERO)
         .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
         .setVpDistance(1)
         .setVpSize(nX, nY)
         .setResolution(nX, nY)
         .setRayTracer(scene, RayTracerType.SIMPLE)
         .setImageWriter(imageWriter)
         .build()
         .renderImage();

      // read the rendered pixels without writing a file
      Field image = ImageWriter.class.getDeclaredField("image");
      image.setAccessible(true);
      return (BufferedImage) image.get(imageWriter);
   }

   /**
    * Helper: the expected gray level of a pixel, computed from the scene geometry
    * @param nX number of columns
    * @param nY number of rows
    * @param column pixel column (0 = left)
    * @param row    pixel row (0 = top)
    * @return the expected gray level of the pixel
    */
   private double expectedGray(int nX, int nY, int column, int row) {
      // center of the pixel on the view plane = the hit point on the plane z=-1
      double x = column - (nX - 1) / 2.0;
      double y = (nY - 1) / 2.0 - row;
      double d = new Point(x, y, -1).distance(LIGHT);
      return 100 * 0.5 / d;
   }

   /**
    * Helper: renders an image and checks the size and every pixel
    * @param nX number of columns
    * @param nY number of rows
    * @throws ReflectiveOperationException if the image cannot be read from the image writer
    */
   private void assertImage(int nX, int nY) throws ReflectiveOperationException {
      BufferedImage image = assertDoesNotThrow(() -> render(nX, nY), "Rendering must not throw");
      assertEquals(nX, image.getWidth(), "Wrong image width");
      assertEquals(nY, image.getHeight(), "Wrong image height");

      Set<Integer> levels = new HashSet<>();
      for (int row = 0; row < nY; ++row)
         for (int column = 0; column < nX; ++column) {
            java.awt.Color c = new java.awt.Color(image.getRGB(column, row));
            // the conversion to java.awt.Color truncates the components
            int expected = (int) expectedGray(nX, nY, column, row);
            String where = " at column " + column + ", row " + row;
            assertEquals(expected, c.getRed(), "Wrong red" + where);
            assertEquals(expected, c.getGreen(), "Wrong green" + where);
            assertEquals(expected, c.getBlue(), "Wrong blue" + where);
            levels.add(expected);
         }
      // sanity: all the pixels differ, so a misplaced pixel cannot go unnoticed
      assertEquals(nX * nY, levels.size(), "The test scene should give every pixel a different color");
   }

   /** TC01: wide image (4 columns, 2 rows) */
   @Test
   void testWideImage() throws ReflectiveOperationException {
      assertImage(4, 2);
   }

   /** TC02: tall image (2 columns, 4 rows) */
   @Test
   void testTallImage() throws ReflectiveOperationException {
      assertImage(2, 4);
   }

   /** TC03: square image (3 columns, 3 rows) */
   @Test
   void testSquareImage() throws ReflectiveOperationException {
      assertImage(3, 3);
   }

   /**
    * Helper: finds the brightest pixel of an image
    * @param image the image
    * @return {column, row} of the brightest pixel
    */
   private int[] brightest(BufferedImage image) {
      int[] best = { 0, 0 };
      int bestRed = -1;
      for (int row = 0; row < image.getHeight(); ++row)
         for (int column = 0; column < image.getWidth(); ++column) {
            int red = new java.awt.Color(image.getRGB(column, row)).getRed();
            if (red > bestRed) {
               bestRed = red;
               best = new int[] { column, row };
            }
         }
      return best;
   }

   /**
    * TC04: the brightest pixel is the one whose hit point (0.5,0.5,-1) is closest to the light:
    * wide image (4x2) - column 2, row 0; tall image (2x4) - column 1, row 1
    */
   @Test
   void testBrightestPixel() throws ReflectiveOperationException {
      assertArrayEquals(new int[] { 2, 0 }, brightest(render(4, 2)), "Wide image: wrong brightest pixel");
      assertArrayEquals(new int[] { 1, 1 }, brightest(render(2, 4)), "Tall image: wrong brightest pixel");
   }
}
