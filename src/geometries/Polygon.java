package geometries;

import static java.lang.Double.*;

import java.util.ArrayList;
import java.util.List;
import static primitives.Util.*;
import primitives.*;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 * @author Dan
 */
public class Polygon extends Geometry {
   /** List of polygon's vertices */
   protected final List<Point> vertices;
   /** Associated plane in which the polygon lays */
   protected final Plane       plane;
   /** The size of the polygon - the amount of the vertices in the polygon */
   private final int           size;


   /**
    * Polygon constructor based on vertices list. The list must be ordered by edge
    * path. The polygon must be convex.
    * @param  vertices                 list of vertices according to their order by
    *                                  edge path
    * @throws IllegalArgumentException in any case of illegal combination of
    *                                  vertices:
    *                                  <ul>
    *                                  <li>Less than 3 vertices</li>
    *                                  <li>Consequent vertices are in the same
    *                                  point
    *                                  <li>The vertices are not in the same
    *                                  plane</li>
    *                                  <li>The order of vertices is not according
    *                                  to edge path</li>
    *                                  <li>Three consequent vertices lay in the
    *                                  same line (180&#176; angle between two
    *                                  consequent edges)
    *                                  <li>The polygon is concave (not convex)</li>
    *                                  </ul>
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      this.vertices = List.of(vertices);
      size          = vertices.length;

      // Generate the plane according to the first three vertices and associate the
      // polygon with this plane.
      // The plane holds the invariant normal (orthogonal unit) vector to the polygon
      plane         = new Plane(vertices[0], vertices[1], vertices[2]);
      if (size == 3) return; // no need for more tests for a Triangle

      Vector  n        = plane.getNormal(vertices[0]);
      // Subtracting any subsequent points will throw an IllegalArgumentException
      // because of Zero Vector if they are in the same point
      Vector  edge1    = vertices[size - 1].subtract(vertices[size - 2]);
      Vector  edge2    = vertices[0].subtract(vertices[size - 1]);

      // Cross Product of any subsequent edges will throw an IllegalArgumentException
      // because of Zero Vector if they connect three vertices that lay in the same
      // line.
      // Generate the direction of the polygon according to the angle between last and
      // first edge being less than 180deg. It is hold by the sign of its dot product
      // with the normal. If all the rest consequent edges will generate the same sign
      // - the polygon is convex ("kamur" in Hebrew).
      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < size; ++i) {
         // Test that the point is in the same plane as calculated originally
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
         // Test the consequent edges have
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }
   }

   /**
     * Returns the normal vector to the polygon at the given point.
     * Since the polygon lies entirely in a single plane, the normal is constant
     * and is delegated to the associated {@link Plane}.
     * @param point the point on the polygon (not used in the calculation)
     * @return the normalized normal vector of the polygon's plane
   */
   @Override
   public Vector getNormal(Point point) { return plane.getNormal(point); }

   /**
    * Calculates the intersections between a given {@link Ray} and this convex polygon.
    * This method checks whether the ray intersects the polygon's plane and,
    * if so, determines whether the intersection point lies inside the polygon's boundaries.
    * The logic includes:
    *     Computing the intersection point with the plane
    *     Rejecting points that lie directly on a vertex or an edge<
    *     Using cross products and dot products to determine if the point is within the polygon<
    * If the intersection point is valid (inside the polygon), it returns a list containing
    * a single {@link Intersection} object with this geometry and the point.
    * Otherwise, it returns {@code null}.
    *
    * @param ray the {@link Ray} to test for intersection with the polygon
    * @return a list containing the intersection with the polygon,
    *         or {@code null} if there is no valid intersection
    */
   @Override
   public List<Intersection> calculateIntersectionsHelper(Ray ray) {
      List<Intersection> intersections = plane.calculateIntersectionsHelper(ray);
      //If the ray does not intersect the plane of the triangle
      if(intersections == null){
         return null;
      }

      //The point of intersection from the plane of the triangle
      Point intersectionPoint = intersections.getFirst().point;

      //Checking the equality of the point to one of the vertices
      for(int i=0; i<this.vertices.size(); i++)
         if (Util.isZero(intersectionPoint.distance(this.vertices.get(i))))
            return null;

      //Checking if the intersection point is on the edge
      if((this.isOnEdge(intersectionPoint, this.vertices.getFirst(), this.vertices.getLast())))
         return null;
      for(int j=0; j+1<this.vertices.size(); j++)
         if((this.isOnEdge(intersectionPoint, this.vertices.get(j+1), this.vertices.get(j))))
            return null;

      //vectors from the sides of the triangle.
      List<Vector> edges = new ArrayList<>();
      Vector edge;
      for(int x=0; x+1<this.vertices.size(); x++) {
         edge = this.vertices.get(x + 1).subtract(this.vertices.get(x));
         edges.add(edge);
      }
      edge = this.vertices.getFirst().subtract(this.vertices.getLast());
      edges.add(edge);

      //Vectors from the intersection point to the vertices of the triangle
      List<Vector> vertexToIntersection = new ArrayList<>();
      for(int y=0; y<this.vertices.size(); y++) {
         Vector ToIntersection = intersectionPoint.subtract(this.vertices.get(y));
         if(Util.isZero(ToIntersection.length()))
            return null;
         vertexToIntersection.add(ToIntersection);
      }
      //Cross product between the sides and the vectors at the intersection point
      Vector planeNormal = plane.getNormal(intersectionPoint);

      List<Vector> localNormals = new ArrayList<>();
      for(int k=0; k<this.vertices.size(); k++) {

         if(Util.isZero(edges.get(k).length()) || Util.isZero(vertexToIntersection.get(k).length()))
            return null;
         Vector v1 = edges.get(k).normalize();
         Vector v2 = vertexToIntersection.get(k).normalize();
         if(v1.equals(v2) || v1.equals(v2.scale((-1))))
            return null;

         Vector localNormal = edges.get(k).crossProduct(vertexToIntersection.get(k));
         localNormals.add(localNormal);
      }
      //Dot product between the local normals and the principal normal
      List<Double> results = new ArrayList<>();
      double result = 0;
      for(Vector pointNormal : localNormals) {
         result = pointNormal.dotProduct(planeNormal);
         results.add(result);
      }

      //Check that all results have the same sign
      if(results.getFirst() > 0)
         for(double number : results) {
            if(Util.alignZero(number) <= 0)
               return null;
         }
      if(results.getFirst() < 0)
         for(double number : results) {
            if(Util.alignZero(number) >= 0)
               return null;
         }

      return List.of(new Intersection(this, intersectionPoint, this.getMaterial()));
   }

   /**
    * Checks whether a given point lies exactly on the edge defined by two triangle vertices.
    * The method computes the sum of distances from the intersection point to each vertex,
    * and compares it to the total edge length. If the sum equals the edge length (within numerical tolerance),
    * the point lies on the edge.

    * @param intersection The point to check.
    * @param vertexA One vertex of the edge.
    * @param vertexB The other vertex of the edge.
    * @return {@code true} if the point lies exactly on the edge between the two vertices; {@code false} otherwise.
    */
   public boolean isOnEdge(Point intersection, Point vertexA, Point vertexB) {
      double edgeLength = vertexA.subtract(vertexB).length();
      double intersectionToVertexA = intersection.distance(vertexA);
      double intersectionToVertexB = intersection.distance(vertexB);
      if(Util.isZero(((intersectionToVertexA + intersectionToVertexB) - (edgeLength))))
         return true;
      return false;
   }

}
