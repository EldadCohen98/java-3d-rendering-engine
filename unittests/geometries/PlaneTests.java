package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for Plane class.
 * Tests correctness of constructor and the getNormal() method.
 */
class PlaneTests {

    /**
     * Test method for constructor.
     * This test verifies that the constructor throws exceptions
     * for invalid configurations of 3 points:
     *     1. All three points are identical
     *     2. Two or more points are identical
     *     3. All three points lie on the same line (colinear)
     */
    @Test
    void testPlane() {

        // === All 3 points identical ===
        Point p1 = new Point(2, 0, 0);
        Point p2 = new Point(2, 0, 0);
        Point p3 = new Point(2, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p3), "Invalid input");

        // === Two points identical ===
        //1.
        Point p4 = new Point(2, 0, 0);
        Point p5 = new Point(2, 0, 0);
        Point p6 = new Point(3, 5, 8);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p4, p5, p6), "Invalid input");

        //2.
        Point p7 = new Point(3, 5, 8);
        Point p8 = new Point(2, 0, 0);
        Point p9 = new Point(2, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p7, p8, p9), "Invalid input");

        //3.
        Point p10 = new Point(2, 0, 0);
        Point p11 = new Point(3, 5, 8);
        Point p12 = new Point(2, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p10, p11, p12), "Invalid input");

        // === All three points on same line ===
        Point p13 = new Point(2, 0, 0);
        Point p14 = new Point(5, 0, 0);
        Point p15 = new Point(1, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> new Plane(p13, p14, p15), "Invalid input");


    }

    /**
     * Test method for getNormal().
     * Verifies that the normal vector:
     *   1. Is orthogonal to two vectors on the plane
     *   2. Has unit length
     */
    @Test
    void testGetNormal() {
        Point q1 = new Point(2, 9, 1);
        Point q2 = new Point(7, 3, 4);
        Point q3 = new Point(6, 5, 8);

        Plane plane = new Plane(q1, q2, q3);

        Vector v1 = q1.subtract(q2);
        Vector v2 = q1.subtract(q3);

        // Check orthogonality
        assertEquals(0, plane.getNormal(q1).dotProduct(v1), 0.00001,"The normal is not perpendicular to the vector V1");
        assertEquals(0, plane.getNormal(q1).dotProduct(v2), 0.00001,"The normal is not perpendicular to the vector V2");

        // Check unit length
        assertEquals(1, plane.getNormal(q1).length(), 0.00001, "The normal vector is not normal.");
    }
}