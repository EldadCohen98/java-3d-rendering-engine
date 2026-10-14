package renderer;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import primitives.*;
import scene.Scene;

/**
 * Regression tests for reusing a {@link Camera.Builder}: every camera built with an automatically
 * created {@link ImageWriter} must get its own pixel buffer matching its resolution, while an
 * explicitly given image writer is used as is.
 * The scenes are empty, so every rendered pixel gets the scene background color.
 */
class CameraBuilderReuseTests {
   /** Default constructor to satisfy JavaDoc generator */
   CameraBuilderReuseTests() { /* to satisfy JavaDoc generator */ }

   /** Background color of the first scene */
   private static final java.awt.Color RED  = new java.awt.Color(200, 0, 0);
   /** Background color of the second scene */
   private static final java.awt.Color BLUE = new java.awt.Color(0, 0, 200);

   /**
    * Helper: an empty scene with the given background
    * @param background the background color
    * @return the scene
    */
   private Scene scene(java.awt.Color background) {
      return new Scene("Builder reuse test").setBackground(new Color(background));
   }

   /**
    * Helper: a new builder with everything set except the resolution
    * @param background the background color of the rendered scene
    * @return the builder
    */
   private Camera.Builder builder(java.awt.Color background) {
      return Camera.getBuilder()
         .setLocation(Point.ZERO)
         .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
         .setVpDistance(1)
         .setVpSize(1, 1)
         .setRayTracer(scene(background), RayTracerType.SIMPLE);
   }

   /**
    * Helper: the image writer of a camera (read by reflection, no API is added for the tests)
    * @param camera the camera
    * @return its image writer
    * @throws ReflectiveOperationException if the field cannot be read
    */
   private ImageWriter writerOf(Camera camera) throws ReflectiveOperationException {
      Field field = Camera.class.getDeclaredField("imageWriter");
      field.setAccessible(true);
      return (ImageWriter) field.get(camera);
   }

   /**
    * Helper: the pixel buffer of an image writer (read by reflection)
    * @param writer the image writer
    * @return its image
    * @throws ReflectiveOperationException if the field cannot be read
    */
   private BufferedImage imageOf(ImageWriter writer) throws ReflectiveOperationException {
      Field field = ImageWriter.class.getDeclaredField("image");
      field.setAccessible(true);
      return (BufferedImage) field.get(writer);
   }

   /**
    * Helper: asserts the size of an image and that all of its pixels have the given color
    * @param nX     expected width
    * @param nY     expected height
    * @param color  expected color of every pixel
    * @param image  the image to check
    */
   private void assertImage(int nX, int nY, java.awt.Color color, BufferedImage image) {
      assertEquals(nX, image.getWidth(), "Wrong pixel buffer width");
      assertEquals(nY, image.getHeight(), "Wrong pixel buffer height");
      for (int y = 0; y < nY; ++y)
         for (int x = 0; x < nX; ++x)
            assertEquals(color.getRGB(), image.getRGB(x, y), "Wrong color at (" + x + "," + y + ")");
   }

   /** TC01: a small camera and then a large one from the same builder - both render at their own size */
   @Test
   void testSmallThenLarge() throws ReflectiveOperationException {
      Camera.Builder builder = builder(RED);
      Camera small = builder.setResolution(2, 2).build();
      Camera large = builder.setResolution(4, 4).build();

      assertDoesNotThrow(small::renderImage, "Rendering the small camera must not throw");
      assertDoesNotThrow(large::renderImage, "Rendering the large camera must not throw");
      assertImage(2, 2, RED, imageOf(writerOf(small)));
      assertImage(4, 4, RED, imageOf(writerOf(large)));
   }

   /** TC02: a large camera and then a small one from the same builder - the pixel buffer fits each */
   @Test
   void testLargeThenSmall() throws ReflectiveOperationException {
      Camera.Builder builder = builder(RED);
      Camera large = builder.setResolution(4, 4).build();
      Camera small = builder.setResolution(2, 2).build();

      ImageWriter smallWriter = writerOf(small);
      assertEquals(2, smallWriter.nX(), "Wrong image writer width");
      assertEquals(2, smallWriter.nY(), "Wrong image writer height");

      large.renderImage();
      small.renderImage();
      assertImage(4, 4, RED, imageOf(writerOf(large)));
      assertImage(2, 2, RED, imageOf(smallWriter));
   }

   /** TC03: two cameras with the same resolution from the same builder get independent pixel buffers */
   @Test
   void testIndependentBuffers() throws ReflectiveOperationException {
      Camera.Builder builder = builder(RED).setResolution(3, 3);
      Camera first  = builder.build();
      Camera second = builder.build();

      assertNotSame(writerOf(first), writerOf(second), "Each camera should get its own image writer");
      assertNotSame(imageOf(writerOf(first)), imageOf(writerOf(second)), "Each camera should get its own pixel buffer");
   }

   /** TC04: rendering the second camera in another color does not change the pixels of the first one */
   @Test
   void testSecondRenderKeepsFirstPixels() throws ReflectiveOperationException {
      Camera.Builder builder = builder(RED).setResolution(3, 3);
      Camera first = builder.build().renderImage();
      Camera second = builder.setRayTracer(scene(BLUE), RayTracerType.SIMPLE).build().renderImage();

      assertImage(3, 3, RED, imageOf(writerOf(first)));
      assertImage(3, 3, BLUE, imageOf(writerOf(second)));
   }

   /** TC05: changing the builder after build does not change the resolution or directions of the built camera */
   @Test
   void testBuilderChangesAfterBuild() throws ReflectiveOperationException {
      Camera.Builder builder = builder(RED).setResolution(2, 2);
      Camera camera = builder.build();

      builder.setResolution(4, 4)
         .setLocation(new Point(5, 5, 5))
         .setDirection(new Vector(1, 0, 0), Vector.AXIS_Z)
         .build();

      assertEquals(new Vector(0, 0, -1), camera.getVTo(), "vTo of the built camera changed");
      assertEquals(Vector.AXIS_Y, camera.getVUp(), "vUp of the built camera changed");
      assertEquals(Vector.AXIS_X, camera.getVRight(), "vRight of the built camera changed");
      assertDoesNotThrow(camera::renderImage, "Rendering the built camera must not throw");
      assertImage(2, 2, RED, imageOf(writerOf(camera)));
      assertEquals(new Ray(Point.ZERO, new Vector(0, 0, -1)), camera.constructRay(1, 1, 0, 0),
                   "The location of the built camera changed");
   }

   /** TC06: an explicitly given image writer that fits the resolution is used as is */
   @Test
   void testExplicitImageWriterUsed() throws ReflectiveOperationException {
      ImageWriter writer = new ImageWriter(3, 2);
      Camera camera = builder(RED).setResolution(3, 2).setImageWriter(writer).build().renderImage();

      assertSame(writer, writerOf(camera), "The camera should use the given image writer");
      assertImage(3, 2, RED, imageOf(writer));
   }

   /** TC07: the same explicit image writer given to several cameras is shared (documented behavior) */
   @Test
   void testExplicitImageWriterShared() throws ReflectiveOperationException {
      ImageWriter writer = new ImageWriter(2, 2);
      Camera.Builder builder = builder(RED).setResolution(2, 2).setImageWriter(writer);
      Camera first  = builder.build();
      Camera second = builder.build();

      assertSame(writer, writerOf(first), "The first camera should use the given image writer");
      assertSame(writer, writerOf(second), "The second camera should use the given image writer");
   }

   /** TC08: an explicit image writer whose size does not fit the resolution is rejected at build time */
   @Test
   void testExplicitImageWriterMismatch() {
      // TC08.1: wrong width and height
      assertThrows(IllegalArgumentException.class,
                   () -> builder(RED).setResolution(4, 4).setImageWriter(new ImageWriter(2, 2)).build(),
                   "A mismatched image writer should be rejected in build()");
      // TC08.2: the size fits after transposing only - rows and columns are not interchangeable
      assertThrows(IllegalArgumentException.class,
                   () -> builder(RED).setResolution(4, 2).setImageWriter(new ImageWriter(2, 4)).build(),
                   "A transposed image writer should be rejected in build()");
   }
}
