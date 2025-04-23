package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit test class for getNormal() method.
 * Verifies that the normal vector computed for a triangle is correct.
 */
class TriangleTests {

    /**
     * Test method forgetNormal().
     * This test checks the following:
     *   1. The returned normal vector is orthogonal (perpendicular) to the triangle’s edges
     *   2.The returned normal vector has a unit length
     */
    @Test
    void getNormal() {
        // === Set up triangle points ===
        Point p1 = new Point(4, 8, 1);
        Point p2 = new Point(9, 3, 5);
        Point p3 = new Point(0, 6, 7);
        Triangle triangle = new Triangle(p1, p2, p3);

        // === Calculate two triangle edges ===
        Vector v1 = p1.subtract(p2);
        Vector v2 = p1.subtract(p3);

        // === Get the normal vector from the method ===
        Vector normalTriangle = triangle.getNormal(p1);

        // === Verify perpendicularity to both edges ===
        assertEquals(0, normalTriangle.dotProduct(v1), 0.0001, "Vector v1 not perpendicular to the normal.");
        assertEquals(0, normalTriangle.dotProduct(v2), 0.0001, "Vector v2 not perpendicular to the normal.");

        // === Verify unit length ===
        assertEquals(1, normalTriangle.length(), 0.0001, "The normal vector has the wrong length.");

    }
}