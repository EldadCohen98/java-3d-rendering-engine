package renderer;

import geometries.Intersectable;
import lighting.*;
import primitives.*;
import scene.Scene;
import java.util.List;
import geometries.Intersectable. Intersection;

/**
  * A simple ray tracer that implements basic ray tracing logic.
  * This version supports emission, ambient light, local lighting (Phong model),
  * shadows (partial, when the blocking geometries are transparent),
  * and the global effects of reflection and transparency (computed recursively),
  * using the closest intersection.
  * Direction conventions: L = light → point (as returned by {@link LightSource#getL(Point)}),
  * V = camera → point, N = the geometry normal at the point.
*/
public class SimpleRayTracer extends  RayTracerBase{

    /**
      * The maximal number of surfaces on the path of a ray: the first hit and the global effects that follow it.
      * The color of a hit at the last level has no global effects.
    */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /**
      * The accumulated attenuation (the product of the kR/kT coefficients along the path) below which,
      * in all three color channels, a ray is no longer followed.
    */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /** The accumulated attenuation of the ray that leaves the camera: no attenuation */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
      * Constructs a SimpleRayTracer with the given scene.
      * @param scene the scene to render using simple ray tracing
    */
    public SimpleRayTracer (Scene scene) {
        super(scene);
    }

    /*1*/
    /**
      * Traces a ray through the scene to find the closest intersection point.
      * Returns the ambient light color at the intersection,
      * or the background color if no intersection is found.
      * @param ray the ray to trace
      * @return the resulting color at the intersection point
    */
    public Color traceRay (Ray ray){
        Intersection closestIntersection = findClosestIntersection(ray);

        if(closestIntersection == null)
            return this.scene.background;

        return this.calcColor(closestIntersection, ray);
    }

    /**
      * Finds the intersection of the ray with the scene's geometries that is closest to the ray's head.
      * @param ray the ray to intersect with the scene
      * @return the closest intersection, or {@code null} if the ray intersects no geometry
    */
    private Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = this.scene.geometries.calculateIntersections(ray);

        return intersections == null ? null : ray.findClosestIntersection(intersections);
    }

    /*2*/
    /**
     * Calculates the color at the first intersection point of a ray that left the camera:
     * the color of the point with its local and global effects (see
     * {@link #calcColor(Intersection, int, Double3)}), plus the ambient light scaled by kA.
     * The intersection is preprocessed here, with the direction of the ray that hit it.
     * The ambient light is added here only, once, and not in the recursion of the global effects.
     * This is an assumption that was not yet verified against the course material.
     *
     * @param intersection the intersection information (point, geometry, etc.)
     * @param ray the ray that hit the point (used to compute the viewing direction)
     * @return the color at the point
     */
    private Color calcColor(Intersection intersection, Ray ray){
        preprocessIntersection(intersection, ray.getDirection());

        Color ambient = scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().kA);
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K).add(ambient);
    }

    /*2.2*/
    /**
     * Calculates the color at the intersection point, without the ambient light:
     * the emission, plus the local effects (diffuse and specular) of every light that is not shadowed,
     * plus - unless this is the last level - the global effects of reflection and transparency.
     * A ray that is tangent to the surface (N·V = 0) keeps its emission and its global effects:
     * no light reaches the point (N·L can't have the sign of N·V), but the ray continues in the direction V.
     * The intersection must be already preprocessed (see {@link #preprocessIntersection}) with the direction
     * of the ray that hit it: this is done by the callers, which know that ray.
     *
     * @param intersection the preprocessed intersection information (point, normal, direction of the ray, etc.)
     * @param level the number of surfaces that can still be handled, including this one
     * @param k the accumulated attenuation (the product of the kR/kT coefficients) of the path to this point
     * @return the color at the point, before the ambient light
     */
    private Color calcColor(Intersection intersection, int level, Double3 k){
        Color color = intersection.geometry.getEmission().add(calcColorLocalEffects(intersection, k));
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /*2.3*/
    /**
     * Calculates the global effects at the intersection point: the reflection and the transparency.
     * A ray is built and followed only if its accumulated attenuation, the product of {@code k} and
     * the coefficient of the effect, is not lower than {@link #MIN_CALC_COLOR_K} in all three channels at once.
     *
     * @param intersection the preprocessed intersection information
     * @param level the number of surfaces that can still be handled, including this one (at least 2)
     * @param k the accumulated attenuation of the path to this point
     * @return the color of the reflection and of the transparency, attenuated by kR and kT
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k){
        Material material = intersection.geometry.getMaterial();
        Color color = Color.BLACK;

        Double3 kkr = k.product(material.kR);
        if (!kkr.lowerThan(MIN_CALC_COLOR_K))
            color = color.add(calcGlobalEffect(constructReflectedRay(intersection), level, kkr, material.kR));

        Double3 kkt = k.product(material.kT);
        if (!kkt.lowerThan(MIN_CALC_COLOR_K))
            color = color.add(calcGlobalEffect(constructRefractedRay(intersection), level, kkt, material.kT));

        return color;
    }

    /*2.4*/
    /**
     * Calculates the color that a global effect (reflection or transparency) brings to a point.
     * The color seen along the ray is attenuated by {@code kx}, the coefficient of this effect only.
     * The attenuation of the previous levels is applied by the same multiplication in these levels
     * on the way back, so {@code kkx} is used only for the stopping condition of the next levels.
     *
     * @param ray the reflection or the transparency ray
     * @param level the level of the point from which the ray left
     * @param kkx the accumulated attenuation including this effect
     * @param kx the coefficient (kR or kT) of this effect
     * @return the attenuated color seen along the ray: the scaled background if the ray hits nothing
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 kkx, Double3 kx){
        Intersection hit = findClosestIntersection(ray);
        if (hit == null)
            return scene.background.scale(kx);

        preprocessIntersection(hit, ray.getDirection());
        return calcColor(hit, level - 1, kkx).scale(kx);
    }

    /*2.5*/
    /**
     * Builds the reflection ray at a preprocessed intersection: R = V - 2(N·V)N,
     * with the head shifted by the constructor of {@link Ray}.
     * If the ray is tangent to the surface (N·V = 0) the reflection direction is V itself,
     * so no zero vector is created.
     *
     * @param intersection the preprocessed intersection information
     * @return the reflection ray
     */
    private Ray constructReflectedRay(Intersection intersection){
        Vector v = intersection.directionRay;
        Vector direction = intersection.dirScaleNormal == 0 ? v
                         : v.add(intersection.normalIntersection.scale(-2 * intersection.dirScaleNormal));

        return new Ray(intersection.point, direction, intersection.normalIntersection);
    }

    /*2.6*/
    /**
     * Builds the transparency ray at a preprocessed intersection: it continues in the direction V of the
     * incoming ray (no refraction), with the head shifted by the constructor of {@link Ray}.
     *
     * @param intersection the preprocessed intersection information
     * @return the transparency ray
     */
    private Ray constructRefractedRay(Intersection intersection){
        return new Ray(intersection.point, intersection.directionRay, intersection.normalIntersection);
    }

    /*2.1*/
    /**
      * Preprocesses an intersection by setting the direction of the incoming ray (V),
      * the normalized surface normal at the point (N) and their dot product (N·V).
      * The normal is not flipped - lighting compares the sides of the light and the viewer
      * by the signs of N·L and N·V, so either normal orientation works.
      * @param intersection the {@link Intersection} object to be updated with normal and direction data
      * @param directionRay the direction vector of the incoming ray (from the camera)
      * All the data (V, N and N·V) is set whatever the returned value is, so a tangent hit is fully prepared:
      * the callers do not skip the calculation of the color when the ray is tangent, because the emission, the
      * ambient light and the global effects do not depend on the angle (the local lighting rejects the lights by itself,
      * since N·L can't have the sign of N·V = 0).
      * @return {@code true} if the intersection is valid for lighting computations;
      *         {@code false} if the ray is tangent to the surface (i.e., dot product is zero)
    */
    public boolean preprocessIntersection (Intersection intersection, Vector directionRay){
        intersection.directionRay = directionRay;
        intersection.normalIntersection = intersection.geometry.getNormal(intersection.point).normalize();
        intersection.dirScaleNormal = Util.alignZero(intersection.normalIntersection.dotProduct(intersection.directionRay));
        return !Util.isZero(intersection.dirScaleNormal);
    }

    /*3.1*/
    /**
     * Initializes lighting-related values in the given intersection:
     *     Sets the light source that affects this intersection
     *     Computes the normalized direction vector from the light to the point (L)
     *     Computes the dot product between the surface normal and the light direction (N·L)
     * The light contributes only if it is on the same side of the surface as the viewer,
     * i.e. N·L and N·V have the same sign. A light tangent to the surface (N·L = 0) does not contribute.
     * @param intersection the intersection to update with light information
     * @param lightSource the light source affecting the intersection
     * @return {@code true} if the light can illuminate the point as seen by the viewer, {@code false} otherwise
     */
    public  boolean setLightSource(Intersection intersection, LightSource lightSource){
        intersection.lightSource = lightSource;
        intersection.lightToIntersection = intersection.lightSource.getL(intersection.point);
        intersection.lightScaleNormal = Util.alignZero(intersection.normalIntersection.dotProduct(intersection.lightToIntersection));

        return Util.compareSign(intersection.lightScaleNormal, intersection.dirScaleNormal);
    }

    /*3*/
    /**
      * Calculates the local lighting effects (diffuse and specular) at the intersection point.
      * This method uses the Phong reflection model and sums the contribution of every light source
      * that is on the viewer's side of the surface. The intensity of a light is attenuated by the
      * transparency of the geometries between the point and the light (see {@link #transparency}),
      * so a partly transparent geometry casts a partial shadow.
      * A light is skipped when the accumulated attenuation of the path to the point, multiplied by that
      * transparency, is lower than {@link #MIN_CALC_COLOR_K} in all three channels.
      * The accumulated attenuation is used for this decision only: the local color is not scaled by it
      * (the recursion scales the color that returns to the previous levels).
      *
      * @param intersection the intersection data containing point, normal, material, light source, etc.
      * @param k the accumulated attenuation (the product of the kR/kT coefficients) of the path to this point
      * @return the color resulting from the local lighting effects (diffuse + specular)
    */
    private Color calcColorLocalEffects(Intersection intersection, Double3 k){
        Color resultColor = Color.BLACK;
        for(LightSource light : scene.lights){
            if(!(setLightSource(intersection, light)))
                continue;

            Double3 ktr = transparency(intersection);
            if(k.product(ktr).lowerThan(MIN_CALC_COLOR_K))
                continue;

            Color LightIntensity = intersection.lightSource.getIntensity(intersection.point).scale(ktr)
                                   .scale(calcSpecular(intersection).add(calcDiffusive(intersection)));
            resultColor = resultColor.add(LightIntensity);
        }
        return resultColor;
    }

    /*5*/
    /**
      * Calculates the specular reflection component at the intersection point
      * based on the Phong reflection model: kS * max(0, -V·R)^nShininess,
      * where R = L - 2(N·L)N is the reflection of the light direction.
      * Must be called after {@link #setLightSource} returned {@code true} (so N·L is not zero).
      *
      * @param intersection the intersection data containing vectors and material
      * @return the specular contribution as a Double3 (RGB scale), or ZERO if the reflection is away from the viewer
    */
    private Double3 calcSpecular(Intersection intersection){
        Vector R = intersection.lightToIntersection
                   .subtract(intersection.normalIntersection.scale(2 * intersection.lightScaleNormal));

        double minusVR = Util.alignZero(-intersection.directionRay.dotProduct(R));
        if (minusVR <= 0)
            return Double3.ZERO;
        return intersection.geometry.getMaterial().kS.scale(Math.pow(minusVR, intersection.geometry.getMaterial().nShininess));
    }

    /*4*/
    /**
      * Calculates the diffuse reflection component at the intersection point
      * based on the Lambertian reflection model: kD * |N·L|.
      * Must be called after {@link #setLightSource} returned {@code true}.
      *
      * @param intersection the intersection data containing vectors and material
      * @return the diffuse contribution as a Double3 (RGB scale)
    */
    private Double3 calcDiffusive(Intersection intersection){
        return intersection.geometry.getMaterial().kD.scale(Math.abs(intersection.lightScaleNormal));
    }

    /*3.1.1*/
    /**
      * Builds the shadow ray of a surface intersection toward its associated light source:
      *   The origin of the ray is shifted along the normal (by the constructor of {@link Ray}),
      *       to the light's side of the surface, to avoid self-intersections (self-shadowing).
      *   The direction is computed again from the shifted origin, so the ray aims
      *       exactly at the light source position (for point lights and spotlights).
      * @param intersection the surface-light intersection bundle containing the hit point,
      *                     surface normal, and the relevant light source
      * @return the shadow ray, from the shifted origin toward the light source
    */
    private Ray constructShadowRay(Intersection intersection) {
        //Vector from the point to the light source
        Vector pointToLight = intersection.lightToIntersection.scale(-1);

        //Moving the head to the light's side of the surface
        Point shiftedHead = new Ray(intersection.point,
                                    pointToLight,
                                    intersection.normalIntersection).getHead();

        //Building the shadow ray - aimed at the light source from the shifted head
        return new Ray(shiftedHead, intersection.lightSource.getL(shiftedHead).scale(-1));
    }

    /*3.2.1*/
    /**
      * Determines whether a given surface intersection is unshaded with respect to its associated
      * light source: {@code false} if a geometry that is [almost] completely opaque is between the point
      * and the light source, {@code true} otherwise. A geometry is [almost] completely opaque if its
      * transparency coefficient kT is lower than {@link #MIN_CALC_COLOR_K} in all three channels
      * ({@link Double3#lowerThan(double)}). Every geometry is considered by itself: the coefficients of
      * several partly transparent geometries are not multiplied (see {@link #transparency}).
      * The shadow ray is built by {@link #constructShadowRay}, and only intersections closer to its (shifted)
      * origin than the light source count (for directional lights the distance is
      * {@code Double.POSITIVE_INFINITY}).
      * <p>
      * This is the version of part 2 of stage 7. It is kept for the review of the lecturer, and is
      * <b>not used</b> by the rendering path: the local effects use {@link #transparency}, which gives partial shadows.
      * @param intersection the surface-light intersection bundle containing the hit point,
      *                     surface normal, and the relevant light source
      * @return {@code false} if an [almost] completely opaque geometry is between the point and the light source;
      *         {@code true} otherwise
    */
    @SuppressWarnings("unused")
    private boolean unshaded(Intersection intersection) {
        Ray shadowRay = constructShadowRay(intersection);

        //Distance from the shadow ray head to the light source
        double lightDistance = intersection.lightSource.getDistance(shadowRay.getHead());

        //List of all intersections of all bodies with the shadow ray (with the geometries, for their materials)
        List<Intersection> blockers = scene.geometries.calculateIntersections(shadowRay);
        if (blockers == null)
            return true;

        //Only an [almost] opaque geometry that is between the shadow ray head and the light source casts a shadow
        for (Intersection blocker : blockers)
            if (Util.alignZero(blocker.point.distance(shadowRay.getHead()) - lightDistance) < 0
                && blocker.geometry.getMaterial().kT.lowerThan(MIN_CALC_COLOR_K))
                return false;
        return true;
    }

    /*3.2*/
    /**
      * Calculates the part of the light of the associated light source that reaches a given surface
      * intersection. The method emits a shadow ray from the intersection point toward the light
      * (opposite to L) and multiplies the transparency coefficients (kT) of all the intersections
      * of the geometries that are between the point and the light source.
      * Every intersection counts: a ray that enters and leaves a transparent sphere is attenuated twice.
      * Implementation details:
      *   The shadow ray origin is shifted along the normal (by the constructor of {@link Ray}),
      *       to the light's side of the surface, to avoid self-intersections (self-shadowing).
      *   The shadow ray direction is computed from the shifted origin, so the ray aims
      *       exactly at the light source position (for point lights and spotlights).
      *   Only intersections closer to the shifted origin than the light source count
      *       (for directional lights the distance is {@code Double.POSITIVE_INFINITY}).
      *   The calculation stops with {@link Double3#ZERO} as soon as the accumulated
      *       coefficient is lower than {@link #MIN_CALC_COLOR_K} in all three channels.
      * @param intersection the surface-light intersection bundle containing the hit point,
      *                     surface normal, and the relevant light source
      * @return the part of the light that reaches the point, per color channel:
      *         {@link Double3#ONE} if nothing is in the way, and {@link Double3#ZERO} if the light is blocked
    */
    private Double3 transparency(Intersection intersection) {
        Ray shadowRay = constructShadowRay(intersection);

        //Distance from the shadow ray head to the light source
        double lightDistance = intersection.lightSource.getDistance(shadowRay.getHead());

        //List of all intersections of all bodies with the shadow ray (with the geometries, for their materials)
        List<Intersection> blockers = scene.geometries.calculateIntersections(shadowRay);
        Double3 ktr = Double3.ONE;
        if (blockers == null)
            return ktr;

        //Only an intersection between the shadow ray head and the light source attenuates the light
        for (Intersection blocker : blockers)
            if (Util.alignZero(blocker.point.distance(shadowRay.getHead()) - lightDistance) < 0) {
                ktr = ktr.product(blocker.geometry.getMaterial().kT);
                if (ktr.lowerThan(MIN_CALC_COLOR_K))
                    return Double3.ZERO;
            }
        return ktr;
    }
}
