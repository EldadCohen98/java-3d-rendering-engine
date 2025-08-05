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
      * Constructs a finite cylinder with a given height, axis ray, and radius.
      * <p>
      * The cylinder is defined by an infinite tube (inherited) and a finite height
      * starting from the given point in the direction of the axis.
      *
      * @param height        the height of the cylinder (must be positive)
      * @param headRay       the starting point of the axis ray
      * @param directionRay  the direction vector of the axis ray
      * @param radius        the radius of the cylinder (inherited from Tube)
      * @throws IllegalArgumentException if height is not greater than zero
    */
    public Cylinder(double height, Point headRay, Vector directionRay, double radius) {
        super(headRay, directionRay, radius);

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
