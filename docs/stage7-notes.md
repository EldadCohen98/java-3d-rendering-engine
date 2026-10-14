# Stage 7 notes: shadows, transparency and reflection

Short notes on what changed in this stage, how it was verified, and what is still open.
They are not a claim that the project meets every submission requirement.

## What changed
- **Shadows.** Shadow rays start at a point shifted by `DELTA = 0.1` along the normal (the `Ray(Point, Vector, Vector)` constructor),
  and their direction is recomputed from the shifted head toward the light. `LightSource.getDistance` limits the blockers
  to those between the point and the light. `unshaded` is kept (it is not used in the render path).
- **Partial shadows.** `transparency(Intersection)` multiplies `kT` of every blocking intersection before the light
  (entering and leaving a sphere are two intersections) and stops below `MIN_CALC_COLOR_K`.
- **Transparency and reflection.** `Material` has `kT` and `kR`. `calcColor(Intersection, int, Double3)` is the recursive method,
  with `calcGlobalEffects` / `calcGlobalEffect`, the secondary rays, `findClosestIntersection(Ray)` and the stopping conditions
  `MAX_CALC_COLOR_LEVEL = 10`, `MIN_CALC_COLOR_K = 0.001`, `INITIAL_K`. The transparency ray continues in the same direction (no refraction).
- **Fixes found during the review of earlier stages:** ambient light with `kA`, light directions, camera direction and builder reuse,
  `Ray.findClosestPoint` on an empty list, the normal of `Tube`, an explicit error for an unsupported ray tracer type.
- **Scenes.** `ShadowTests` and `ReflectionRefractionTests` are the course scenes (the eight `???` values of `ShadowTests` were filled by us);
  the triangle lights of `LightsTests` use the course values. `FinalSceneTests` renders `images/finalScene.png`, a scene made for the project.
  The transparent sphere of the final scene has `kR = 0` (no closed object is both transparent and reflective).

## Verification
- **163 tests pass, 0 fail** (primitives, geometries, renderer, lighting).
- **Two kinds of tests.** Unit tests check results with assertions (geometries, primitives, camera, lighting, ray tracer,
  recursion, tangent hits, shadows). Rendering tests (`LightsTests`, `ShadowTests`, `ReflectionRefractionTests`, `FinalSceneTests`,
  `RenderTests`, `ImageWriterTest`) have no assertions: "passed" means only that the rendering finished without an exception,
  not that the picture is right.
- The 24 rendered images in `images/` are identical, pixel by pixel, to a fresh rendering of the current code.
  This shows consistency with the code, not correctness. There are no reference images of the course to compare to.
  The shadow images were also measured (the shadow moves with the triangle and grows when the spotlight gets closer).

## Deliberate deviations and choices
- **Tangent hits (N·V = 0).** The emission and the global effects are kept, ambient light is added only at the primary hit,
  and no `scale(0)` is applied. The course pseudo-code returns `Color.BLACK` there. This is a deliberate deviation, covered by tests.
- **Tangent test of `Ray`.** It is done on the dot product of the normalized vectors, not on `isZero(vn)`.
- **Our own choices:** the helper `constructShadowRay`, the values that fill the `???` of `ShadowTests`, the final scene, and the added tests.

## Clarification: `unshaded`
The course material is inconsistent here: the English pseudo-code of the slide says "if the geometry is [almost] completely
transparent, return false", while the Hebrew text and the instructions say that only an opaque body (`kT` below `MIN_CALC_COLOR_K`)
casts the shadow. The implementation follows the Hebrew text and the instructions. This is the implementation of the Hebrew
requirement, not a deviation from it. (The Hebrew text itself was not re-read for this note.)

## Sources of the course files
- `LightsTests` (triangle values), `ShadowTests` and `ReflectionRefractionTests` were taken from copies of the course files.
  For `ShadowTests` and `ReflectionRefractionTests` the origin on the course site was **not independently verified**;
  the copies are the ones that were given as the course files.
- The slides and the instructions were read only partly (the English code and identifiers). Several assumptions about the
  course material remain unchecked (for example, how the levels are counted and the exact form of the threshold checks).

## Known limitations (open)
- A thin black line is visible along the edge shared by two triangles in some images (probably rays that hit the edge exactly).
- `sphereSpot` in `LightsTests` has `kq = 0.00004`, the course file has `0.0001`. The other sphere tests were not compared line by line.
- `Tube` and `Cylinder` have no intersections, and `RayTracerType.GRID` is not implemented.
- The Javadoc of `preprocessIntersection` has a paragraph inside the `@param` description, and a sentence that says
  the global effects do not depend on the angle, which is not accurate (the direction of the reflection does).
- Minor: an unused import in `SimpleRayTracer`, and some missing Javadoc.
- The stage tag requirements of the course (`PR07.1`) are not addressed by this repository state.
