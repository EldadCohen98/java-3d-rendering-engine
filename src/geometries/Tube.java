package geometries;

import primitives.*;

import java.util.List;

/**
 * Represents an infinite tube in 3D space.
 * A tube is defined by a central axis ray and a radius.
 * Inherits from RadialGeometry.
 */
public class Tube extends RadialGeometry {

    /**
     * The axis ray of the tube (its central direction and starting point).
     */

    protected Ray axis;

    /**
     * The head of the tube's ray
     */
    protected Point axisHead;

    /**
     * The direction of the tube's ray
     */
    protected Vector axisDirection;

    /**
     * Constructs a tube with a given axis and radius.
     *
     * @param headRay      The head (starting point) of the tube's axis.
     * @param directionRay The direction of the tube's axis (will be normalized).
     * @param radius The radius of the tube. Must be positive.
     */
    public Tube(Point headRay, Vector directionRay, double radius) {
        super(radius);
        this.axisHead = headRay;
        this.axisDirection = directionRay.normalize();
        this.axis = new Ray(this.axisHead, this.axisDirection);
    }

    /**
     * Returns the normal vector to the tube at the given point on its surface.
     * The normal points outwards, from the closest point on the axis to the surface point.
     * The closest axis point is the axis head moved by the projection of (point - head) on the axis;
     * the projection may be negative (the tube is infinite) or zero (then the head itself is used).
     *
     * @param point The point on the tube surface
     * @return The normalized outward normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        //1. Calculate the vector between the vertex and the given point.
        Vector pointToHead = point.subtract(this.axisHead);

        //2. Calculate the distance between the starting point of the ray and the given point.
        double pointToHeadDistance = Util.alignZero(pointToHead.dotProduct(this.axisDirection));

        //3. "normalPoint" is the closest point on the axis (the head itself when the projection is zero)
        Point normalPoint = Util.isZero(pointToHeadDistance) ? this.axisHead
                : this.axisHead.add(this.axisDirection.scale(pointToHeadDistance));

        //4. We will return the outward normal vector at the point
        return point.subtract(normalPoint).normalize();
    }


    @Override
    public List<Intersection> calculateIntersectionsHelper(Ray ray){
        return null;
    }
}

