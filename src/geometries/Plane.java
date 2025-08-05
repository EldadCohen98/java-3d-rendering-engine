package geometries;

import primitives.*;
import java.util.List;
import static primitives.Util.*;

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

    /**
      * Calculates the intersection point between a given ray and the plane.
      * Returns a list with a single {@link Intersection} containing the point
      * and this geometry, or {@code null} if there is no intersection.
      * The method handles the following cases:
      *     If the ray is parallel to the plane – returns {@code null}.
      *     If the ray starts on the plane – returns {@code null}.
      *     If the intersection point is behind the ray origin – returns {@code null}.
      *     Otherwise – returns the intersection point as an {@code Intersection}.
      * @param ray the ray to intersect with the plane
      * @return a list containing the intersection, or {@code null} if no intersection occurs
    */
    @Override
    public List<Intersection> calculateIntersectionsHelper(Ray ray){
        //The denominator of the formula
        double normalDualDirection = Util.alignZero(this.normal.dotProduct(ray.getDirection()));
        //If the denominator is 0 there is nothing more to check and we will return null
        if(isZero(normalDualDirection)){
            return null;
        }
        if(this.q0.equals(ray.getHead())){
            return null;
        }
        //Calculate the denominator of the formula.
        Vector P0Q0 = this.q0.subtract(ray.getHead());

        //Substituting into the formula to find the distance between the top of the ray and the plane
        double t = (this.normal.dotProduct(P0Q0)) / normalDualDirection;

        //When T=0 it means that the ray started in the plane.
        //When T<0 it means that the ray continues in the opposite direction to the plane and does not hit it.
        //Therefore, we will return null in both cases.
        if(alignZero(t) <= 0) {
            return null;
        }

        // Calculating the intersection point of the ray and the plane.
        Point intersectionPoint = ray.getPoint(t);

        return List.of(new Intersection(this, intersectionPoint, this.getMaterial()));
    }
}
