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
    private double narrowBeam = 1;

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
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    /**
     * Sets the linear attenuation factor and returns the current SpotLight.
     * @param kL the linear attenuation factor
     * @return the current SpotLight object for method chaining
     */
    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    /**
     * Sets the quadratic attenuation factor and returns the current SpotLight.
     * @param kQ the quadratic attenuation factor
     * @return the current SpotLight object for method chaining
     */
    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }


    /**
     * Calculates the intensity of the spotlight at a given point.
     * It uses the attenuation factors from PointLight and multiplies it by
     * (dir · L)^narrowBeam, where L is the direction from the light to the point.
     * Points behind the spotlight (dir · L &lt;= 0) get no light.
     * @param p the point in the scene
     * @return the attenuated light intensity at point {@code p}
     */
    @Override
    public Color getIntensity(Point p){
        //cosine of the angle between the spot direction and the light-to-point direction
        double proportion = Util.alignZero(this.spotLightDirection.dotProduct(this.getL(p)));

        if (proportion <= 0)
           return Color.BLACK;

        return super.getIntensity(p).scale(Math.pow(proportion, this.narrowBeam));
    }

    /**
     * Returns the normalized vector from the spotlight position to the given point.
     * @param p the point in the scene
     * @return a normalized vector pointing from the light source to point {@code p}
     */
    @Override
    public Vector getL(Point p){
        return super.getL(p);
    }

    @Override
    public double getDistance(Point point){
        return super.getDistance(point);
    }


    /**
     * Sets the narrow beam exponent - higher values give a narrower beam.
     * @param narrow the exponent of the spot factor (1 = regular spotlight)
     * @return the current SpotLight object for method chaining
     */
    public SpotLight setNarrowBeam(double narrow){
        this.narrowBeam = narrow;
        return this;

    }
}
