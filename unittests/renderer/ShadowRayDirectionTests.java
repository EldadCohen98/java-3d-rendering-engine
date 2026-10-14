package renderer;

import static renderer.LocalLightingTests.assertGray;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Regression tests for the direction of the shadow ray in {@link SimpleRayTracer}:
 * after the shadow ray head is moved off the surface, the ray must still aim exactly
 * at the light source position (point light and spotlight lighting at an angle).
 * Scene: plane z=0 (kD=1, kS=0), no ambient light, no emission, a light at (5,0,5)
 * lighting the origin at 45 degrees, so the unblocked color is 100 * cos(45) = 70.71.
 */
class ShadowRayDirectionTests {
   /** Default constructor to satisfy JavaDoc generator */
   ShadowRayDirectionTests() { /* to satisfy JavaDoc generator */ }

   /** Light intensity used in the tests */
   private final Color white         = new Color(100, 100, 100);
   /** Light position */
   private final Point lightPosition = new Point(5, 0, 5);
   /** Viewer looking at the origin from the -y side, away from the blockers */
   private final Ray   viewer        = new Ray(new Point(0, -10, 10), new Vector(0, 1, -1));
   /** Expected color component of the lit point: 100 * cos(45) */
   private static final double LIT  = 100 * Math.sqrt(0.5);

   /** @return a blocker lying on the segment between the shifted shadow ray head and the light */
   private Sphere onLightPath() { return new Sphere(new Point(4.9, 0, 4.9), 0.04); }

   /** @return a sphere next to the light path, which does not block the light */
   private Sphere offLightPath() { return new Sphere(new Point(4.9, 0, 5.0), 0.04); }

   /** @return a point light at the light position */
   private LightSource pointLight() { return new PointLight(white, lightPosition); }

   /** @return a spotlight at the light position aimed at the origin */
   private LightSource spotLight() { return new SpotLight(white, lightPosition, new Vector(-1, 0, -1)); }

   /**
    * Helper: traces the viewer ray in a scene with the plane z=0, one light and extra geometries
    * @param light    the light source
    * @param blockers additional geometries
    * @return the traced color at the origin
    */
   private Color trace(LightSource light, Intersectable... blockers) {
      Scene scene = new Scene("Shadow direction test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setMaterial(new Material().setkD(1).setkS(0)));
      scene.geometries.add(blockers);
      scene.lights.add(light);
      return new SimpleRayTracer(scene).traceRay(viewer);
   }

   /** Sanity: the viewing ray hits the plane at the origin before any of the spheres */
   @Test
   void testViewerHitsPlane() {
      Geometries geometries = new Geometries(new Plane(Point.ZERO, Vector.AXIS_Z), onLightPath(), offLightPath());
      assertEquals(Point.ZERO, viewer.findClosestPoint(geometries.findIntersections(viewer)),
                   "The viewing ray should hit the plane at the origin");
   }

   /** TC01: point light, no blocker - lit by cos(45) */
   @Test
   void testPointNoBlocker() {
      assertGray(LIT, trace(pointLight()));
   }

   /** TC02: spotlight, no blocker - lit by cos(45) */
   @Test
   void testSpotNoBlocker() {
      assertGray(LIT, trace(spotLight()));
   }

   /** TC03: point light, a small sphere on the path to the light - shadow */
   @Test
   void testPointBlockerOnLightPath() {
      assertGray(0, trace(pointLight(), onLightPath()));
   }

   /** TC04: spotlight, a small sphere on the path to the light - shadow */
   @Test
   void testSpotBlockerOnLightPath() {
      assertGray(0, trace(spotLight(), onLightPath()));
   }

   /** TC05: point light, a small sphere beside the path to the light - not a blocker */
   @Test
   void testPointSphereOffLightPath() {
      assertGray(LIT, trace(pointLight(), offLightPath()));
   }

   /** TC06: spotlight, a small sphere beside the path to the light - not a blocker */
   @Test
   void testSpotSphereOffLightPath() {
      assertGray(LIT, trace(spotLight(), offLightPath()));
   }
}
