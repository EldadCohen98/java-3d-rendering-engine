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
     * (Currently not implemented; returns null.)
     *
     * @param point The point on the tube surface
     * @return The normal vector
     */
    @Override
    public Vector getNormal(Point point) {
        //1. Calculate the vector between the vertex and the given point.
        Vector pointToHead = point.subtract(this.axisHead);

        //2. Calculate the distance between the starting point of the ray and the given point.
        double pointToHeadDistance = pointToHead.dotProduct(this.axisDirection);

        //3. "normalPoint" is the point of the normalized vector
        Point normalPoint = this.axisHead.add(this.axisDirection.scale(pointToHeadDistance));

        //4. We will return the normal vector at the point
        return normalPoint.subtract(point).normalize();
    }


    @Override
    public List<Intersection> calculateIntersectionsHelper(Ray ray){
        return null;
    }
}

