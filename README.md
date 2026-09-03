# Java 3D Rendering Engine

A 3D rendering engine developed in Java using ray tracing, vector mathematics, lighting calculations, and object-oriented design.

## Features
  - 3D scene rendering using ray tracing
  - Geometric intersection calculations
  - Camera and ray construction
  - Multiple light sources and lighting calculations
  - Material properties and shading
  - Vector and point-based mathematical computations
  - Object-oriented software design
  - Automated testing with JUnit

## Technologies
  - Java
  - JUnit
  - Object-Oriented Programming (OOP)
  - Ray Tracing
  - Computer Graphics


## How It Works

The rendering process follows these main steps:

1. **Camera:** Generates rays through the pixels of the image.
2. **Ray Intersection:** Checks each ray for intersections with objects in the 3D scene.
3. **Closest Point:** Selects the closest intersection point.
4. **Lighting & Shading:** Applies lighting, material, and shading calculations.
5. **Pixel Rendering:** Calculates the final color and renders it to the corresponding pixel.

**Rendering Flow:** `Camera → Ray → Intersection → Lighting → Pixel`


## Example Renderings
  ### Multiple Light Sources
  ![Multiple Light Sources](images/lightThreeLightsSphere.png)
  
  ### Directional Lighting
  ![Directional Lighting](images/lightSphereDirectional.png)
  
  ### Lighting on Different Geometry
  ![Lighting on Different Geometry](images/lightThreeLightsTriangle.png)



## Testing

The project includes automated unit tests using JUnit to validate core components and mathematical calculations.

Tests cover areas such as:

- Vector and point operations
- Ray calculations
- Geometric intersections
- Camera behavior
- Rendering-related logic


## Project Structure

The project is organized into modular packages:

- `geometries` – 3D geometric shapes and intersection logic
- `lighting` – light sources and lighting-related calculations
- `primitives` – fundamental mathematical objects such as points, vectors, rays, and colors
- `renderer` – camera, ray tracing, and image rendering components
- `scene` – scene configuration and management
- `unittests` – automated tests for core functionality


## Project Context

Developed as an academic Java project at Lev Academic Center, with a focus on object-oriented programming, computer graphics, mathematical modeling, testing, and modular software design.
