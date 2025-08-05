package lighting;

import primitives.*;

/**
  * PointLight represents a light source that emits light in all directions from a specific position in space,
  * like a light bulb. Its intensity decreases with distance using attenuation factors.
*/
public class PointLight extends Light  implements LightSource{

    /** The position of the light source in the scene */
    protected final Point position;

    /** Constant attenuation factor (default: 1) */
    protected double kC = 1;

    /** Linear attenuation factor (default: 0) */
    protected double kL = 0;

    /** Quadratic attenuation factor (default: 0) */
    protected double kQ = 0;



    /**
     * Constructs a point light with the given intensity and position.
     * @param intensity the color (intensity) of the light
     * @param position the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Sets the constant attenuation factor.
     * @param kC the constant attenuation factor
     * @return the current PointLight object for method chaining
     */
    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     * @param kL the linear attenuation factor
     * @return the current PointLight object for method chaining
     */
    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     * @param kQ the quadratic attenuation factor
     * @return the current PointLight object for method chaining
     */
    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    /**
     * Calculates the intensity of the light at a given point using attenuation.
     * @param p the point at which to calculate the intensity
     * @return the attenuated light intensity at point {@code p}
     */
    @Override
    public Color getIntensity(Point p){
        double dist = position.distance(p);
        //Using the formula 1 / (kC + kL * d + kQ * d²)
        double attenuation = 1/(this.kC + this.kL*dist + this.kQ * dist*dist);

        return this.intensity.scale(attenuation);

    }

    /**
     * Returns the normalized vector from the light source to the given point.
     *
     * @param p the point in the scene
     * @return a normalized vector pointing from the light source to point {@code p}
     */
    @Override
    public Vector getL(Point p){
        return p.subtract(position).normalize();
    }

}
