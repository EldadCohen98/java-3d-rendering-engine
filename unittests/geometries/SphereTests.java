package geometries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import primitives.*;

import java.util.List;

/**
 * Unit test class for getNormal() method.
 * Verifies that the normal vector computed for a point on a sphere is correct.
 */
class SphereTests {

    /**
     * Test method for getNormal().
     * This test checks the following:
     *   1 . The normal vector is in the correct direction (from the center to the surface point)
     *   2. The normal vector is normalized (unit length)
     */
    @Test
    void testGetNormal() {
        // === Set up sphere ===
        Point sphereCenter = new Point(0, 0, 0);
        double sphereRadius = 50;
        Sphere mySphere = new Sphere(sphereCenter, sphereRadius);

        // === A point on the sphere's surface ===
        Point pointOnSurface = new Point(0, 0, 50);

        // === Get normal from method ===
        Vector sphereNormal = mySphere.getNormal(pointOnSurface);

        // === Calculate expected normal manually ===
        Vector expectedNormal = pointOnSurface.subtract(sphereCenter).normalize();


        // === Verify the results ===
        assertEquals(1,sphereNormal.length(), 0.0001, "The sphere normal vector is not of the correct length.");
        assertEquals(expectedNormal, sphereNormal, "The sphere normal vector is incorrect.");
    }


    /** A point used in some tests */
    private final Point p001 = new Point(0, 0, 1);

    /** A point used in some tests */
    private final Point p100 = new Point(1, 0, 0);

    /** A vector used in some tests */
    private final Vector v001 = new Vector(0, 0, 1);

    /**
     * Unit test for {@link geometries.Sphere#findIntersections(primitives.Ray)}.

     * This test method verifies the correctness of the findIntersections method
     * for the Sphere class across a wide variety of scenarios, covering:

     *     Equivalence Partitions (EP):
     *             Ray's line is outside the sphere (0 points)
     *             Ray starts before and crosses the sphere (2 points)
     *             Ray starts inside the sphere (1 point)
     *             Ray starts after the sphere (0 points)

     *     Boundary Value Analysis (BVA):
     *             Ray starts at the sphere and goes inside (1 point)
     *             Ray starts at the sphere and goes outside (0 points)
     *             Ray crosses through the center of the sphere (2 points)
     *             Ray starts at the center (1 point)
     *             Ray starts at the sphere and goes outside from the other end (0 points)

     *     Tangent Cases:
     *             Ray starts before, on, or after the tangent point (0 points)

     *     liSpecial Orthogonal Cases:
     *             Ray is orthogonal to line from center and starts outside (0 points)
     *             Ray is orthogonal to line from center and starts inside (1 point)
     */

    @Test
    public void testFindIntersections() {
        // ===set a sphere=== \\
        Sphere sphere = new Sphere(p100, 1d);

        final Point expectedPoint1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        final Point expectedPoint2 = new Point(1.53484692283495, 0.844948974278318, 0);
        final var exp = List.of(expectedPoint1, expectedPoint2);

        // Two direction vectors for testing \\
        final Vector v310 = new Vector(3, 1, 0);
        final Vector v110 = new Vector(1, 1, 0);

        // Common starting point \\
        final Point p01 = new Point(-1, 0, 0);


        // ============ Equivalence Partitions Tests ==============

        // -----TC01: Ray's line is outside the sphere (0 points)----- \\
        assertNull(sphere.findIntersections(new Ray(p01, v110)), "Ray's line out of sphere");

        // -----TC02: Ray starts before and crosses the sphere (2 points)----- \\
        final var result2 = sphere.findIntersections(new Ray(p01, v310));
        assertNotNull(result2, "Can't be empty list");
        assertEquals(2, result2.size(), "Wrong number of points");
        assertEquals(exp, result2, "Ray crosses sphere");

        // -----TC03: Ray starts inside the sphere (1 point)----- \\
        Ray internalRay = new Ray(new Point(1, 0.5, 0), new Vector(0,1,0));

        //Value returned from the method
        var resultTC3 = sphere.findIntersections(internalRay);
        //Checking if a NULL value is returned
        assertNotNull(resultTC3, "Ray from inside the sphere should intersect once");
        //Checking if the number of elements being repeated is 1
        assertEquals(1, resultTC3.size(), "Wrong number of points for ray from inside");

        // -----TC04: Ray starts after the sphere (0 points)----- \\
        Ray externalRay = new Ray(new Point(3, 0, 0), new Vector(1,0,0));
        var resultTC4 = sphere.findIntersections(externalRay);
        assertNull(resultTC4, "Ray starts after the sphere and should not intersect");

        // =============== Boundary Values Tests ==================

                // **** Group 1: Ray's line crosses the sphere (but not the center) **** \\
        // -----TC11: Ray starts at sphere and goes inside (1 points)----- \\
        Ray onSphereRayIn = new Ray(new Point(1, 1, 0), new Vector(0,-0.5,0.5));
        var resultTC11 = sphere.findIntersections(onSphereRayIn);
        assertNotNull(resultTC11, "A ray from a point on the sphere inward should intersect the sphere once.");
        assertEquals(1, resultTC11.size(), "Wrong number of points for ray from inside");

        // -----TC12: Ray starts at sphere and goes outside (0 points)----- \\
        Ray onSphereRayOut = new Ray(new Point(1, 1, 0), new Vector(0,0.5,0.5));
        var resultTC12 = sphere.findIntersections(onSphereRayOut);
        assertNull(resultTC12, "A ray from a point on the sphere outwards should not intersect the sphere at all.");


                // **** Group 2: Ray's line goes through the center **** \\
        // -----TC21: Ray starts before the sphere (2 points)----- \\
        Ray crossRay = new Ray(new Point(-2, 0, 0), new Vector(1,0,0));
        var resultTC21 = sphere.findIntersections(crossRay);
        assertNotNull(resultTC21,"A ray from a point outside the sphere into the sphere that passes through the center point of the sphere should intersect the sphere twice.");
        assertEquals(2, resultTC21.size(), "Wrong number of points for ray from inside");

        // -----TC22: Ray starts at sphere and goes inside (1 point)----- \\
        Ray onRay = new Ray(new Point(0, 0, 0), new Vector(1,0,0));
        var resultTC22 = sphere.findIntersections(onRay);
        assertNotNull(resultTC22, "A ray starting on a sphere inward should intersect the sphere once.");
        assertEquals(1, resultTC22.size(), "Wrong number of points for ray from inside");

        // -----TC23: Ray starts inside (1 points)----- \\
        Ray inRay = new Ray(new Point(1, 0.5, 0), new Vector(1,0,0));
        var resultTC23 = sphere.findIntersections(inRay);
        assertNotNull(resultTC23, "A ray starting inside the sphere should intersect the sphere once.");
        assertEquals(1, resultTC23.size(), "Wrong number of points for ray from inside");

        // -----TC24: Ray starts at the center (1 points)----- \\
        Ray freomCenterRay = new Ray(new Point(1, 0, 0), new Vector(1,4,3));
        var resultTC24 = sphere.findIntersections(freomCenterRay);
        assertNotNull(resultTC24, "A ray from the center of the sphere should intersect the sphere once.");
        assertEquals(1, resultTC24.size(), "Wrong number of points for ray from inside");

        // -----TC25: Ray starts at sphere and goes outside (0 points)----- \\
        Ray onSphereRayOun = new Ray(new Point(2, 0, 0), new Vector(1,0,0));
        var resultTC25 = sphere.findIntersections(onSphereRayOun);
        assertNull(resultTC25, "A ray from the sphere outwards should not intersect the sphere at all.");

        // -----TC26: Ray starts after sphere (0 points)----- \\
        Ray startsAfterSphereRay = new Ray(new Point(3, 0, 0), new Vector(1,0,0));
        assertNull(sphere.findIntersections(startsAfterSphereRay),"A ray after the sphere and onwards should not cut the count at all");


                // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points) **** \\
        // ----- TC31: Ray starts before the tangent point----- \\
        Ray tangentBeforeRay = new Ray(new Point(0, 0, 1), new Vector(1,0,0));
        assertNull(sphere.findIntersections(tangentBeforeRay), "A tangent from the sphere onwards does not intersect the sphere at all.");

        // TC32: Ray starts at the tangent point----- \\
        Ray tangentOnRay = new Ray(new Point(1, 0, 1), new Vector(1,0,0));
        assertNull(sphere.findIntersections(tangentOnRay), "A tangent from the sphere onwards does not intersect the sphere at all.");

        // ----- TC33: Ray starts after the tangent point----- \\
        Ray tangentAfterRay = new Ray(new Point(2, 0, 1), new Vector(1,0,0));
        assertNull(sphere.findIntersections(tangentAfterRay), "A tangent from behind the sphere onwards does not intersect the sphere at all.");


                // **** Group 4: Special cases **** \\
        // ----- TC41: Ray's line is outside sphere, ray is orthogonal to ray start to sphere's center line----- \\
        Ray orthogonalRay = new Ray(new Point(3, 0, 0), new Vector(0,0,1));
        assertNull(sphere.findIntersections(orthogonalRay), "A ray passing near the sphere does not intersect the sphere at all.");

        // ----- TC42: Ray's starts inside, ray is orthogonal to ray start to sphere's center line----- \\
        Ray orthogonalInsideRay = new Ray(new Point(0.5, 0, 0), new Vector(0,0,1));
        assertNotNull(sphere.findIntersections(orthogonalInsideRay), "A ray from the sphere must intersect the sphere once.");
        assertEquals(1, sphere.findIntersections(orthogonalInsideRay).size(), "Wrong number of points for ray from inside");
    }
}