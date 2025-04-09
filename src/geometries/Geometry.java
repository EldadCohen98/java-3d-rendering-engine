package geometries;

import primitives.*;

/**
 * Abstract base class for all geometric objects.
 * Defines a common interface for retrieving the normal vector at a given point on the surface.
 */
public abstract class Geometry {

    /**
     * Returns the normal vector to the geometry at the given point.
     *
     * @param point The point on the geometry surface
     * @return The normal vector at the point
     */
    public abstract Vector getNormal(Point point);
}
