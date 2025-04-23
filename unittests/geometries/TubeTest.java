package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the getNormal() method.
 * Tests the correctness of the normal vector at a point on the surface of a Tube.
 */
class TubeTest {

    /**
     * The test checks:
     *   That the returned normal vector is perpendicular to the tube's axis.
     *   That the returned normal vector has length 1.
     *   That the returned normal matches the expected direction.
     */

    @Test
    void testGetNormal() {

        Point headRay = new Point(0,6,12);
        Vector directionRay = new Vector(0,5,3).normalize();
        Tube tube = new Tube (headRay, directionRay, 5);

        // point on the tube according GPT
        Point pointOnTube = new Point(5, 7.713, 13.028);

        //Vector from the top of the ray to the point on the tube.
        Vector headToPointOnTube = pointOnTube.subtract(headRay);
        //The distance along the axis.
        double distanceHeadToPointOnTube = headToPointOnTube.dotProduct(directionRay);
        //Reaches exactly the point on the pipe axis that is closest to the given point.
        Point normalPoint = headRay.add(directionRay.scale(distanceHeadToPointOnTube));

        Vector normalTube = normalPoint.subtract(pointOnTube).normalize();
        Vector expectNormal = tube.getNormal(pointOnTube);

        assertEquals(expectNormal, normalTube, "Wrong normal vector");
        assertEquals(1, normalTube.length(), 0.0001, "Wrong normal vector length");
        assertEquals(0, directionRay.dotProduct(normalTube), 0.0001, "The normal and direction are not perpendicular.");
    }
}