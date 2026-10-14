package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the getNormal() method.
 * Tests the correctness of the normal vector at a point on the surface of a Tube.
 * The expected normals are worked out from the geometry: the normal points from the
 * closest point on the axis outwards to the surface point.
 */
class TubeTest {

    /** Delta for comparing doubles */
    private static final double DELTA = 1e-10;

    /**
     * Helper: asserts the normal of a tube at a point - expected direction, length 1
     * and orthogonal to the tube axis
     * @param expected      the expected normal
     * @param tube          the tube
     * @param axisDirection the direction of the tube axis
     * @param point         a point on the tube surface
     */
    private void assertNormal(Vector expected, Tube tube, Vector axisDirection, Point point) {
        Vector normal = assertDoesNotThrow(() -> tube.getNormal(point), "getNormal should not throw");
        assertEquals(expected, normal, "Wrong normal vector");
        assertEquals(1, normal.length(), DELTA, "Wrong normal vector length");
        assertEquals(0, axisDirection.dotProduct(normal), DELTA, "The normal and the axis are not perpendicular");
    }

    /**
     * The test checks:
     *   That the returned normal vector is perpendicular to the tube's axis.
     *   That the returned normal vector has length 1.
     *   That the returned normal matches the expected direction.
     * The axis starts at (0,6,12) in direction (0,5,3), radius 5. The surface point is
     * (0,6,12) + (0,5,3) + 5*(1,0,0) = (5,11,15): its closest axis point is (0,11,15),
     * so the outward normal is +X.
     */
    @Test
    void testGetNormal() {
        Vector directionRay = new Vector(0,5,3);
        Tube tube = new Tube(new Point(0,6,12), directionRay, 5);
        assertNormal(new Vector(1,0,0), tube, directionRay, new Point(5,11,15));
    }

    /** TC01: axis Z from the origin, radius 1, point (1,0,5) - closest axis point (0,0,5), normal +X */
    @Test
    void testNormalOnZAxisTube() {
        Tube tube = new Tube(Point.ZERO, Vector.AXIS_Z, 1);
        assertNormal(Vector.AXIS_X, tube, Vector.AXIS_Z, new Point(1,0,5));
    }

    /** TC02: same tube, point (1,0,0) - the projection on the axis is zero, normal +X without exception */
    @Test
    void testNormalZeroProjection() {
        Tube tube = new Tube(Point.ZERO, Vector.AXIS_Z, 1);
        assertNormal(Vector.AXIS_X, tube, Vector.AXIS_Z, new Point(1,0,0));
    }

    /** TC03: same tube, point (0,-1,-3) - negative projection (the tube is infinite), normal -Y */
    @Test
    void testNormalNegativeProjection() {
        Tube tube = new Tube(Point.ZERO, Vector.AXIS_Z, 1);
        assertNormal(new Vector(0,-1,0), tube, Vector.AXIS_Z, new Point(0,-1,-3));
    }

    /**
     * TC04: axis from (1,2,3) in direction (0,0,4) (not a unit vector), radius 1, point (1,3,3) -
     * the closest axis point is the axis head (1,2,3), normal +Y
     */
    @Test
    void testNormalShiftedAxis() {
        Vector direction = new Vector(0,0,4);
        Tube tube = new Tube(new Point(1,2,3), direction, 1);
        assertNormal(Vector.AXIS_Y, tube, direction, new Point(1,3,3));
    }

    /**
     * TC05: axis from (1,2,3) in direction (1,1,0), radius 2, point (3,4,5) -
     * the closest axis point is (3,4,3), normal +Z
     */
    @Test
    void testNormalDiagonalAxis() {
        Vector direction = new Vector(1,1,0);
        Tube tube = new Tube(new Point(1,2,3), direction, 2);
        assertNormal(Vector.AXIS_Z, tube, direction, new Point(3,4,5));
    }
}
