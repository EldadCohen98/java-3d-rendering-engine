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
}