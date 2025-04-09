package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a finite cylinder in 3D space.
 * A cylinder is defined by its height, a central axis ray, and a radius.
 * Inherits from {@link Tube}.
 */
public class Cylinder extends Tube {

    /**
     * The height of the cylinder.
     */
    private double height;

    /**
     * Constructs a cylinder with a given height, axis, and radius.
     *
     * @param height The height of the cylinder. Must be greater than zero.
     * @param axis   The axis ray (central direction and base point)
     * @param radius The radius of the cylinder. Must be positive.
     * @throws IllegalArgumentException if height is less than or equal to zero
     */
    public Cylinder(double height, Ray axis, double radius) {
        super(axis, radius);

        if (height <= 0)
            throw new IllegalArgumentException("Height must be greater than zero");

        this.height = height;
    }

    /**
     * Returns the normal vector to the cylinder at the given point on its surface.
     * (Currently not implemented; returns null.)
     *
     * @param point The point on the cylinder
     * @return The normal vector (null for now)
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}
