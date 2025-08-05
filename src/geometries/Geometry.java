package geometries;

import primitives.*;

import java.util.List;

/**
 * Abstract base class for all geometric objects.
 * Defines a common interface for retrieving the normal vector at a given point on the surface.
 */
public abstract class Geometry extends Intersectable{
            /** ================ Fields ================ *\
    /**
      * The emission color of the geometry.
      * This represents the inherent light emitted from the object itself
      * (used to simulate glowing or colored surfaces).
      * Default is black (no emission).
    */
    protected Color emission = Color.BLACK;

    /**
      * The material of the geometry, which defines how it interacts with light.
      * Includes ambient, diffuse, and specular reflection coefficients.
      * Initialized by default with a basic material.
    */
    private Material material = new Material();

            /** ================ Function ================ *\
    /**
      * Returns the emission color of the geometry.
      * @return the emission color
    */
    public Color getEmission() {
        return this.emission;
    }

    /**
      * Sets the emission color of the geometry.
      * This method follows the builder pattern.
      * @param useEmission the emission color to set
      * @return the geometry itself (for method chaining)
    */
    public Geometry setEmission(Color useEmission) {
        this.emission = useEmission;
        return this;
    }

    /**
     * Returns the normal vector to the geometry at the given point.
     * @param point The point on the geometry surface
     * @return The normal vector at the point
     */
    public abstract Vector getNormal(Point point);

    /**
      * Returns the material of the geometry.
      * @return the material object defining light interaction properties
    */
    public Material getMaterial() {
        return this.material;
    }

    /**
      * Sets the material of the geometry (builder-style).
      * @param useMaterial the material to use
      * @return the current Geometry object (for method chaining)
    */
    public Geometry setMaterial(Material useMaterial) {
        this.material = useMaterial;
        return this;
    }
}
