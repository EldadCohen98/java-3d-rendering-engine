package renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import primitives.*;

/**
 * Regression tests for the three {@code setDirection} methods of {@link Camera.Builder}.
 * Expected direction vectors are worked out by hand from the given input.
 * Contract of {@link Camera.Builder#setDirection(Point)}: the reference up direction is +Y;
 * a view direction parallel or opposite to Y is rejected (as in the course test {@code CameraTest}).
 */
class CameraDirectionTests {
   /** Default constructor to satisfy JavaDoc generator */
   CameraDirectionTests() { /* to satisfy JavaDoc generator */ }

   /** Delta for comparing doubles */
   private static final double DELTA = 1e-10;

   /**
    * Helper: a builder with everything set except the direction
    * @param location the camera location
    * @return the builder
    */
   private Camera.Builder builder(Point location) {
      return Camera.getBuilder().setLocation(location).setVpDistance(1).setVpSize(3, 3).setResolution(3, 3);
   }

   /**
    * Helper: asserts the direction vectors of a camera
    * @param to     expected vTo
    * @param up     expected vUp
    * @param right  expected vRight
    * @param camera the camera to check
    */
   private void assertDirections(Vector to, Vector up, Vector right, Camera camera) {
      assertEquals(to, camera.getVTo(), "Wrong vTo");
      assertEquals(up, camera.getVUp(), "Wrong vUp");
      assertEquals(right, camera.getVRight(), "Wrong vRight");
   }

   /**
    * Helper: asserts that the direction vectors of a camera are normalized and orthogonal
    * @param camera the camera to check
    */
   private void assertOrthonormal(Camera camera) {
      assertEquals(1, camera.getVTo().length(), DELTA, "vTo is not normalized");
      assertEquals(1, camera.getVUp().length(), DELTA, "vUp is not normalized");
      assertEquals(1, camera.getVRight().length(), DELTA, "vRight is not normalized");
      assertEquals(0, camera.getVTo().dotProduct(camera.getVUp()), DELTA, "vTo and vUp are not orthogonal");
      assertEquals(0, camera.getVTo().dotProduct(camera.getVRight()), DELTA, "vTo and vRight are not orthogonal");
      assertEquals(0, camera.getVUp().dotProduct(camera.getVRight()), DELTA, "vUp and vRight are not orthogonal");
   }

   // ============ setDirection(Vector to, Vector up) ==============

   /** TC01: valid orthogonal vectors, including vectors that are not unit vectors */
   @Test
   void testVectorsOrthogonal() {
      // TC01.1: axis vectors with lengths 2 and 3 - normalized, right = to x up = +X
      assertDirections(new Vector(0, 0, -1), Vector.AXIS_Y, Vector.AXIS_X,
                       builder(Point.ZERO).setDirection(new Vector(0, 0, -2), new Vector(0, 3, 0)).build());
      // TC01.2: non-axis vectors: (1,1,0) . (-2,2,5) = 0, (1,1,0) x (-2,2,5) = (5,-5,4)
      Camera camera = builder(Point.ZERO).setDirection(new Vector(1, 1, 0), new Vector(-2, 2, 5)).build();
      assertDirections(new Vector(1, 1, 0).normalize(), new Vector(-2, 2, 5).normalize(), new Vector(5, -5, 4).normalize(),
                       camera);
      assertOrthonormal(camera);
   }

   /** TC02: non orthogonal, parallel, opposite or null vectors are rejected */
   @Test
   void testVectorsRejected() {
      Vector to = new Vector(0, 0, -1);
      // TC02.1: not orthogonal
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(to, new Vector(0, 1, 1)),
                   "Non orthogonal vectors should be rejected");
      // TC02.2: parallel
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(to, new Vector(0, 0, -3)),
                   "Parallel vectors should be rejected");
      // TC02.3: opposite
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(to, new Vector(0, 0, 2)),
                   "Opposite vectors should be rejected");
      // TC02.4: null
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(null, Vector.AXIS_Y),
                   "A null vTo should be rejected");
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(to, (Vector) null),
                   "A null vUp should be rejected");
   }

   // ============ setDirection(Point target) ==============

   /** TC03: targets along the axes - reference up is +Y */
   @Test
   void testTargetAxes() {
      // TC03.1: +X - right = X x Y = +Z, up = Z x X = +Y
      assertDirections(Vector.AXIS_X, Vector.AXIS_Y, Vector.AXIS_Z,
                       builder(Point.ZERO).setDirection(new Point(5, 0, 0)).build());
      // TC03.2: -X - right = -X x Y = -Z, up = -Z x -X = +Y
      assertDirections(new Vector(-1, 0, 0), Vector.AXIS_Y, new Vector(0, 0, -1),
                       builder(Point.ZERO).setDirection(new Point(-5, 0, 0)).build());
      // TC03.3: +Z - right = Z x Y = -X, up = -X x Z = +Y
      assertDirections(Vector.AXIS_Z, Vector.AXIS_Y, new Vector(-1, 0, 0),
                       builder(Point.ZERO).setDirection(new Point(0, 0, 5)).build());
      // TC03.4: -Z from a camera that is not at the origin - right = +X, up = +Y
      assertDirections(new Vector(0, 0, -1), Vector.AXIS_Y, Vector.AXIS_X,
                       builder(new Point(1, 2, 3)).setDirection(new Point(1, 2, -7)).build());
      // TC03.5: +Y and -Y - parallel / opposite to the reference up, rejected with a clear exception
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(new Point(0, 5, 0)),
                   "A view direction along +Y needs an explicit up vector");
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(new Point(0, -5, 0)),
                   "A view direction along -Y needs an explicit up vector");
   }

   /** TC04: looking towards -Z with a target only gives vUp=+Y and vRight=+X */
   @Test
   void testTargetMinusZ() {
      Camera camera = builder(Point.ZERO).setDirection(new Point(0, 0, -10)).build();
      assertEquals(Vector.AXIS_Y, camera.getVUp(), "Wrong vUp");
      assertEquals(Vector.AXIS_X, camera.getVRight(), "Wrong vRight");
   }

   /**
    * TC05: diagonal target (1,2,-2) from the origin:
    * vTo = (1,2,-2)/3, vRight = vTo x Y normalized = (2,0,1)/sqrt5 (horizontal, to the right of the
    * view direction), vUp = vRight x vTo = (-2,5,4)/(3 sqrt5) (positive Y component)
    */
   @Test
   void testTargetDiagonal() {
      Camera camera = builder(Point.ZERO).setDirection(new Point(1, 2, -2)).build();
      assertOrthonormal(camera);
      assertDirections(new Vector(1, 2, -2).normalize(), new Vector(-2, 5, 4).normalize(), new Vector(2, 0, 1).normalize(),
                       camera);
      assertEquals(0, camera.getVRight().dotProduct(Vector.AXIS_Y), DELTA, "vRight should be horizontal");
      assertTrue(camera.getVUp().dotProduct(Vector.AXIS_Y) > 0, "vUp should point upwards");
   }

   // ============ setDirection(Point target, Vector up) ==============

   /** TC06: target with a reference up vector */
   @Test
   void testTargetAndUp() {
      Point target = new Point(0, 0, -10);
      // TC06.1: a reference up that is not orthogonal to the view direction - vUp is derived from it
      Camera camera = builder(Point.ZERO).setDirection(target, new Vector(0, 1, 1)).build();
      assertDirections(new Vector(0, 0, -1), Vector.AXIS_Y, Vector.AXIS_X, camera);
      assertOrthonormal(camera);
      // TC06.2: up parallel to the view direction
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(target, new Vector(0, 0, -1)),
                   "An up vector parallel to the view direction should be rejected");
      // TC06.3: up opposite to the view direction
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(target, new Vector(0, 0, 5)),
                   "An up vector opposite to the view direction should be rejected");
   }

   /**
    * TC10: target along +Y with an explicit up +Z (the case that the target-only version rejects):
    * vTo = +Y, vRight = Y x Z = +X, vUp = X x Y = +Z
    */
   @Test
   void testTargetPlusYWithUpZ() {
      Point target = new Point(0, 5, 0);
      Camera camera = assertDoesNotThrow(() -> builder(Point.ZERO).setDirection(target, Vector.AXIS_Z).build(),
                                         "Building should succeed with an explicit up vector");
      assertDirections(Vector.AXIS_Y, Vector.AXIS_Z, Vector.AXIS_X, camera);
      assertOrthonormal(camera);
      assertEquals(new Ray(Point.ZERO, Vector.AXIS_Y), camera.constructRay(3, 3, 1, 1), "Bad central ray");
   }

   /**
    * TC11: target along -Y with an explicit up +Z (the case that the target-only version rejects):
    * vTo = -Y, vRight = -Y x Z = -X, vUp = -X x -Y = +Z
    */
   @Test
   void testTargetMinusYWithUpZ() {
      Point target = new Point(0, -5, 0);
      Camera camera = assertDoesNotThrow(() -> builder(Point.ZERO).setDirection(target, Vector.AXIS_Z).build(),
                                         "Building should succeed with an explicit up vector");
      assertDirections(new Vector(0, -1, 0), Vector.AXIS_Z, new Vector(-1, 0, 0), camera);
      assertOrthonormal(camera);
      assertEquals(new Ray(Point.ZERO, new Vector(0, -1, 0)), camera.constructRay(3, 3, 1, 1), "Bad central ray");
   }

   // ============ Invalid input ==============

   /** TC07: invalid input of the target versions */
   @Test
   void testTargetInvalidInput() {
      // TC07.1: null target
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection((Point) null),
                   "A null target should be rejected");
      // (the cast selects setDirection(Point, Vector) - a plain null would select setDirection(Vector, Vector))
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection((Point) null, Vector.AXIS_Y),
                   "A null target should be rejected");
      // TC07.2: null up
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(new Point(0, 0, -1), null),
                   "A null up vector should be rejected");
      // TC07.3: target equals the camera location
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(Point.ZERO),
                   "A target at the camera location should be rejected");
      assertThrows(IllegalArgumentException.class, () -> builder(Point.ZERO).setDirection(Point.ZERO, Vector.AXIS_Y),
                   "A target at the camera location should be rejected");
      // TC07.4: the camera location is not set yet
      assertThrows(IllegalStateException.class, () -> Camera.getBuilder().setDirection(new Point(0, 0, -1)),
                   "The location must be set before a target");
      assertThrows(IllegalStateException.class, () -> Camera.getBuilder().setDirection(new Point(0, 0, -1), Vector.AXIS_Y),
                   "The location must be set before a target");
   }

   // ============ Central ray ==============

   /** TC08: the central ray of a 3x3 view plane points at the target */
   @Test
   void testCentralRay() {
      Point location = new Point(1, 2, 3);
      Point target   = new Point(4, -2, -9);
      Vector expected = target.subtract(location).normalize();
      // TC08.1: target only
      assertEquals(new Ray(location, expected),
                   builder(location).setDirection(target).build().constructRay(3, 3, 1, 1), "Bad central ray");
      // TC08.2: target and up
      assertEquals(new Ray(location, expected),
                   builder(location).setDirection(target, new Vector(1, 1, 1)).build().constructRay(3, 3, 1, 1),
                   "Bad central ray");
   }

   // ============ Failed call ==============

   /** TC09: an invalid call after a valid setting does not change the previous setting */
   @Test
   void testFailedCallKeepsSetting() {
      Camera.Builder builder = builder(Point.ZERO).setDirection(new Vector(0, 0, -1), Vector.AXIS_Y);

      // TC09.1: non orthogonal vectors
      assertThrows(IllegalArgumentException.class, () -> builder.setDirection(new Vector(1, 0, 0), new Vector(1, 1, 0)));
      assertDirections(new Vector(0, 0, -1), Vector.AXIS_Y, Vector.AXIS_X, builder.build());
      // TC09.2: target along Y
      assertThrows(IllegalArgumentException.class, () -> builder.setDirection(new Point(0, 7, 0)));
      assertDirections(new Vector(0, 0, -1), Vector.AXIS_Y, Vector.AXIS_X, builder.build());
      // TC09.3: target with an up vector parallel to the view direction
      assertThrows(IllegalArgumentException.class, () -> builder.setDirection(new Point(5, 0, 0), new Vector(-1, 0, 0)));
      assertDirections(new Vector(0, 0, -1), Vector.AXIS_Y, Vector.AXIS_X, builder.build());
   }
}
