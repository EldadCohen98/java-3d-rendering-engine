package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void testSubtract() {
        Point p1 = new Point(1,2,3);
        Point p2 = new Point(4,6,8);
        Vector expected = new Vector(-3,-4,-5);
        assertEquals(expected, p1.subtract(p2), "Subtraction error between points");
    }

    @Test
    void testAdd() {
        Point p1 = new Point(1,2,3);
        Vector v1 = new Vector(12,5,7);
        Point expected = new Point(p1.xyz.add(v1.xyz));
        assertEquals(expected, p1.add(v1), "Addition error between points");
    }

    @Test
    void testDistanceSquared() {
        Point p1 = new Point(1,2,3);
        Point p2 = new Point(4,6,8);
        assertEquals(50, p1.distanceSquared(p2), "Distance Squared error between points");
    }

    @Test
    void testDistance() {
        Point p1 = new Point(1,2,3);
        Point p2 = new Point(4,6,8);
        assertEquals(Math.sqrt(50), p1.distance(p2), 0.00001 , "Distance error between points");
    }
}