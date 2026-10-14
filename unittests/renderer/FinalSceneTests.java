package renderer;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * An original scene that shows the effects of stage 7 in one picture:
 * a reflective floor, a transparent sphere with a solid ball seen through it,
 * a solid triangle, and a point light that casts a full shadow (the triangle and the ball)
 * and a partial shadow (the transparent sphere).
 * The geometries, the positions and the colors are our own, not taken from the course material.
 * The camera looks from the front, a little above the floor, towards the negative z axis.
 */
class FinalSceneTests {
   /** Default constructor to satisfy JavaDoc generator */
   FinalSceneTests() { /* to satisfy JavaDoc generator */ }

   /** The scene of the test */
   private final Scene scene = new Scene("Final scene")
      .setBackground(new Color(8, 10, 30))
      .setAmbientLight(new AmbientLight(new Color(25, 25, 25)));

   /** Camera builder of the test */
   private final Camera.Builder camera = Camera.getBuilder()
      .setRayTracer(scene, RayTracerType.SIMPLE);

   /** Produce a picture with reflection, transparency, a partial shadow and a full shadow */
   @Test
   void finalScene() {
      scene.geometries.add(
         // the floor: the plane y=0, a dark mirror
         new Plane(Point.ZERO, Vector.AXIS_Y)
            .setEmission(new Color(10, 12, 20))
            .setMaterial(new Material().setKD(0.3).setKS(0.1).setShininess(40).setKR(0.45)),

         // the transparent sphere: it shows the solid ball behind it and casts a partial shadow
         new Sphere(new Point(-25, 25, 0), 25d)
            .setEmission(new Color(0, 15, 45))
            .setMaterial(new Material().setKD(0.2).setKS(0.3).setShininess(80).setKT(0.65)),

         // the solid ball behind the transparent sphere: seen through it, and casts a full shadow
         new Sphere(new Point(-25, 20, -45), 14d)
            .setEmission(new Color(120, 15, 10))
            .setMaterial(new Material().setKD(0.5).setKS(0.3).setShininess(50)),

         // the solid triangle standing on the floor: casts a full shadow, and is reflected in the floor
         new Triangle(new Point(22, 0, 20), new Point(58, 0, 12), new Point(40, 42, 16))
            .setEmission(new Color(10, 45, 15))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)));

      scene.lights.add(new PointLight(new Color(380, 360, 330), new Point(-55, 110, 90))
         .setKl(0.0002).setKq(0.000004));

      camera
         .setLocation(new Point(0, 65, 230))
         .setDirection(new Point(0, 15, -20), Vector.AXIS_Y)
         .setVpDistance(200).setVpSize(160, 120)
         .setResolution(600, 450)
         .build()
         .renderImage()
         .writeToImage("finalScene");
   }
}
