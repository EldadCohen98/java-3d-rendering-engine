package lighting;

import primitives.*;

/**
  * The AmbientLight class represents a uniform background light in a 3D scene.
  * It simulates general illumination that affects all objects equally,
  * regardless of their position or direction.
*/
public class AmbientLight {

    /**
      * Light intensity by RGB components.
    */
    private final Color intensity;

    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor
     * @param intensity the ambient light color (intensity)
    */
    public AmbientLight(Color intensity) {
        this.intensity = intensity;
    }

    /**
      * Getter for intensity
      * @return color of ambient light
    */
    public Color getIntensity() {
        return intensity;
    }
}
