package geometries;

import primitives.Vector;
import primitives.Point;

/**
 * Abstract base class for geometries that have a radius,
 * such as spheres, cylinders, and tubes.
 * Extends the Geometry class and adds radius-related fields.
 */
public abstract class RadialGeometry extends Geometry {

    /**
     * The radius of the geometry.
     */
    protected final double radius;

    /**
     * The squared radius (used for efficiency in distance calculations).
     */
    protected final double radiusSquared;

    /**
     * Constructs a RadialGeometry with a given radius.
     *
     * @param radius The radius of the geometry. Must be positive.
     * @throws IllegalArgumentException if the radius is less than or equal to zero
     */
    public RadialGeometry(double radius) {
        if(radius <= 0)
            throw new IllegalArgumentException("Radius must be greater than zero");

        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    /**
     * Placeholder for getNormal implementation.
     * Subclasses must override this method.
     *
     * @param point The point on the geometry
     * @return Currently returns null. Should be implemented by subclasses.
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}
