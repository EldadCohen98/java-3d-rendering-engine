package renderer;

import primitives.*;
import scene.Scene;

/**
  * Abstract class representing a ray tracer responsible for tracing a ray
  * and computing the color of the intersection point in a 3D scene.
*/
public abstract class RayTracerBase {

    /** The scene to be rendered */
    protected final Scene scene;

    /**
      * Constructor for RayTracerBase
      * @param scene the scene to trace rays in
    */
    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
      * Traces the given ray through the scene and calculates the color at the intersection point.
      * @param ray the ray to trace
      * @return the color of the closest intersection point (or background color if no intersection)
    */
    public abstract Color traceRay(Ray ray);
}
