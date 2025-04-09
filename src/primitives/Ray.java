package primitives;

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
}
