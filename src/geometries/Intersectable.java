package geometries;
import lighting.LightSource;
import primitives.*;
import java.util.List;

public abstract class Intersectable {

    /**
      * Finds the intersection points between a given ray and this geometry.
      * Implementing classes should calculate and return all points where
      * the ray intersects the specific geometric shape.
      * @param ray the ray to intersect with the geometry
      * @return list of intersection points, or {@code null} if no intersections are found
    */
    public final List<Point> findIntersections(Ray ray){
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }

    /**
      * A data structure representing an intersection between a ray and a geometry.
      * Contains both the point of intersection and the geometry that was hit.
      * This class is used as part of the more detailed intersection model,
      * allowing color calculations, lighting, and material-based shading.
    */
    public static class Intersection {

        /**
          * The geometry object that was intersected by the ray.
          * Used for retrieving emission color, material, and other properties at the point of intersection.
        */
        public final Geometry geometry;
        /**
          * The point in 3D space where the ray intersects the geometry.
        */
        public final Point point;
        /**
          * The material of the geometry at the intersection point.
          * Contains lighting coefficients such as kD, kS, and shininess.
          * Used for computing lighting effects like diffuse and specular reflection.
        */
        public final Material material;

        /**
          * The direction vector of the ray that intersected the geometry.
          * Points from the camera toward the intersection point.
          * Used for reflection and shading calculations.
        */
        public Vector directionRay;
        /**
         * The normal vector of the geometry at the intersection point.
         * Perpendicular to the surface; used in lighting calculations.
         */
        public Vector normalIntersection;
        /**
         * The scalar (dot) product between the ray direction and the surface normal.
         * Used to determine lighting behavior (e.g., whether the normal faces toward or away from the ray).
         */
        public double dirScaleNormal;
        /**
         * The specific light source considered for shading at this intersection.
         * Used in shadow and light intensity calculations.
         */
        public LightSource lightSource;
        /**
         * A vector from the light source to the intersection point.
         * Used to compute light attenuation and angle-based effects.
         */
        public Vector lightToIntersection;
        /**
         * The scalar (dot) product between the surface normal and the light direction.
         * Determines the intensity of the diffuse component of lighting.
         */
        public double lightScaleNormal;
        /**
          * Constructs an Intersection object with a given geometry and point.
          * @param geometry the geometry that was intersected
          * @param point the intersection point on the geometry
        */
        public Intersection(Geometry geometry, Point point , Material material) {
            this.geometry = geometry;
            this.point = point;
            this.material = material;
        }

        /**
         * Checks whether another object is equal to this intersection.
         * Two intersections are considered equal if:
         *     1. They reference the exact same geometry object (by reference).
         *     2. Their intersection points are equal (by value).
         * @param object the object to compare to
         * @return {@code true} if equal, {@code false} otherwise
         */
        @Override
        public boolean equals(Object object){
            if(this == object)
                return true;
            if(object == null || !(object instanceof Intersection))
                return false;
            Intersection other = (Intersection) object;
            return this.geometry == other.geometry && this.point.equals(other.point);
        }

        /**
          * Returns a string representation of the intersection object.
          * The string includes both the intersected geometry and the point of intersection.
          * @return a string in the format:
          *         "Intersects {geometry = ..., point = ...}"
        */
        @Override
        public String toString() {
            return "Intersects {geometry = " + this.geometry + ", point = " + this.point +"}";
        }
    }

    /**
      * Helper method to calculate detailed intersection data with a ray.
      * To be implemented by derived geometry classes.
      * @param ray the ray to check for intersection
      * @return list of intersection objects (geometry + point), or null if no intersections
    */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }

}
