package lighting;

import primitives.*;

/**
  * DirectionalLight represents a light source that has a constant direction
  * and illuminates the scene uniformly from a very far distance, like sunlight.
  * It does not have a position, so its light does not decay with distance.
*/
public class DirectionalLight extends Light implements LightSource {

    /** Normalized direction in which the light travels */
    private final Vector direction;



    /**
     * Constructs a new directional light with a given intensity and direction.
     *
     * @param intensity the color (intensity) of the light
     * @param direction the direction in which the light travels (will be normalized)
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    /**
      * Returns the intensity of the light at the given point.
      * Since directional light does not decay, it returns the same intensity everywhere.
      * @param p the point in space (ignored)
      * @return the constant light intensity
    */
    public Color getIntensity(Point p){
        return this.intensity;
    }

    /**
     * Returns the direction vector of the light towards the given point.
     * For directional light, it is always the light's travel direction (not inverted).
     * @param p the point in the scene (ignored)
     * @return a normalized vector pointing from the light to the point {@code p}
     */
    public Vector getL(Point p){
        return this.direction;
    }

    /**
     * Returns the distance from the light to the given point.
     * A directional light is infinitely far away.
     * @param point the point in the scene (ignored)
     * @return {@link Double#POSITIVE_INFINITY}
     */
    @Override
    public double getDistance(Point point){
        return Double.POSITIVE_INFINITY;
    }
}
