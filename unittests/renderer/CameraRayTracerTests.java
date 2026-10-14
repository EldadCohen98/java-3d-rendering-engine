package renderer;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import primitives.*;
import scene.Scene;

/**
 * Tests for {@link Camera.Builder#setRayTracer(Scene, RayTracerType)}:
 * SIMPLE uses the given scene, an unsupported type (GRID) and null input are rejected,
 * and a rejected call keeps the ray tracer that was set before.
 * The scenes are empty, so the rendered pixel gets the scene background color.
 */
class CameraRayTracerTests {
   /** Default constructor to satisfy JavaDoc generator */
   CameraRayTracerTests() { /* to satisfy JavaDoc generator */ }

   /** A unique background color for the given scene */
   private static final java.awt.Color BACKGROUND = new java.awt.Color(17, 34, 51);

   /**
    * Helper: an empty scene with the unique background color
    * @return the scene
    */
   private Scene scene() {
      return new Scene("Ray tracer test").setBackground(new Color(BACKGROUND));
   }

   /**
    * Helper: a builder with everything set except the ray tracer, 1x1 resolution
    * @return the builder
    */
   private Camera.Builder builder() {
      return Camera.getBuilder()
         .setLocation(Point.ZERO)
         .setDirection(new Vector(0, 0, -1), Vector.AXIS_Y)
         .setVpDistance(1)
         .setVpSize(1, 1)
         .setResolution(1, 1);
   }

   /**
    * Helper: renders a camera and returns the color of its single pixel
    * (the pixel buffer is read by reflection, no API is added and no image is written)
    * @param camera the camera
    * @return the RGB value of the pixel
    * @throws ReflectiveOperationException if the fields cannot be read
    */
   private int renderedPixel(Camera camera) throws ReflectiveOperationException {
      camera.renderImage();
      Field writerField = Camera.class.getDeclaredField("imageWriter");
      writerField.setAccessible(true);
      Field imageField = ImageWriter.class.getDeclaredField("image");
      imageField.setAccessible(true);
      return ((BufferedImage) imageField.get(writerField.get(camera))).getRGB(0, 0);
   }

   /** TC01: SIMPLE uses the given scene - the pixel gets its background color */
   @Test
   void testSimpleUsesScene() throws ReflectiveOperationException {
      Camera camera = builder().setRayTracer(scene(), RayTracerType.SIMPLE).build();
      assertEquals(BACKGROUND.getRGB(), renderedPixel(camera), "The given scene should be rendered");
   }

   /** TC02: GRID is not implemented - rejected already in setRayTracer with a clear message */
   @Test
   void testGridRejected() {
      UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class,
                                                     () -> builder().setRayTracer(scene(), RayTracerType.GRID),
                                                     "GRID should be rejected in setRayTracer");
      assertNotNull(e.getMessage(), "The exception should have a message");
      assertTrue(e.getMessage().contains("GRID"), "The message should name the unsupported type");
   }

   /** TC03: a null scene is rejected */
   @Test
   void testNullSceneRejected() {
      assertThrows(IllegalArgumentException.class, () -> builder().setRayTracer(null, RayTracerType.SIMPLE),
                   "A null scene should be rejected");
   }

   /** TC04: a null type is rejected */
   @Test
   void testNullTypeRejected() {
      assertThrows(IllegalArgumentException.class, () -> builder().setRayTracer(scene(), null),
                   "A null ray tracer type should be rejected");
   }

   /** TC05: a rejected call keeps the valid ray tracer that was set before */
   @Test
   void testRejectedCallKeepsTracer() throws ReflectiveOperationException {
      Camera.Builder builder = builder().setRayTracer(scene(), RayTracerType.SIMPLE);
      Scene other = new Scene("Other scene").setBackground(new Color(200, 0, 0));

      assertThrows(UnsupportedOperationException.class, () -> builder.setRayTracer(other, RayTracerType.GRID));
      assertThrows(IllegalArgumentException.class, () -> builder.setRayTracer(null, RayTracerType.SIMPLE));
      assertThrows(IllegalArgumentException.class, () -> builder.setRayTracer(other, null));

      assertEquals(BACKGROUND.getRGB(), renderedPixel(builder.build()),
                   "The ray tracer that was set before the rejected calls should be kept");
   }
}
