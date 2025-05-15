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
        return super.getNormal(point);
    }


    /**
     * Checks whether a given point lies exactly on the edge defined by two triangle vertices.
     * The method computes the sum of distances from the intersection point to each vertex,
     * and compares it to the total edge length. If the sum equals the edge length (within numerical tolerance),
     * the point lies on the edge.

     * @param intersection The point to check.
     * @param vertexA One vertex of the edge.
     * @param vertexB The other vertex of the edge.
     * @return {@code true} if the point lies exactly on the edge between the two vertices; {@code false} otherwise.
     */

    public boolean isOnEdge(Point intersection, Point vertexA, Point vertexB) {
        double edgeLength = vertexA.subtract(vertexB).length();
        double intersectionToVertexA = intersection.distance(vertexA);
        double intersectionToVertexB = intersection.distance(vertexB);
         if(Util.isZero(((intersectionToVertexA + intersectionToVertexB) - (edgeLength))))
             return true;
         return false;
    }
    /**
     * @param ray The method receives a ray.
     * @return List of intersection points with the triangle.
     */
    @Override
    public List<Point> findIntersections(Ray ray){
        List<Point> intersections = plane.findIntersections(ray);
        //If the ray does not intersect the plane of the triangle
        if(intersections == null){
            return null;
        }

        //The point of intersection from the plane of the triangle
        Point intersection = intersections.getFirst();

        //Checking the equality of the point to one of the vertices
        if (Util.isZero(intersection.distance(this.vertices.get(0))) ||
                Util.isZero(intersection.distance(this.vertices.get(1))) ||
                Util.isZero(intersection.distance(this.vertices.get(2)))) {
            return null;
        }

        //Checking if the intersection point is on the edge
        if((this.isOnEdge(intersection, this.vertices.get(1), this.vertices.get(0))) ||
            (this.isOnEdge(intersection, this.vertices.get(2), this.vertices.get(1))) ||
            (this.isOnEdge(intersection, this.vertices.get(0), this.vertices.get(2))))
            return null;

        //vectors from the sides of the triangle
        Vector AB = this.vertices.get(1).subtract(this.vertices.get(0));
        Vector BC = this.vertices.get(2).subtract(this.vertices.get(1));
        Vector CA = this.vertices.get(0).subtract(this.vertices.get(2));

        //Vectors from the intersection point to the vertices of the triangle
        Vector ABIntersection = intersection.subtract(this.vertices.get(0));
        Vector BCIntersection = intersection.subtract(this.vertices.get(1));
        Vector CAIntersection = intersection.subtract(this.vertices.get(2));

        if(Util.isZero(ABIntersection.length()) || Util.isZero(BCIntersection.length()) || Util.isZero(CAIntersection.length())){
            return null;
        }

        //Cross product between the sides and the vectors at the intersection point
        Vector localNormal1 = AB.crossProduct(ABIntersection);
        Vector localNormal2 = BC.crossProduct(BCIntersection);
        Vector localNormal3 = CA.crossProduct(CAIntersection);

        //Dot product between the local normals and the principal normal
        Vector normal = plane.getNormal(intersection);

        double result1 = localNormal1.dotProduct(normal);
        double result2 = localNormal2.dotProduct(normal);
        double result3 = localNormal3.dotProduct(normal);

        //Check that all results have the same sign
        if((result1 > 0 && result2 > 0 && result3 > 0) || (result1 < 0 && result2 < 0 && result3 < 0)){
            return List.of(intersection);
        }

        return null;
    }
}
