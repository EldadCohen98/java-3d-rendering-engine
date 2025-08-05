package lighting;

import primitives.*;

/**
  * The AmbientLight class represents a uniform background light in a 3D scene.
  * It simulates general illumination that affects all objects equally,
  * regardless of their position or direction.
*/
public class AmbientLight extends Light {


    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor for ambient light.
     * @param intensity the ambient light color (intensity)
    */
    public AmbientLight(Color intensity) {
        super(intensity);
    }


}
