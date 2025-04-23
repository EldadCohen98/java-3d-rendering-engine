package geometries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import primitives.*;

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
}