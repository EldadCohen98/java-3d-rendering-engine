package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

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
     * @ Returns the normal vector between the center and the point we received.
     */
    @Override
    public Vector getNormal(Point point) {
        if(Util.isZero(this.center.distance(point)))
            throw new IllegalArgumentException("Normal is undefined at the center of the sphere");
        return point.subtract(this.center).normalize();
    }

    /**
     * @param ray The method receives a ray.
     * @return List of intersection points with the sphere.
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        //A ray begins at the center of the sphere.
        if(Util.isZero((this.center.distance(ray.getHead())))){
            //Exit point from the sphere according to the formula "P0 + directionVector * distance = P"
            Point exitPointCenter = ray.getPoint(this.radius);
            return List.of(exitPointCenter);
        }

        //Vector between the center point of the sphere and the starting point of the ray
        Vector centerToHeadRay = this.center.subtract(ray.getHead());

        //Cross product between the direction vector of the ray and
        //the vector we created from the beginning of the ray to the center point of the sphere
        double distanceToClosestPointRay = Util.alignZero(centerToHeadRay.dotProduct(ray.getDirection()));

        //The distance between the closest point on the ray and the center point of the sphere
        double squaredDistanceCenterClosestPoint = Util.alignZero((centerToHeadRay.lengthSquared() - distanceToClosestPointRay * distanceToClosestPointRay));

        //If the distance between the point on the ray closest to the center of the sphere is greater than or equal to the radius of the sphere
        //This means either it is tangent to the sphere or the ray does not touch the sphere and therefore we will return NULL
        if (Util.alignZero(squaredDistanceCenterClosestPoint - this.radiusSquared) >= 0) {
            return null;
        }

        //Half the length of the ray segment inside the sphere
        double halfLengthRaySegment = Math.sqrt(this.radiusSquared - squaredDistanceCenterClosestPoint);

        double rightPart = distanceToClosestPointRay - halfLengthRaySegment;
        double leftPart = distanceToClosestPointRay + halfLengthRaySegment;

        //where the ray starts beyond the sphere and intersects the sphere at two points.
        if(squaredDistanceCenterClosestPoint < this.radiusSquared && centerToHeadRay.length() > this.radius) {
            List<Point> intersections = new ArrayList<>();

            if(Util.alignZero((rightPart)) > 0) {
                //Entry point for sphere according to the formula
                Point entryPoint = ray.getPoint(rightPart);
                intersections.add(entryPoint);
            }
            if(Util.alignZero((distanceToClosestPointRay + halfLengthRaySegment)) > 0 && !Util.isZero((leftPart - rightPart))) {
                //Exit point from the sphere according to the formula
                Point exitPoint = ray.getPoint(leftPart);
                intersections.add(exitPoint);
            }
            //Add the points to the list and return the list
            if(!intersections.isEmpty())
                return intersections;
        }

        //The ray starts on the sphere and towards the inside of the sphere.
        if(Util.isZero((centerToHeadRay.length() - this.radius)) && ray.getDirection().dotProduct(centerToHeadRay) > 0){
            //Exit point from the sphere according to the formula "d * P0 + directionVector * distance = P"
            Point exitPointTangent = ray.getPoint(distanceToClosestPointRay * 2);
            return List.of(exitPointTangent);
        }

        //A ray starts on the sphere towards the outside of the sphere.
        if(Util.isZero((centerToHeadRay.length() - this.radius)) && ray.getDirection().dotProduct(centerToHeadRay) < 0) {
            return null;
        }

        //A ray begins within the sphere.
        if(Util.alignZero((centerToHeadRay.length() - this.radius)) < 0) {
            //The distance that needs to be added in the formula
            double distanceAdd = Math.sqrt(this.radiusSquared - squaredDistanceCenterClosestPoint) + distanceToClosestPointRay;

            //Exit point from the sphere according to the formula "P0 + directionVector * distance = P"
            Point exitPointCenter = ray.getPoint(distanceAdd);

            return List.of(exitPointCenter);
        }

        //If the list is empty, return null
        return null;
    }
}
