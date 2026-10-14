package renderer;

import static renderer.LocalLightingTests.assertGray;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Unit tests for shadow rays in {@link SimpleRayTracer}.
 * The tested surface is the plane z=0 (kD=1, kS=0) lit at the origin from above.
 * The viewer looks at the origin obliquely from (10,0,10) so the viewing ray hits the plane
 * and passes by all the blockers (the closest approach of the viewing ray to the z axis
 * is far enough from every blocker in the tests).
 */
class ShadowRayTests {
   /** Default constructor to satisfy JavaDoc generator */
   ShadowRayTests() { /* to satisfy JavaDoc generator */ }

   /** Light intensity used in the tests */
   private final Color    white    = new Color(100, 100, 100);
   /** Oblique viewer looking at the origin */
   private final Ray      viewer   = new Ray(new Point(10, 0, 10), new Vector(-1, 0, -1));
   /** Light position for point and spot lights - right above the origin */
   private final Point    lightPos = new Point(0, 0, 5);

   /**
    * Helper: traces the viewer ray in a scene with the plane z=0, one light and extra geometries
    * @param light    the light source
    * @param blockers additional geometries
    * @return the traced color at the origin
    */
   private Color trace(LightSource light, Intersectable... blockers) {
      Scene scene = new Scene("Shadow test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z).setMaterial(new Material().setkD(1).setkS(0)));
      scene.geometries.add(blockers);
      scene.lights.add(light);
      return new SimpleRayTracer(scene).traceRay(viewer);
   }

   /** @return a point light right above the origin */
   private LightSource pointLight() { return new PointLight(white, lightPos); }

   /** @return a spotlight right above the origin pointing down */
   private LightSource spotLight() { return new SpotLight(white, lightPos, new Vector(0, 0, -1)); }

   /** Sanity: the viewing ray must hit the plane at the origin and not any of the blockers */
   @Test
   void testViewerHitsPlane() {
      Scene scene = new Scene("Viewer test");
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z),
                           new Sphere(new Point(0, 0, 2.5), 1d),
                           new Sphere(new Point(0, 0, 8), 1d),
                           new Sphere(new Point(0, 0, -3), 1d),
                           new Sphere(new Point(0, 0, 1000), 10d));
      assertEquals(Point.ZERO, viewer.findClosestPoint(scene.geometries.findIntersections(viewer)),
                   "The viewing ray should hit the plane at the origin");
   }

   /** A surface with no other geometry is not shadowed by itself */
   @Test
   void testNoSelfShadow() {
      // TC01: plane, point light
      assertGray(100, trace(pointLight()));
      // TC02: plane, spotlight
      assertGray(100, trace(spotLight()));
      // TC03: plane, directional light
      assertGray(100, trace(new DirectionalLight(white, new Vector(0, 0, -1))));
   }

   /** A sphere is not shadowed by itself at a lit point */
   @Test
   void testSphereNoSelfShadow() {
      // the top of the sphere is at the origin, the light is right above it
      Scene scene = new Scene("Sphere self shadow");
      scene.geometries.add(new Sphere(new Point(0, 0, -50), 50d).setMaterial(new Material().setkD(1)));
      scene.lights.add(pointLight());
      assertGray(100, new SimpleRayTracer(scene).traceRay(new Ray(new Point(0, 0, 10), new Vector(0, 0, -1))));
   }

   /** A blocker between the light and the surface casts a shadow */
   @Test
   void testBlockerBetween() {
      Sphere blocker = new Sphere(new Point(0, 0, 2.5), 1d);
      // TC01: point light
      assertGray(0, trace(pointLight(), blocker));
      // TC02: spotlight
      assertGray(0, trace(spotLight(), blocker));
   }

   /** A blocker beyond the light source does not shadow the surface */
   @Test
   void testBlockerBeyondLight() {
      Sphere blocker = new Sphere(new Point(0, 0, 8), 1d);
      // TC01: point light
      assertGray(100, trace(pointLight(), blocker));
      // TC02: spotlight
      assertGray(100, trace(spotLight(), blocker));
   }

   /** A blocker behind the surface does not block light coming from the front */
   @Test
   void testBlockerBehindSurface() {
      Sphere blocker = new Sphere(new Point(0, 0, -3), 1d);
      // TC01: point light
      assertGray(100, trace(pointLight(), blocker));
      // TC02: spotlight
      assertGray(100, trace(spotLight(), blocker));
      // TC03: directional light
      assertGray(100, trace(new DirectionalLight(white, new Vector(0, 0, -1)), blocker));
   }

   /** Directional light: a blocker towards the light casts a shadow at any distance */
   @Test
   void testDirectionalBlocker() {
      DirectionalLight light = new DirectionalLight(white, new Vector(0, 0, -1));
      // TC01: near blocker
      assertGray(0, trace(light, new Sphere(new Point(0, 0, 2.5), 1d)));
      // TC02: far blocker
      assertGray(0, trace(light, new Sphere(new Point(0, 0, 1000), 10d)));
   }

   /** A shadowed point keeps its emission and ambient light */
   @Test
   void testShadowKeepsEmissionAndAmbient() {
      Scene scene = new Scene("Shadow with ambient").setAmbientLight(new AmbientLight(new Color(40, 40, 40)));
      scene.geometries.add(new Plane(Point.ZERO, Vector.AXIS_Z)
                              .setEmission(new Color(10, 20, 30))
                              .setMaterial(new Material().setkD(1).setkA(1)),
                           new Sphere(new Point(0, 0, 2.5), 1d));
      scene.lights.add(pointLight());
      java.awt.Color c = new SimpleRayTracer(scene).traceRay(viewer).getColor();
      assertEquals(50, c.getRed(), "Wrong red component");
      assertEquals(60, c.getGreen(), "Wrong green component");
      assertEquals(70, c.getBlue(), "Wrong blue component");
   }
}
