package geometries;

import primitives.*;

/**
 * Represents an infinite tube in 3D space.
 * A tube is defined by a central axis ray and a radius.
 * Inherits from {@link RadialGeometry}.
 */
public class Tube extends RadialGeometry {

    /**
     * The axis ray of the tube (its central direction and starting point).
     */
    protected Ray axis;

    /**
     * Constructs a tube with a given axis and radius.
     *
     * @param axis   The axis ray of the tube
     * @param radius The radius of the tube. Must be positive.
     */
    public Tube(Ray axis, double radius) {
        super(radius);
        this.axis = axis;
    }

    /**
     * Returns the normal vector to the tube at the given point on its surface.
     * (Currently not implemented; returns null.)
     *
     * @param point The point on the tube surface
     * @return The normal vector (null for now)
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}
