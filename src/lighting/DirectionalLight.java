package lighting;

import primitives.*;

/**
  * DirectionalLight represents a light source that has a constant direction
  * and illuminates the scene uniformly from a very far distance, like sunlight.
  * It does not have a position, so its light does not decay with distance.
*/
public class DirectionalLight extends Light implements LightSource {

    /** Light direction vector */
    private final Vector direction;



    /**
     * Constructs a new directional light with a given intensity and direction.
     *
     * @param intensity the color (intensity) of the light
     * @param direction the direction vector of the light (will be normalized)
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
     * For directional light, it is always the same (fixed direction), but inverted
     * because light travels *towards* the point.
     * @param p the point in the scene
     * @return a normalized vector pointing from the light to the point {@code p}
     */
    public Vector getL(Point p){
        return this.direction.scale((-1)).normalize();
    }
}
