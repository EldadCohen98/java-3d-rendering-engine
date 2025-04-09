package primitives;

/**
 * Class representing a point in 3D space using Double3 coordinates.
 */
public class Point {

    /**
     * The coordinates of the point.
     */
    protected final Double3 xyz;

    /**
     * A constant representing the point (0,0,0) in space.
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point with the given Double3 coordinates.
     *
     * @param xyz The 3D coordinates.
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    /**
     * Constructs a point with the given x, y, and z values.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }

    /**
     * Subtracts another point from this point and returns the resulting vector.
     *
     * @param p1 The point to subtract.
     * @return The resulting vector from this point to p1.
     */
    public Vector subtract(Point p1) {
        return new Vector(this.xyz.subtract(p1.xyz));
    }

    /**
     * Adds a vector to this point and returns the resulting point.
     *
     * @param v1 The vector to add.
     * @return A new point after adding the vector.
     */
    public Point add(Vector v1) {
        return new Point(this.xyz.add(v1.xyz));
    }

    /**
     * Calculates the squared distance between this point and another point.
     *
     * @param p1 The other point.
     * @return The squared distance.
     */
    public double distanceSquared(Point p1) {
        Double3 distanceCoordinates = this.xyz.subtract(p1.xyz);

        return (distanceCoordinates.d1() * distanceCoordinates.d1()) +
                (distanceCoordinates.d2() * distanceCoordinates.d2() +
                        (distanceCoordinates.d3() * distanceCoordinates.d3()));
    }

    /**
     * Calculates the Euclidean distance between this point and another point.
     *
     * @param p1 The other point.
     * @return The distance.
     */
    public double distance(Point p1) {
        return Math.sqrt(distanceSquared(p1));
    }

    /**
     * Returns a string representation of the point.
     *
     * @return A string containing the point's coordinates.
     */
    @Override
    public String toString() {
        return "Point coordinates: \n" + this.xyz.toString();
    }

    /**
     * Checks if this point is equal to another object.
     *
     * @param obj The object to compare.
     * @return True if the object is a Point with the same coordinates, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point pointComparison = (Point) obj;
        return this.xyz.equals(pointComparison.xyz);
    }
}
