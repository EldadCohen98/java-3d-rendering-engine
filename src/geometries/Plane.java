package geometries;

import primitives.*;

/**
 * Represents a plane in 3D space, defined either by a point and a normal vector,
 * or by three points lying on the plane.
 */
public class Plane extends Geometry {

    /**
     * A reference point on the plane.
     */
    private final Point q0;

    /**
     * The normalized normal vector of the plane.
     */
    private final Vector normal;

    /**
     * Constructs a plane from a point and a normal vector.
     * The normal vector is automatically normalized.
     *
     * @param q0     A point on the plane
     * @param normal The normal vector to the plane
     */
    public Plane(Point q0, Vector normal) {
        this.q0 = q0;
        this.normal = normal.normalize();
    }

    /**
     * Constructs a plane from three points lying on the plane.
     * The normal is calculated using the cross product of two vectors formed by the points.
     *
     * @param q0 First point on the plane
     * @param q1 Second point on the plane
     * @param q2 Third point on the plane
     */
    public Plane(Point q0, Point q1, Point q2) {
        this.q0 = q0;

        Vector v1 = q0.subtract(q1);
        Vector v2 = q0.subtract(q2);

        this.normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Returns the normal vector to the plane (which is constant everywhere on the plane).
     *
     * @param point The point at which the normal is requested (ignored in this case)
     * @return The normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        return this.normal;
    }
}
