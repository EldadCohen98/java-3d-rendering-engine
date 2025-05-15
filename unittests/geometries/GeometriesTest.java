package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeometriesTest {

    @Test
    void testFindIntersections() {
        //set the geometries bodies
        Plane plane = new Plane(new Point(1,0,0), new Point(0,1,0), new Point(0,0,1));
        Sphere sphere = new Sphere (new Point(1,0,0), 2 );
        Triangle triangle = new Triangle(new Point(-1, 3, -1), new Point(-2, -1.5, -1), new Point(0,0,2));


        //------------------------------------// ===== TC1 ===== \\------------------------------------\\
        //set a ray
        Ray crossAllray = new Ray (new Point(-2, 1, 0), new Vector(1,0,0.5));
        List<Point> planIntersectionTC1 = plane.findIntersections(crossAllray);
        List<Point> sphereIntersectionTC1 = sphere.findIntersections(crossAllray);
        List<Point> triangleIntersectionTC1 = triangle.findIntersections(crossAllray);
        List<Point> allIntersectionsTC1 = new LinkedList<>();

        if(planIntersectionTC1 != null)
            allIntersectionsTC1.addAll(planIntersectionTC1);
        if(sphereIntersectionTC1 != null)
            allIntersectionsTC1.addAll(sphereIntersectionTC1);
        if(triangleIntersectionTC1 != null)
            allIntersectionsTC1.addAll(triangleIntersectionTC1);

        Geometries geometriesTC1 = new Geometries();
        geometriesTC1.add(plane, sphere, triangle);

        var resultTC1 = geometriesTC1.findIntersections(crossAllray);

        assertNotNull(resultTC1, "geometries is null");
        assertEquals(allIntersectionsTC1.size(), resultTC1.size(), "Intersections did not match");


        //------------------------------------// ===== TC2 ===== \\------------------------------------\\
        //set a ray
        Ray crossTowRay = new Ray (new Point(2.5,2.5,0), new Vector(-1,-1,0));
        List<Point> planIntersectionTC2 = plane.findIntersections(crossTowRay);
        List<Point> sphereIntersectionTC2 = sphere.findIntersections(crossTowRay);
        List<Point> triangleIntersectionTC2 = triangle.findIntersections(crossTowRay);
        List<Point> allIntersectionsTC2 = new LinkedList<>();

        if(planIntersectionTC2 != null)
            allIntersectionsTC2.addAll(planIntersectionTC2);
        if(sphereIntersectionTC2 != null)
            allIntersectionsTC2.addAll(sphereIntersectionTC2);
        if(triangleIntersectionTC2 != null)
            allIntersectionsTC2.addAll(triangleIntersectionTC2);

        Geometries geometriesTC2 = new Geometries(plane, sphere, triangle);

        var resultTC2 = geometriesTC2.findIntersections(crossTowRay);
        System.out.println("resultTC2 "+resultTC2);
        assertNotNull(resultTC2, "geometries is null");
        assertEquals(allIntersectionsTC2.size(), resultTC2.size(), "The number of intersection points is different.");


        //------------------------------------// ===== TC3 ===== \\------------------------------------\\
        //set a ray
        Ray crossOneRay = new Ray (new Point(5,0,0), new Vector(0,-4,0));
        List<Point> planIntersectionTC3 = plane.findIntersections(crossOneRay);
        List<Point> sphereIntersectionTC3 = sphere.findIntersections(crossOneRay);
        List<Point> triangleIntersectionTC3 = triangle.findIntersections(crossOneRay);
        List<Point> allIntersectionsTC3 = new LinkedList<>();

        if(planIntersectionTC3 != null)
            allIntersectionsTC3.addAll(planIntersectionTC3);
        if(sphereIntersectionTC3 != null)
            allIntersectionsTC3.addAll(sphereIntersectionTC3);
        if(triangleIntersectionTC3 != null)
            allIntersectionsTC3.addAll(triangleIntersectionTC3);

        Geometries geometriesTC3 = new Geometries();
        geometriesTC3.add(plane);
        geometriesTC3.add(sphere);
        geometriesTC3.add(triangle);

        var resultTC3 = geometriesTC3.findIntersections(crossOneRay);

        assertNotNull(resultTC3, "geometries is null");
        assertEquals(allIntersectionsTC3.size(), resultTC3.size(),  "The number of intersection points is different.");


        //------------------------------------// ===== TC4 ===== \\------------------------------------\\
        //set a ray
        Ray noneCrossRay = new Ray (new Point(5,0,0), new Vector(0,5,0));
        List<Point> planIntersectionTC4 = plane.findIntersections(noneCrossRay);
        List<Point> sphereIntersectionTC4 = sphere.findIntersections(noneCrossRay);
        List<Point> triangleIntersectionTC4 = triangle.findIntersections(noneCrossRay);
        List<Point> allIntersectionsTC4 = new LinkedList<>();

        if(planIntersectionTC4 != null)
            allIntersectionsTC4.addAll(planIntersectionTC4);
        if(sphereIntersectionTC4 != null)
            allIntersectionsTC4.addAll(sphereIntersectionTC4);
        if(triangleIntersectionTC4 != null)
            allIntersectionsTC4.addAll(triangleIntersectionTC4);

        if(allIntersectionsTC4.isEmpty())
            allIntersectionsTC4 = null;

        Geometries geometriesTC4 = new Geometries();
        geometriesTC4.add(plane);
        geometriesTC4.add(sphere);
        geometriesTC4.add(triangle);

        var resultTC4 = geometriesTC4.findIntersections(noneCrossRay);

        assertNull(resultTC4, "A ray that does not intersect the geometric bodies at all should contain a null value.");


        //------------------------------------// ===== TC5 ===== \\------------------------------------\\
        //set a ray
        Geometries geometriesTC5 = new Geometries();
        Ray noGeometriesRay = new Ray (new Point(5,0,0), new Vector(0,-5,0));
        assertNull(geometriesTC5.findIntersections(noGeometriesRay), "When there are no geometric bodies, there can be no intersection points.");

    }
}