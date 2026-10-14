package primitives;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link primitives.Ray} class.
 * This class includes tests for:
 *  {@code findClosestPoint(List<Point>)} – finding the closest point to the ray's origin
 * All tests are written using TDD principles to verify correctness of geometric computations.
 */
class RayTest {
    /**
     * Unit test for {@link primitives.Ray#getPoint(double)}.
     * This test verifies the correctness of the getPoint method,
     * which calculates a point on the ray at a given distance t from its origin.
     * The method is tested in three scenarios:
     * ZTC1: t is negative – verifies that the method correctly computes a point behind the ray origin.
     * TC2: t is positive – verifies that the method correctly computes a point in the direction of the ray.
     * TC3: t is zero – verifies that the method returns the origin point itself.
     */
    @Test
    void testGetPoint() {
        Ray checkRay = new Ray(new Point(1,1 ,-31), new Vector(2,-3 ,1 ));

        // ------------------------------- // ===== TC1 ===== \\ ------------------------------- \\
        double negativeT = -5;
        Point acceptedPoint = checkRay.getHead().add(checkRay.getDirection().scale(negativeT));

        var resultTC1 = checkRay.getPoint(negativeT);

        assertNotNull(resultTC1, "Ray.getPoint() returned null");
        assertEquals(resultTC1, acceptedPoint, "Ray.getPoint() returned wrong result");

        // ------------------------------- // ===== TC2 ===== \\ ------------------------------- \\
        double positiveT = 5;
        Point acceptedPoint2 = checkRay.getHead().add(checkRay.getDirection().scale(positiveT));

        var resultTC2 = checkRay.getPoint(positiveT);

        assertNotNull(resultTC2, "Ray.getPoint() returned null");
        assertEquals(resultTC2, acceptedPoint2, "Ray.getPoint() returned wrong result");


        // ------------------------------- // ===== TC3 ===== \\ ------------------------------- \\
        double t0 = 0;

        var resultTC3 = checkRay.getPoint(t0);

        assertNotNull(resultTC3, "Ray.getPoint() returned null");
        assertEquals(resultTC3, checkRay.getHead(), "Ray.getPoint() returned wrong result");
    }


    /**
     * Unit tests for {@link primitives.Ray#findClosestPoint(java.util.List)}.
     * Test cases:
     *    1. Test with null list (should return null)
     *    2. Test where the first point is the closest
     *    3. Test where the last point is the closest
     *    4. Test where the closest point is in the middle of the list
     */
    @Test
    void testFindClosestPoint(){
        // ============== 1. Empty list (null) - expected result - null. ============== \\
        Ray firstRay = new Ray(new Point(1,1 ,-1), new Vector(2,3 ,-1 ));
        List<Point> points1 = null;
        assertNull(firstRay.findClosestPoint(points1), "A list of points that is null - should return null.");

        // ============== 2. The first point is closest to the vertex. ============== \\
        Ray secondRay = new Ray(new Point(0,0 ,0), new Vector(1,1 ,-1 ));
        Point p1 = new Point(1,0 ,0 );
        Point p2 = new Point(2,4 ,-1 );
        Point p3 = new Point(3,0 ,5 );
        List<Point> points2 = List.of(p1,p2,p3);
        Point result = secondRay.findClosestPoint(points2);
        assertNotNull(result, "The list and the ray should not return a null value.");
        assertEquals(result, p1, "the ray returned wrong result");

        // ============== 3. The last point is closest to the vertex. ============== \\
        Ray thirdRay = new Ray(new Point(0,0 ,0), new Vector(0,0 ,-1 ));
        Point q1 = new Point(3,6 ,5 );
        Point q2 = new Point(2,4 ,-1 );
        Point q3 = new Point(1,0 ,-1 );
        List<Point> points3 = List.of(q1,q2,q3);
        result = thirdRay.findClosestPoint(points3);
        assertNotNull(result, "The list and the ray should not return a null value.");
        assertEquals(result, q3, "the ray and list returned wrong result");

        // ============== 4. The closest point is some point in the middle of the list. ============== \\
        Ray forthRay = new Ray(new Point(1,1 ,1), new Vector(-1,0 ,-1 ));
        Point v1 = new Point(2,4 ,-1 );
        Point v2 = new Point(1,0 ,0 );
        Point v3 = new Point(3,0 ,5 );
        List<Point> points4 = List.of(v1,v2,v3);
        result = forthRay.findClosestPoint(points4);
        assertNotNull(result, "The list and the ray should not return a null value.");
        assertEquals(result, v2, "the ray and list returned wrong result");
    }

    /**
     * Unit test for {@link primitives.Ray#findClosestPoint(java.util.List)} with an empty list.
     * An empty list (not null) should return null without throwing an exception.
     */
    @Test
    void testFindClosestPointEmptyList(){
        Ray ray = new Ray(new Point(1,1 ,-1), new Vector(2,3 ,-1 ));
        Point result = assertDoesNotThrow(() -> ray.findClosestPoint(List.of()),
                                          "An empty list of points should not throw an exception.");
        assertNull(result, "An empty list of points - should return null.");
    }

    /**
     * Unit test for the constructor {@link primitives.Ray#Ray(Point, Vector, Vector)}.
     * The head is shifted by 0.1 along the normal, to the side the direction points to.
     * If the direction is tangent to the surface (the dot product with the normal is zero after aligning to zero)
     * the head is not shifted.
     * The surface in the tests is the plane z=0, with the normal (0,0,1) unless stated otherwise.
     */
    @Test
    void testConstructorWithNormal() {
        final Vector normal = new Vector(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============
        // TC01: direction at an acute angle to the normal - the head moves along the normal
        Ray up = new Ray(Point.ZERO, new Vector(1, 0, 1), normal);
        assertEquals(new Point(0, 0, 0.1), up.getHead(), "Wrong head for a direction above the surface");
        assertEquals(new Vector(1, 0, 1).normalize(), up.getDirection(), "Wrong direction above the surface");

        // TC02: direction at an obtuse angle to the normal - the head moves against the normal
        Ray down = new Ray(Point.ZERO, new Vector(1, 0, -1), normal);
        assertEquals(new Point(0, 0, -0.1), down.getHead(), "Wrong head for a direction below the surface");
        assertEquals(new Vector(1, 0, -1).normalize(), down.getDirection(), "Wrong direction below the surface");

        // TC03: the shift is relative to the head, and the normal may point away from the direction's side
        Ray flipped = new Ray(new Point(1, 2, 3), new Vector(0, 0, 1), new Vector(0, 0, -1));
        assertEquals(new Point(1, 2, 3.1), flipped.getHead(), "Wrong head for a reversed normal");

        // TC04: the direction is normalized, whatever its length
        Ray longDirection = new Ray(Point.ZERO, new Vector(3, 0, 4), normal);
        assertEquals(1, longDirection.getDirection().length(), 1e-12, "The direction is not normalized");
        assertEquals(new Vector(0.6, 0, 0.8), longDirection.getDirection(), "Wrong normalized direction");
        assertEquals(new Point(0, 0, 0.1), longDirection.getHead(), "Wrong head for a long direction");

        // =============== Boundary Values Tests ==================
        // TC11: direction tangent to the surface - no exception, the head is not moved
        Ray tangent = assertDoesNotThrow(() -> new Ray(Point.ZERO, new Vector(1, 0, 0), normal),
                                         "A tangent direction should not throw an exception");
        assertEquals(Point.ZERO, tangent.getHead(), "The head of a tangent direction should not move");
        assertEquals(new Vector(1, 0, 0), tangent.getDirection(), "Wrong direction for a tangent direction");
        assertEquals(new Point(1, 2, 3), new Ray(new Point(1, 2, 3), new Vector(0, 4, 0), normal).getHead(),
                     "The head of a tangent direction should not move (head not at the origin)");

        // TC12: direction almost tangent (the dot product is aligned to zero) - same as TC11
        assertEquals(Point.ZERO, new Ray(Point.ZERO, new Vector(1, 0, -1e-13), normal).getHead(),
                     "An almost tangent direction (aligned to zero) should be treated as tangent");
        assertEquals(Point.ZERO, new Ray(Point.ZERO, new Vector(1, 0, 1e-13), normal).getHead(),
                     "An almost tangent direction (aligned to zero, the other side) should be treated as tangent");

        // TC13: a normal that is not a unit vector - the size of the shift stays 0.1
        assertEquals(new Point(0, 0, 0.1), new Ray(Point.ZERO, new Vector(0, 0, 1), new Vector(0, 0, 5)).getHead(),
                     "The shift depends on the length of the normal (up)");
        assertEquals(new Point(0, 0, -0.1), new Ray(Point.ZERO, new Vector(0, 0, -1), new Vector(0, 0, 5)).getHead(),
                     "The shift depends on the length of the normal (down)");

        // TC14: a normal component of the direction of +-1e-9 is not aligned to zero - the head is shifted by +-0.1
        assertEquals(new Point(0, 0, 0.1), new Ray(Point.ZERO, new Vector(1, 0, 1e-9), normal).getHead(),
                     "A normal component of +1e-9 should shift the head by +0.1");
        assertEquals(new Point(0, 0, -0.1), new Ray(Point.ZERO, new Vector(1, 0, -1e-9), normal).getHead(),
                     "A normal component of -1e-9 should shift the head by -0.1");

        // TC15: tangent direction with a normal that is not a unit vector - the head is not moved, no exception
        Vector longNormal = new Vector(0, 0, 5);
        Ray tangentLongNormal = assertDoesNotThrow(() -> new Ray(Point.ZERO, new Vector(1, 0, 0), longNormal),
                                                   "A tangent direction with a long normal should not throw an exception");
        assertEquals(Point.ZERO, tangentLongNormal.getHead(), "The head should not move for a tangent direction and a long normal");
        // TC16: +-1e-9 normal component with a normal that is not a unit vector - the shift is +-0.1
        assertEquals(new Point(0, 0, 0.1), new Ray(Point.ZERO, new Vector(1, 0, 1e-9), longNormal).getHead(),
                     "The shift for +1e-9 depends on the length of the normal");
        assertEquals(new Point(0, 0, -0.1), new Ray(Point.ZERO, new Vector(1, 0, -1e-9), longNormal).getHead(),
                     "The shift for -1e-9 depends on the length of the normal");
    }
}