package lighting;

import primitives.*;

/**
 * SpotLight represents a spotlight that emits light in a specific direction from a specific point.
 * The intensity of the light decreases both with distance and with angle from the center of the beam.
 * The light behaves like a PointLight but is limited to a cone-shaped direction.
 */
public class SpotLight extends PointLight implements LightSource{

    /** Light direction vector */
    private final Vector spotLightDirection;

    /**
     * Constructs a spotlight with the given intensity, position, and direction.
     *
     * @param intensity the color (intensity) of the light
     * @param position the position of the light source
     * @param direction the direction of the spotlight beam (will be normalized)
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.spotLightDirection = direction.normalize();
    }

    /**
     * Sets the constant attenuation factor and returns the current SpotLight.
     * @param kC the constant attenuation factor
     * @return the current SpotLight object for method chaining
     */
    @Override
    public PointLight setKc(double kC) {
        return super.setKc(kC);
    }

    /**
     * Sets the linear attenuation factor and returns the current SpotLight.
     * @param kL the linear attenuation factor
     * @return the current SpotLight object for method chaining
     */
    @Override
    public PointLight setKl(double kL) {
        return super.setKl(kL);
    }

    /**
     * Sets the quadratic attenuation factor and returns the current SpotLight.
     * @param kQ the quadratic attenuation factor
     * @return the current SpotLight object for method chaining
     */
    @Override
    public PointLight setKq(double kQ) {
        return super.setKq(kQ);
    }


    /**
     * Calculates the intensity of the spotlight at a given point.
     * It uses the attenuation factors from PointLight and multiplies it by the cosine
     * of the angle between the spotlight direction and the direction to the point.
     * If the point is outside the cone, the intensity will be very low or zero.
     * @param p the point in the scene
     * @return the attenuated light intensity at point {@code p}
     */
    @Override
    public Color getIntensity(Point p){
       Color returnedIntensity = super.getIntensity(p);
       double proportion = Math.max(0, this.spotLightDirection.dotProduct(this.getL(p))); //Calculating whether the point is at an angle to the light
       return returnedIntensity.scale(proportion);
    }

    /**
     * Returns the normalized vector from the spotlight position to the given point.
     * @param p the point in the scene
     * @return a normalized vector pointing from the light source to point {@code p}
     */
    @Override
    public Vector getL(Point p){
        return p.subtract(position).normalize();
    }
}
