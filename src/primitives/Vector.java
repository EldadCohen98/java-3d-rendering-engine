package primitives;

/**
 * Represents a 3D vector, extending Point but disallowing the zero vector.
 */
public class Vector extends Point implements Cloneable {

    public static final Vector AXIS_X = new Vector(1, 0, 0);
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    public static final Vector AXIS_Z = new Vector(0, 0, 1);


    /**
     * Constructs a vector from x, y, z values.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @throws IllegalArgumentException if the zero vector is provided
     */
    public Vector(double x, double y, double z ) {
        super(x, y, z);
        if(xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
    }

    /**
     * Constructs a vector from a Double3 object.
     *
     * @param xyzValues The Double3 containing the vector components
     * @throws IllegalArgumentException if the zero vector is provided
     */
    public Vector(Double3 xyzValues) {
        super(xyzValues);
        if(xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
    }

    /**
     * Calculates the squared length (magnitude) of the vector.
     *
     * @return The squared length
     */
    public double lengthSquared() {
        return (this.xyz.d1() * this.xyz.d1() +
                this.xyz.d2() * this.xyz.d2() +
                this.xyz.d3() * this.xyz.d3());
    }

    /**
     * Calculates the Euclidean length (magnitude) of the vector.
     *
     * @return The length
     */
    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    /**
     * Returns a normalized (unit length) version of the vector.
     *
     * @return A new normalized vector
     * @throws IllegalArgumentException if the vector is zero
     */
    public Vector normalize() {
        if(Util.isZero(this.length()))
            throw new IllegalArgumentException("Cannot normalize a zero vector");

        return this.scale(1/this.length());
    }

    /**
     * Calculates the cross product between this vector and another.
     *
     * @param vectorMultiplication The other vector
     * @return A new vector representing the cross product
     */
    public Vector crossProduct(Vector vectorMultiplication) {
        return new Vector(
                this.xyz.d2()*vectorMultiplication.xyz.d3() - vectorMultiplication.xyz.d2()*this.xyz.d3(),
                (-1)*(this.xyz.d1()*vectorMultiplication.xyz.d3() - vectorMultiplication.xyz.d1()*this.xyz.d3()),
                this.xyz.d1()*vectorMultiplication.xyz.d2() - vectorMultiplication.xyz.d1()*this.xyz.d2());
    }

    /**
     * Calculates the dot product between this vector and another.
     *
     * @param vectorMultiplier The other vector
     * @return The dot product (scalar)
     */
    public double dotProduct(Vector vectorMultiplier) {
        return (this.xyz.d1() * vectorMultiplier.xyz.d1() +
                this.xyz.d2() * vectorMultiplier.xyz.d2() +
                this.xyz.d3() * vectorMultiplier.xyz.d3()) ;
    }

    /**
     * Scales the vector by a scalar.
     *
     * @param multiplierNumber The scalar to multiply by
     * @return A new scaled vector
     */
    public Vector scale(double multiplierNumber) {
        return new Vector(multiplierNumber * this.xyz.d1(),
                multiplierNumber * this.xyz.d2(),
                multiplierNumber * this.xyz.d3());
    }

    /**
     * Adds another vector to this one.
     *
     * @param addingVectors The vector to add
     * @return A new vector representing the sum
     */
    public Vector add(Vector addingVectors) {
        return new Vector(this.xyz.add(addingVectors.xyz));
    }

    /**
     * Returns a string representation of the vector.
     *
     * @return A string describing the vector
     */
    @Override
    public String toString(){
        return "Vector " + super.toString();
    }

    /**
     * Checks if this vector is equal to another object.
     *
     * @param comparatorObject The object to compare with
     * @return True if the vectors are equal, false otherwise
     */
    @Override
    public boolean equals(Object comparatorObject){
        if (!(comparatorObject instanceof Vector)) {
            return false;
        }
        Vector comparatorVector = (Vector) comparatorObject;
        return this.xyz.equals(comparatorVector.xyz);
    }

    /**
      * Creates and returns a copy (clone) of this {@code Vector}.
      * Since {@code Vector} extends {@link Point} and relies on its {@code clone()} implementation,
      * this method performs a shallow copy that safely duplicates the internal {@code Point} data.
      * Use this method to create an independent {@code Vector} with the same coordinate values.
      * @return a cloned {@code Vector} with the same direction
    */    @Override
    public Vector clone(){
            return (Vector) super.clone();
    }
}
