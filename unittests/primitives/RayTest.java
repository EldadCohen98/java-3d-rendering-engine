package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}