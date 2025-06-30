package renderer;

import primitives.*;
import scene.Scene;
import java.util.List;

/**
  * A simple ray tracer that implements basic ray tracing logic.
  * This version supports only ambient light and the closest intersection.
  * No shadows, reflection, or refraction are computed at this stage.
*/
public class SimpleRayTracer extends  RayTracerBase{

    /**
      * Constructs a SimpleRayTracer with the given scene.
      * @param scene the scene to render using simple ray tracing
    */
    public SimpleRayTracer (Scene scene) {
        super(scene);
    }

    /**
      * Traces a ray through the scene to find the closest intersection point.
      * Returns the ambient light color at the intersection,
      * or the background color if no intersection is found.
      * @param ray the ray to trace
      * @return the resulting color at the intersection point
    */
    public Color traceRay (Ray ray){
        List<Point> intersectionPoints = this.scene.geometries.findIntersections(ray);

        if(intersectionPoints == null || intersectionPoints.isEmpty()){
            return this.scene.ambientLight.getIntensity();
        }

        Point closestPoint = ray.findClosestPoint(intersectionPoints);
        return this.calcColor(closestPoint);
    }

    /**
      * Calculates the color at a given point in the scene.
      * At this stage, it only returns the ambient light color,
      * regardless of geometry or lighting conditions.
      * @param point the point in the scene where the color is calculated
      * @return the resulting color at the point (currently ambient light only)
    */
    private Color calcColor(Point point){
        return this.scene.ambientLight.getIntensity();
    }
}
