package renderer;
import primitives.*;
import geometries.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
  * Integration tests for the {@link Camera} class and its {@code constructRay} method,
  * in combination with various geometric shapes.
  * The goal of these tests is to verify that the camera constructs rays correctly through
  * the view plane pixels and that these rays intersect with geometries (Sphere, Plane, Triangle)
  * as expected.
  * For each geometry:
  *     A camera is set up at the origin, looking toward the negative Z-axis.
  *     The view plane is located at a fixed distance in front of the camera.
  *     Rays are constructed through each pixel, and the total number of intersection
  *         points with the geometry is counted.
  *     The result it asserted against the expected number of intersections.
  * These tests serve as integration checks between the camera ray-construction logic
  * and the intersection implementations of geometric shapes.
  * Geometries tested:
  *     {@link geometries.Sphere}
  *     {@link geometries.Plane}
  *     {@link geometries.Triangle}
  * The tests assume a resolution of 3x3 pixels on a view plane of size 3x3 units,
  * positioned 4 units away from the camera.
*/
public class CameraIntersectionsIntegrationTests {

    /**
      * Integration test for {@link Camera#constructRay(int, int, int, int)} using a {@link Sphere}.
      * This test verifies the total number of intersection points between rays constructed by the camera
      * (one ray through each pixel in the view plane) and a sphere placed in front of the camera.
      * Test Setup:
      *    Camera is located at the origin (0, 0, 0).
      *    Camera looks toward negative Z-axis (vTo = (0, 0, -1)) with vUp = (0, 1, 0).
      *    View plane is at distance 4 from the camera, with size 3x3 and resolution 3x3 pixels.
      *    A sphere of radius 2 is centered at (0, 0, -5).
      * Test Goal:
      * Count how many intersection points are found when casting a ray from the camera through each pixel
      * in the view plane and checking for intersections with the sphere.
      * Expected Result:
      * The number of total intersection points is known in advance and asserted against.
    */
    @Test
    void testCameraSphereIntersections() {
        //building the camera and view plan
        Point p0 = new Point(0,0,0);
        Vector to = new Vector(0,0,-1);
        Vector up = new Vector(0,1,0);
        int distance = 4;
        double viewPlanWidth = 3;
        double viewPlanHeight = 3;
        int nX = 3;
        int nY = 3;

         Camera sphereCamera = Camera.getBuilder()
                 .setLocation(p0)
                 .setDirection(to ,up)
                 .setVpDistance(distance)
                 .setResolution(nX,nY)
                 .setVpSize(viewPlanWidth,viewPlanHeight)
                 .build();
        //building the sphere
        Sphere sphere = new Sphere(new Point(0,0,-5), 2);

        //Building a view plan and counting how many intersection points for each ray.
        int countIntersections = 0;
        for (int i=0; i<nX; i++){
            for (int j=0; j<nY; j++){
                Ray rayVP = sphereCamera.constructRay(nX,nY,i,j);
                List<Point> listIntersection = sphere.findIntersections(rayVP);
                if(listIntersection != null)
                    countIntersections = countIntersections + listIntersection.size();
            }
        }
        //equality check
        assertEquals(18 ,countIntersections, "Count of intersections are not equal");
    }

    /**
      * Integration test for {@link Camera#constructRay(int, int, int, int)} with a {@link Plane}.
      * This test verifies that the camera correctly constructs rays through each pixel in the view plane,
      * and that these rays correctly intersect with a plane positioned in front of the camera.
      * Test Setup:
      * - Camera is located at the origin (0, 0, 0).
      * - Camera faces the negative Z-axis with the up direction along positive Y-axis.
      * - View plane is located at distance 4 from the camera, with size 3x3 and resolution 3x3 pixels.
      * - A plane is located at z = -8 (in front of the view plane), perpendicular to the Z-axis.
      * Expected Behavior:
      * - Each of the 9 rays should intersect the plane exactly once.
      * Assertion:
      * - Verifies that the total number of intersection points is exactly 9.
    */
    @Test
    void testCameraPlaneIntersections() {
        //building the camera and view plan
        Point p0 = new Point(0,0,0);
        Vector to = new Vector(0,0,-1);
        Vector up = new Vector(0,1,0);
        int distance = 4;
        double viewPlanWidth = 3;
        double viewPlanHeight = 3;
        int nX = 3;
        int nY = 3;

        Camera planCamera = Camera.getBuilder()
                .setLocation(p0)
                .setDirection(to ,up)
                .setVpDistance(distance)
                .setResolution(nX,nY)
                .setVpSize(viewPlanWidth,viewPlanHeight)
                .build();
        //building the plan
        Plane plane = new Plane(new Point(0,0,-8), new Point(0,4,-8), new Point(3,0,-8));

        //Building a view plan and counting how many intersection points for each ray.
        int planCountIntersections = 0;
        for (int i=0; i<nX; i++){
            for (int j=0; j<nY; j++){
                Ray rayVP = planCamera.constructRay(nX,nY,i,j);
                List<Point> listIntersection = plane.findIntersections(rayVP);
                if(listIntersection != null)
                    planCountIntersections = planCountIntersections + listIntersection.size();
            }
        }
        //equality check
        assertEquals(9 ,planCountIntersections, "Count of intersections are not equal");
    }

    /**
      * Integration test for {@link Camera#constructRay(int, int, int, int)} with a {@link Triangle}.
      * This test checks that the rays constructed by the camera intersect the triangle as expected.
      * Test Setup:
      *  - Camera is at origin (0,0,0), looking in the -Z direction, with up vector in +Y.
      *  - View plane is placed at distance 4 from the camera with 3x3 resolution and size 3x3.
      *  - A triangle is positioned further along the Z-axis at z = -6, slightly offset on X and Y axes.
      * Expected Behavior:
      *  - Rays are sent through each pixel.
      *  - The total number of intersection points with the triangle is expected to be 4.
      * Assertion:
      *  - Verifies that the actual number of intersection points equals the expected number (4).
    */
    @Test
    void testCameraTriangleIntersections() {
        //building the camera and view plan
        Point p0 = new Point(0,0,0);
        Vector to = new Vector(0,0,-1);
        Vector up = new Vector(0,1,0);
        int distance = 4;
        double viewPlanWidth = 3;
        double viewPlanHeight = 3;
        int nX = 3;
        int nY = 3;

        Camera triangleCamera = Camera.getBuilder()
                .setLocation(p0)
                .setDirection(to ,up)
                .setVpDistance(distance)
                .setResolution(nX,nY)
                .setVpSize(viewPlanWidth,viewPlanHeight)
                .build();
        //building the triangle
        Triangle triangle = new Triangle(new Point(-0.5,-0.5,-6), new Point(0,4,-6), new Point(3,-0.5,-6));

        //Building a view plan and counting how many intersection points for each ray.
        int triangleCountIntersections = 0;
        for (int i=0; i<nX; i++){
            for (int j=0; j<nY; j++){
                Ray rayVP = triangleCamera.constructRay(nX,nY,i,j);
                List<Point> listIntersection = triangle.findIntersections(rayVP);
                if(listIntersection != null)
                    triangleCountIntersections = triangleCountIntersections + listIntersection.size();
            }
        }
        //equality check
        assertEquals(4 ,triangleCountIntersections, "Count of intersections are not equal");
    }
}
