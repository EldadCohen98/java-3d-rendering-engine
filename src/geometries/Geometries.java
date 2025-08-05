package geometries;

import primitives.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
  * A composite geometry class that aggregates multiple geometries into a single collection.
  * This class implements the Composite Design Pattern and allows intersecting a ray with all
  * contained geometries as if they were a single unit.
*/
public class Geometries extends Intersectable{

    /**A list containing all geometries in the scene.*/
    private final List<Intersectable> geo = new LinkedList<>();

    /**
      * Constructs an empty collection of geometries.
    */
    public Geometries(){}

    /**
      * Constructs a geometry collection and adds the given geometries to it.
      * @param geometries geometries to add to the collection
    */
    public Geometries(Intersectable...geometries){
        this.add(geometries);
    }

    /**
      * Adds one or more geometries to the collection.
      * @param geometries the geometries to add
    */
    public void add(Intersectable...geometries){
        Collections.addAll(geo, geometries);
    }

    /**
     * Calculates the intersections between a given ray and all geometries in the collection.
     * This method is part of the internal intersection mechanism (NVI) and returns detailed
     * intersection information, including the geometry intersected and the point of intersection.
     * @param ray the ray to test for intersections
     * @return a list of {@link Intersection} objects containing both the intersected geometry
     *         and the point of intersection, or {@code null} if no intersections are found
     */
    @Override
    public List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> allIntersections = new LinkedList<>();

        for(Intersectable geometry : this.geo){
            List<Intersection> currentIntersections = geometry.calculateIntersections(ray);
            if(currentIntersections != null)
                allIntersections.addAll(currentIntersections);
        }

        if(allIntersections.isEmpty())
            return null;

        return allIntersections;
    }
}
