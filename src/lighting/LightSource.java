package lighting;

import primitives.*;

/**
  * LightSource is an interface that represents a source of light in the scene.
  * Any type of light (such as directional light, point light, or spot light)
  * should implement this interface in order to define how the light behaves.
*/
public interface LightSource
{
    /**
     * Returns the intensity (color) of the light at a given point in space.
     * This method allows different light sources to compute how strong their
     * illumination is at a specific location (e.g. with attenuation for point lights).
     *
     * @param p the point in the scene at which the light intensity is evaluated
     * @return the color (intensity) of the light at point {@code p}
     */
    Color getIntensity(Point p);


    /**
     * Returns a normalized vector from the light source to a given point in space.
     * This vector represents the direction of the incoming light to the point
     * (the direction in which the light travels, NOT the direction towards the light).
     * It is commonly used for shading and lighting calculations (like diffuse & specular).
     *
     * @param p the point in the scene to which the light direction is computed
     * @return a normalized vector pointing from the light source to point {@code p}
     */
    Vector getL(Point p);

    /**
     * Returns the distance between the light source and a given point in space.
     * Used to limit shadow rays to the segment between the point and the light.
     *
     * @param point the point in the scene
     * @return the distance to the light, or {@link Double#POSITIVE_INFINITY} for a light with no position
     */
    double getDistance(Point point);
}
