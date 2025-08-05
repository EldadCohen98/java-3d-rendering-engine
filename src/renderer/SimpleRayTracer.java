package renderer;

import lighting.LightSource;
import primitives.*;
import scene.Scene;
import java.util.List;
import geometries.Intersectable. Intersection;

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
        List<Intersection> intersectionPoints = this.scene.geometries.calculateIntersections(ray);

        if(intersectionPoints == null || intersectionPoints.isEmpty())
            return this.scene.background;

        Intersection closestIntersection = ray.findClosestIntersection(intersectionPoints);
        return this.calcColor(closestIntersection, ray);
    }

    /**
     * Calculates the color at the intersection point by combining diffuse and specular
     * effects, only if the point is not in shadow.
     *
     * @param intersection the intersection information (point, normal, geometry, etc.)
     * @param ray the ray that hit the point (used to compute the viewing direction)
     * @return the color resulting from local lighting effects, or black if the point is in shadow
     */
    private Color calcColor(Intersection intersection, Ray ray){
        if (!(preprocessIntersection(intersection, ray.getDirection())))
            return Color.BLACK;

        return calcColorLocalEffects(intersection);
    }

    /**
      * Preprocesses an intersection by setting the direction of the incoming ray,
      * calculating the surface normal at the point, and ensuring that the normal is
      * oriented against the ray direction (facing the viewer).
      * This method is essential for shading calculations such as diffuse and specular,
      * which depend on the angle between the normal and the incoming light/ray direction.
      * @param intersection the {@link Intersection} object to be updated with normal and direction data
      * @param directionRay the direction vector of the incoming ray (from the camera)
      * @return {@code true} if the intersection is valid for lighting computations;
      *         {@code false} if the ray is perpendicular to the surface (i.e., dot product is zero)
    */
    public boolean preprocessIntersection (Intersection intersection, Vector directionRay){
        intersection.directionRay = directionRay;
        intersection.normalIntersection = intersection.geometry.getNormal(intersection.point);
        intersection.dirScaleNormal = intersection.normalIntersection.dotProduct(intersection.directionRay);
        if (Util.isZero(intersection.dirScaleNormal))
            return false;
        if (intersection.dirScaleNormal > 0)
            intersection.normalIntersection = intersection.normalIntersection.scale((-1));
        return true;
    }

    /**
     * Initializes lighting-related values in the given intersection:
     *     Sets the light source that affects this intersection
     *     Computes the normalized direction vector from the light to the point
     *     Computes the dot product between the light direction and the surface normal
     * If both the dot product between the view direction and the normal (dirScaleNormal),
     * and the dot product between the light direction and the normal (lightScaleNormal),
     * are zero, then the intersection does not contribute to lighting and the method returns {@code false}.
     * @param intersection the intersection to update with light information
     * @param lightSource the light source affecting the intersection
     * @return {@code true} if the intersection is valid for lighting calculations, {@code false} otherwise
     */
    public  boolean setLightSource(Intersection intersection, LightSource lightSource){
        intersection.lightSource = lightSource;
        intersection.lightToIntersection = intersection.lightSource.getL(intersection.point);
        intersection.lightScaleNormal = intersection.lightToIntersection.dotProduct(intersection.normalIntersection);

        return !Util.isZero(intersection.dirScaleNormal) || (!Util.isZero(intersection.lightScaleNormal));
    }

    /**
      * Calculates the local lighting effects (diffuse and specular) at the intersection point.
      * This method uses the Phong reflection model to compute the light contribution from a single light source.
      *
      * @param intersection the intersection data containing point, normal, material, light source, etc.
      * @return the color resulting from the local lighting effects (diffuse + specular)
    */
    private Color calcColorLocalEffects(Intersection intersection){
        Color resultColor = Color.BLACK;
        for(LightSource light : scene.lights){
            if(!(setLightSource(intersection, light)))
                continue;

            Color LightIntensity = intersection.lightSource.getIntensity(intersection.point).scale(calcSpecular(intersection).add(calcDiffusive(intersection)));
            resultColor = resultColor.add(LightIntensity);
        }
        return resultColor;
    }

    /**
      * Calculates the specular reflection component at the intersection point
      * based on the Phong reflection model.
      * This represents the "shiny highlight" seen on glossy surfaces.
      *
      * @param intersection the intersection data containing vectors and material
      * @return the specular contribution as a Double3 (RGB scale), or ZERO if angle is negative
    */
    private Double3 calcSpecular(Intersection intersection){
        Vector R = intersection.lightToIntersection
                .subtract(intersection.normalIntersection.scale(2 *(intersection.normalIntersection.dotProduct(intersection.lightToIntersection)))).normalize();
        double angle = intersection.directionRay.scale((-1)).dotProduct(R);
        if (angle < 0)
            return Double3.ZERO;
        return intersection.geometry.getMaterial().kS.scale(Math.max(Math.pow(angle, intersection.geometry.getMaterial().nShininess), 0));
    }

    /**
      * Calculates the diffuse reflection component at the intersection point
      * based on the Lambertian reflection model.
      * This simulates the scattering of light on matte surfaces.
      *
      * @param intersection the intersection data containing vectors and material
      * @return the diffuse contribution as a Double3 (RGB scale), or ZERO if angle is non-positive
    */
    private Double3 calcDiffusive(Intersection intersection){
        intersection.lightToIntersection = intersection.lightToIntersection.normalize();
        intersection.normalIntersection = intersection.normalIntersection.normalize();

        double lightDotProductNormal = intersection.lightToIntersection.dotProduct(intersection.normalIntersection);
        if (lightDotProductNormal > 0)
            return intersection.geometry.getMaterial().kD.scale(lightDotProductNormal);
        return Double3.ZERO;
    }
}
