package primitives;

/**
  * Material is a passive data structure (PDS) that holds the material properties
  * of a geometry. It defines how the geometry responds to different lighting effects.
  * currently includes ambient reflection coefficient (kA).
  * Future extensions may include diffuse (kD), specular (kS), shininess, etc.
 */
public class Material {

    //=========================== Fields =========================== \\
    /**
      * Ambient reflection coefficient – determines how much ambient light is reflected.
      * Represented as a Double3 to allow different reflection per RGB channel.
    */
    public Double3 kA = Double3.ONE;

    /**
     * Diffuse reflection coefficient (kD) –
     * controls how much light is scattered when hitting the surface.
     * Represented as a Double3 for per-channel control (R, G, B).
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Specular reflection coefficient (kS) –
     * controls how much mirror-like reflection the surface produces.
     * Represented as a Double3 for per-channel control (R, G, B).
     */
    public Double3 kS = Double3.ZERO;

    /**
     * Shininess factor (nShininess) –
     * determines how concentrated the specular highlight is.
     * Higher values mean sharper, smaller highlights.
     */
    public int nShininess = 0;

    //=========================== Function =========================== \\

    /**
      * Sets the ambient reflection coefficient using a Double3 value.
      * @param kA the ambient reflection coefficient per RGB channel
      * @return the current Material object (for builder-style chaining)
    */
    public Material setkA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
      * Sets the ambient reflection coefficient using a single double value
      * (applied equally to all RGB channels).
      * @param kA the scalar ambient reflection coefficient
      * @return the current Material object (for builder-style chaining)
    */
    public Material setkA(double kA) {
        return setkA(new Double3(kA));
    }


    /**
      * Sets the diffuse reflection coefficient (kD) using a Double3.
      * @param kD the diffuse reflection coefficient per RGB channel
      * @return the current Material object (for builder-style chaining)
    */
    public Material setkD(Double3 kD) {
        this.kD = kD;
        return this;
    }
    /**
      * Sets the diffuse reflection coefficient (kD) using a scalar value.
      * @param kD the diffuse reflection coefficient (applied equally to R, G, B)
      * @return the current Material object (for builder-style chaining)
    */
    public Material setkD(double kD) {
        return setkD(new Double3(kD));
    }

    /**
      * Sets the specular reflection coefficient (kS) using a Double3.
      * @param kS the specular reflection coefficient per RGB channel
      * @return the current Material object (for builder-style chaining)
    */
    public Material setkS(Double3 kS) {
        this.kS = kS;
        return this;
    }
    /**
      * Sets the specular reflection coefficient (kS) using a scalar value.
      *  @param kS the specular reflection coefficient (applied equally to R, G, B)
      * @return the current Material object (for builder-style chaining)
    */
    public Material setkS(double kS) {
        return setkS(new Double3(kS));
    }

    /**
      * Sets the shininess factor of the material.
      * Higher values produce smaller and sharper highlights.
      * @param nSh the shininess factor
      * @return the current Material object (for builder-style chaining)
    */
    public Material setShininess(int nSh) {
        this.nShininess = nSh;
        return this;
    }
}
