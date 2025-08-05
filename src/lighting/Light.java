package lighting;

import primitives.*;


/**
  * The Light class is an abstract base class representing a general light source
  * with an associated intensity (color).
  * This class serves as the foundation for specific types of lights
  * such as ambient light, directional light, point light, etc.
  * The intensity represents the power of the light and is stored as a Color object,
  * which can be scaled or combined with other light sources to compute
  * the final color of a point in the scene.
*/
abstract class Light {
    /**
      *  Power of the light source
    */
    protected final Color intensity;

    /**
     * Constructor to initialize the light's intensity.
     * @param intensity the intensity of the light
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
      * Gets the intensity of the light.
      * @return the light's intensity
    */
    public Color getIntensity() {
        return intensity;
    }
}
