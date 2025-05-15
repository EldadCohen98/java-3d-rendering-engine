package geometries;

import primitives.*;

import java.util.List;

/**
 * Abstract base class for all geometric objects.
 * Defines a common interface for retrieving the normal vector at a given point on the surface.
 */
public abstract class Geometry implements Intersectable{

    /**
     * Returns the normal vector to the geometry at the given point.
     * @param point The point on the geometry surface
     * @return The normal vector at the point
     */
    public abstract Vector getNormal(Point point);

    /**
     * Calculates the intersection points of a given ray with the geometric object.
     * This default implementation returns {@code null}, indicating no intersections.
     * Specific geometric shapes should override this method with their own intersection logic.
     * @param ray The ray to intersect with the geometry.
     * @return A list of intersection {@link Point}s with the geometry, or null if there are no intersections.
     */
    @Override
    public List<Point> findIntersections(Ray ray){
        return null;
    }
}
