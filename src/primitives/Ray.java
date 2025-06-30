package primitives;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Represents a ray in 3D space, defined by a starting point (head)
 * and a normalized direction vector.
 */
public class Ray {

    /**
     * The origin point of the ray.
     */
    private final Point head;

    /**
     * The normalized direction vector of the ray.
     */
    private final Vector direction;

    /**
     * Constructs a ray from a given head point and direction vector.
     * The direction is automatically normalized.
     *
     * @param headRay The starting point of the ray
     * @param directionRay The direction vector of the ray
     */
    public Ray(Point headRay, Vector directionRay) {
        this.head = headRay;
        this.direction = directionRay.normalize();
    }

    /**
     * @return the head of ray
     */
    public Point getHead() {
        return head;
    }

    /**
     * @return the direction of ray
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * Returns a string representation of the ray.
     *
     * @return A string describing the ray's head and direction
     */
    @Override
    public String toString() {
        return "Ray [head = " + this.head + ", direction = " + this.direction + "]";
    }

    /**
     * Checks if this ray is equal to another object.
     * Two rays are considered equal if both their head and direction are equal.
     *
     * @param objectInspection The object to compare with
     * @return True if the rays are equal, false otherwise
     */
    @Override
    public boolean equals(Object objectInspection) {
        if (!(objectInspection instanceof Ray)) {
            return false;
        }
        Ray rayComparison = (Ray) objectInspection;
        return this.head.equals(rayComparison.head) && this.direction.equals(rayComparison.direction);
    }

    /**
     * @param t Distance from the top of the ray
     * @return  Point according to distance t.
     */
    public Point getPoint (double t) {
        if(isZero(t))
            return this.head;

        return this.head.add(this.direction.scale(t));
    }

    /**
      * Finds the point in the given list that is closest to the ray's origin (head).
      * @param points the list of points to check (can be null or empty)
      * @return the closest point to the ray's origin, or null if the list is null or empty
    */
    public Point findClosestPoint(List<Point> points) {
        if(points == null ||points.isEmpty())
            return null;

        Point closestPoint = null;
        double closestDistance = Double.MAX_VALUE;

        for(Point point : points) {
            double distance = point.distance(this.head);
            if(distance < closestDistance) {
                closestPoint = point;
                closestDistance = distance;
            }
        }
        return closestPoint;
    }
}

