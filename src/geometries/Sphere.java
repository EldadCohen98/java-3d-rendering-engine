package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space.
 * The sphere is defined by its center point and radius.
 * Inherits from {@link RadialGeometry}.
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a sphere with a given center point and radius.
     *
     * @param center The center of the sphere
     * @param radius The radius of the sphere. Must be positive.
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    /**
     * Returns the normal vector to the sphere at the given point on its surface.
     * (Currently returns null; should be implemented later.)
     *
     * @param point The point on the sphere surface
     * @return The normal vector (null for now)
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}
