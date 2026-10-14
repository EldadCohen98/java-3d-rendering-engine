package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;
import java.util.List;

/**
 * Represents a triangle in 3D space.
 * A triangle is a specific case of a polygon with exactly three vertices.
 * Inherits from {@link Polygon}.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a triangle from three points.
     * The points must be ordered and form a valid triangle.
     *
     * @param a First vertex of the triangle
     * @param b Second vertex of the triangle
     * @param c Third vertex of the triangle
     */
    public Triangle(Point a, Point b, Point c) {
        super(a, b, c);
    }

    /**
     * Returns the normal vector to the triangle at the given point.
     * (Currently not implemented; returns null.)
     *
     * @param point A point on the triangle surface
     * @return The normal vector (null for now)
     */
    @Override
    public Vector getNormal(Point point) {
        Vector v1 = this.vertices.get(0).subtract(this.vertices.get(1));
        Vector v2 = this.vertices.get(0).subtract(this.vertices.get(2));
        return v1.crossProduct(v2).normalize();
    }
}
