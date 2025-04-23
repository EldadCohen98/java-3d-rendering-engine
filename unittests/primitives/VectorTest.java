package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    @Test
    void testLengthSquared() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        assertEquals(14, v1.lengthSquared(), 0.00001, "Squared length error of a vector");
    }

    @Test
    void testLength() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        assertEquals(Math.sqrt(14), v1.length(), 0.00001, "Vector length error");
    }

    @Test
    void testNormalize() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        Vector expected = new Vector(1.0/Math.sqrt(14),2.0/Math.sqrt(14),3.0/Math.sqrt(14));
        assertEquals(expected, v1.normalize(), "Vector normalization error");
    }

    @Test
    void testCrossProduct() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        Vector v2 = new Vector(8.0,10.0,7.0);
        Vector expected = new Vector(-16.0,17.0,-6.0);
        assertEquals(expected, v1.crossProduct(v2), "Cross product error");
    }

    @Test
    void testDotProduct() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        Vector v2 = new Vector(8.0,10.0,7.0);
        double expected = 8.0 + 20.0 + 21.0;
        assertEquals(expected, v1.dotProduct(v2), "Dot product error");
    }

    @Test
    void testScale() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        double numberScale = 56.4;
        Vector expected = new Vector(56.4,112.8,169.2);
        assertEquals(expected, v1.scale(numberScale), "Scale vector error");
    }

    @Test
    void testAdd() {
        Vector v1 = new Vector(1.0,2.0,3.0);
        Vector v2 = new Vector(8.0,10.0,7.0);
        Vector expected = new Vector(9,12,10);
        assertEquals(expected, v1.add(v2), "Add vector error");
    }
}