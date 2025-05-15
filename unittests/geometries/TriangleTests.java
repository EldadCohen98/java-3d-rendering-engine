package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
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

    /**
     * Unit test for {@link geometries.Triangle#findIntersections(primitives.Ray)}.
     * This test method checks the correctness of the findIntersections method
     * for the Triangle class through various edge and partition cases:
     *     TC1: Ray intersects the triangle inside (1 point expected)
     *     TC2: Ray misses the triangle (0 points)
     *     TC3: Ray intersects exactly on an edge (0 points)
     *     TC4: Ray intersects exactly on a vertex (0 points)
     *     TC5: Ray starts after the triangle and continues away (0 points)
     *     TC6: Ray starts on the triangle and moves outward (0 points)
     */

    @Test
    public void testFindIntersections(){
        //set a triangle
        Point A = new Point(3,0,0);
        Point B = new Point(0,5,0);
        Point C = new Point(0,0,3);
        Triangle triangle = new Triangle (A,B,C);

                        // ----- TC1 ----- \\
        //set a ray
        Ray crossRay = new Ray(new Point(1,1,3), new Vector(0,0,-1));
        //function call
        var resultTC1 = triangle.findIntersections(crossRay);
        //examination
        assertNotNull(resultTC1, "A ray that starts in the triangle's area and goes in its direction should intersect the triangle once.");
        assertEquals(1, resultTC1.size(), "A ray that intersects the triangle should intersect the triangle once.");

                        // ----- TC2 ----- \\
        //set a ray
        Ray backRay = new Ray(new Point(3,3,1), new Vector(1,0,1));
        //function call
        var resultTC2 = triangle.findIntersections(backRay);
        //examination
        assertNull(resultTC2, "A ray that moves away from the triangle should not intersect the triangle.");

                        // ----- TC3 ----- \\
        //set a ray
        Ray edgeRay = new Ray(new Point(3,3,1), B.subtract(A));
        var resultTC3 = triangle.findIntersections(edgeRay);
        assertNull(resultTC3, "A ray that hits a side does not intersect the triangle.");

                        // ----- TC4 ----- \\
        //set a ray
        Ray vertexRay = new Ray(new Point(3,3,1), A.subtract(new Point(3,3,1)));
        //function call
        var resultTC4 = triangle.findIntersections(vertexRay);
        //examination
        assertNull(resultTC4,"A ray that hits a vertex does not intersect the triangle.");

                        // ----- tC5 ----- \\
        //set a ray
        Ray startAfterRay = new Ray(new Point(-2,3,1), new Vector(-1,1,1));
        //function call
        var resultTC5 = triangle.findIntersections(startAfterRay);
        //examination
        assertNull(resultTC5, "A ray that starts after the triangle and continues does not hit the triangle.");

                        // ----- TC6 ----- \\
        //set a ray
        Ray onRay = new Ray(new Point(1,2.5,0.5), new Vector(1,1,1));
        //function call
        var resultTC6 = triangle.findIntersections(onRay);
        //examination
        assertNull(resultTC6, "A ray that starts on the triangle does not cross the triangle.");
    }

}