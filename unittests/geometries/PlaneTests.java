package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
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


    /**
     * Test method for {@link geometries.Plane#findIntersections(primitives.Ray)}.
     * This test covers all Equivalence Partition (EP) and Boundary Value Analysis (BVA) cases
     * for ray-plane intersection detection.
     * EP1: Ray intersects the plane (1 point expected)
     * EP2: Ray is parallel and outside the plane (no intersection)
     * EP3: Ray is parallel and contained in the plane (no intersection)
     * BVA1: Ray orthogonal to plane and starts before it (1 point expected)
     * BVA2: Ray orthogonal to plane and starts on it (no intersection)
     * BVA3: Ray orthogonal to plane and starts after it (no intersection)
     * BVA4: Ray starts before the plane but goes away from it (no intersection)
     * BVA5: Ray starts after the plane and continues away (no intersection)
     */
    @Test
    void findIntersections() {
        // === set a plan and normal === \\
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 0);
        Plane plane = new Plane(p1, p2, p3);
        Vector v1 = p1.subtract(p2);
        Vector v2 = p1.subtract(p3);
        Vector normalVector = v1.crossProduct(v2).normalize();

        // ----- TC1 ----- \\
        Ray downUpRay = new Ray(new Point(2, 2, -3), new Vector(1, 1, 1));
        var resultTC1 = plane.findIntersections(downUpRay);
        assertNotNull(resultTC1, "");
        assertEquals(1, resultTC1.size(), "Wrong number of points for ray from inside");

        // ----- TC2 ----- \\
        Ray parallelRay = new Ray(new Point(1, 1, 0), v1);
        var resultTC2 = plane.findIntersections(parallelRay);
        assertNull(resultTC2, "A ray parallel to a plane should not intersect the plane.");

        // ----- TC3 ----- \\
        Ray containedRay = new Ray(new Point(1, 0, 0), v1);
        var resultTC3 = plane.findIntersections(containedRay);
        assertNull(resultTC3, "A ray contained in a plane should not intersect the plane at all.");

        // ----- TC4 ----- \\
        Ray downUpOrthogonalRay = new Ray(new Point(-2, 0, -3), new Vector(0, 0, 1));
        var resultTC4 = plane.findIntersections(downUpOrthogonalRay);
        assertNotNull(resultTC4, "A ray that starts before a plane and is orthogonal to the plane intersects the plane once.");
        assertEquals(1, resultTC4.size(), "Wrong number of points for ray from inside");

        // ----- TC5 ----- \\
        Ray onPlanUpRay = new Ray(new Point(1, 0, 0), new Vector(0, 0, 1));
        var resultTC5 = plane.findIntersections(onPlanUpRay);
        assertNull(resultTC5, "A ray that starts on the plane does not intersect the plane at all.");

        // ----- TC6 ----- \\
        Ray onPlanDownRay = new Ray(new Point(1, 0, 0), new Vector(0, 0, -1));
        var resultTC6 = plane.findIntersections(onPlanDownRay);
        assertNull(resultTC6, "A ray that starts on the plane does not intersect the plane at all.");

        // ----- TC7 ----- \\
        Ray downRay = new Ray(new Point(-2, 0, -1), new Vector(0, 0, -1));
        var resultTC7 = plane.findIntersections(downRay);
        assertNull(resultTC7, "A ray that starts in front of the plane and continues behind it does not intersect the plane at all.");

        // ----- TC8 ----- \\
        Ray upRay = new Ray(new Point(1, 1, 1), new Vector(0, 0, 1));
        var resultTC8 = plane.findIntersections(upRay);
        assertNull(resultTC8,"A ray that starts after the plane and continues beyond it does not intersect the plane at all." );
    }
}