package renderer;

import primitives.*;
import scene.Scene;
import java.util.MissingResourceException;


public class Camera implements Cloneable{
    /*Camera fields*/
    private Point p0;        //Camera location
    private Vector vTo;      //Forward direction
    private Vector vUp;      //Upward direction
    private Vector vRight;   //Right direction

    /*View plane*/
    private double width;    //View plane width
    private double height;   //View plane height
    private double distance; //Distance from the camera
    private int nX=1, nY=1;      //Resolution (pixels)

    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;

    /*empty constructor*/
    public Camera() {}

                /**============== Interior Department - Builder Design Template ================**/
    public static class Builder {
        private final Camera camera = new Camera();

        /**
          * Sets the location (position) of the camera in 3D space.
          * @param for_P0 the {@link Point} representing the camera's position
          * @return the current {@link Builder} instance (for method chaining)
          * @throws IllegalArgumentException if the given point is {@code null}
        */
        public Builder setLocation(Point for_P0){
            if(for_P0 == null)
                throw new IllegalArgumentException("Location cannot be null");
            this.camera.p0 = for_P0;
            return this;
        }

        /**
          * Sets the distance between the camera and the view plane.
          * @param distance the distance value (must be positive)
          * @return the current {@link Builder} instance (for method chaining)
          * @throws IllegalArgumentException if the distance is less than or equal to zero
        */
        public Builder setVpDistance(double distance){
            if(distance <= 0)
                throw new IllegalArgumentException("Distance cannot be negative");
            this.camera.distance = distance;
            return this;
        }

        /**
          * Sets the physical size of the view plane (screen).
          * @param width  the width of the view plane (must be positive)
          * @param height the height of the view plane (must be positive)
          * @return the current {@link Builder} instance (for method chaining)
          * @throws IllegalArgumentException if either width or height is less than or equal to zero
        */
        public Builder setVpSize(double width, double height){
            if(width <= 0 || height <= 0)
                throw new IllegalArgumentException("View plane width and height must be positive");
            this.camera.width = width;
            this.camera.height = height;
            return this;
        }

        /**
          * Sets the resolution of the view plane in pixels.
          * @param nX the number of pixels horizontally (must be positive)
          * @param nY the number of pixels vertically (must be positive)
          * @return the current {@link Builder} instance (for method chaining)
          * @throws IllegalArgumentException if either resolution dimension is less than or equal to zero
        */
        public Builder setResolution(int nX, int nY){
            if(nX <= 0 || nY <= 0)
                throw new IllegalArgumentException("Resolution cannot be negative");
            this.camera.nX = nX;
            this.camera.nY = nY;
            return this;
        }

        /** Working method: Works with two vectors to and up
          * Sets the direction of the camera using forward and upward vectors.
          * The vectors must be normalized and orthogonal to each other.
          * @param to the forward direction vector (vTo)
          * @param up the upward direction vector (vUp)
          * @return the current {@link Builder} instance (for method chaining)
          * @throws IllegalArgumentException if either vector is {@code null} or not orthogonal
        */
        public Builder setDirection(Vector to, Vector up){
            if(to == null || up == null)
                throw new IllegalArgumentException("Direction cannot be null");

            Vector checkCross = to.crossProduct(up).normalize();
            if(checkCross.equals(Vector.ZERO) || to.equals(up))
                throw new IllegalArgumentException("vTo and vUp are parallel");

            this.camera.vTo = to.normalize();
            this.camera.vUp = up.normalize();
            this.camera.vRight = this.camera.vTo.normalize().crossProduct(this.camera.vUp.normalize()).normalize();
            return this;
        }

        /**
          * Sets the direction of the camera to look at a specific target point.
          * This method calculates the forward vector {@code vTo} from the camera's position ({@code p0})
          * toward the specified target point. Based on {@code vTo}, it selects a default "up" direction
          * (usually {@code Vector.AXIS_Z}, unless {@code vTo} is parallel to it, in which case {@code Vector.AXIS_Y} is used),
          * and computes the {@code vRight} and {@code vUp} vectors using cross products to ensure an orthogonal basis.
          * @param pointTarget the point in space that the camera should look at
          * @return the current {@link Builder} instance (for method chaining)
          * @throws IllegalArgumentException if the target point is {@code null} or equals the camera's location
        */
        public Builder setDirection(Point pointTarget){
            if(pointTarget == null)
                throw new IllegalArgumentException("Direction cannot be null");
            if(pointTarget.equals(this.camera.p0))
                throw new IllegalArgumentException("The direction point cannot be the camera's location point.");

            this.camera.vTo = pointTarget.subtract(this.camera.p0).normalize();
            this.camera.vRight = Vector.AXIS_Z.normalize();

            if (this.camera.vTo.crossProduct(Vector.AXIS_Y).length() == 0) {
                    throw new IllegalArgumentException("Right vector and up vector are parallel");
            }

            this.camera.vUp = this.camera.vRight.crossProduct(this.camera.vTo).normalize();
            this.camera.vRight = this.camera.vTo.crossProduct(this.camera.vUp).normalize();
            return this;
        }

        /**
          * Sets the direction of the camera by specifying a target point and an upward vector.
          * Calculates the forward vector (vTo) from the camera's position to the target point,
          * then uses the cross product with the given up vector to compute a right vector (vRight),
          * and finally derives the orthogonal up vector (vUp) from the right and forward vectors.
          * @param targetPoint the point the camera should look at
          * @param upVector    a vector indicating the general "up" direction in the scene
          * @return this Builder object (for chaining)
          * @throws IllegalArgumentException if the target point or up vector is null,
          *                                  if the target equals the camera location,
          *                                  or if the given up vector is parallel to the view direction
        */
        public Builder setDirection(Point targetPoint, Vector upVector){
            if(targetPoint == null || upVector == null)
                throw new IllegalArgumentException("The target point and vector up cannot be null");
            if(targetPoint.equals(this.camera.p0))
                throw new IllegalArgumentException("The direction point cannot be the camera's location point.");

            this.camera.vTo = targetPoint.subtract(this.camera.p0).normalize();
            this.camera.vRight = this.camera.vTo.crossProduct(upVector.normalize()).normalize();

            if(this.camera.vRight.equals(Vector.ZERO))
                throw new IllegalArgumentException("A right vector cannot be 0.");

            this.camera.vUp = this.camera.vRight.crossProduct(this.camera.vTo).normalize();
            return this;
        }

        /**
          * Sets the ray tracer for the camera according to the specified type.
          * Currently, supports only the SIMPLE type, which uses a basic ray tracing algorithm.
          * @param type  the type of ray tracer to use (e.g., SIMPLE)
          * @param scene the scene to be traced by the ray tracer
          * @return this Builder object for method chaining
        */
        public Builder setRayTracer(Scene scene,RayTracerType type){
            if(type == RayTracerType.SIMPLE )
                this.camera.rayTracer = new SimpleRayTracer(scene);
            return this;
        }

        /**
          * Sets the image writer for the camera, which handles pixel writing and image creation.
          * @param imageWriter the image writer to assign to the camera
          * @return this Builder object for method chaining
        */
        public Builder setImageWriter(ImageWriter imageWriter){
            this.camera.imageWriter = imageWriter;
            return this;
        }

        /**
          * Finalizes the building process and returns the constructed {@link Camera} object.
          * This method verifies that all required fields have been set and are valid.
          * If any mandatory field is missing or has an illegal value (such as non-positive dimensions),
          * it throws an {@link IllegalArgumentException}.
          * @return the fully constructed {@link Camera} instance
          * @throws IllegalArgumentException if any required field is missing or invalid:
          *     <li>Camera position (p0) is null</li>
          *     <li>Direction vectors (vTo, vUp, vRight) are not all set</li>
          *     <li>View plane size or distance are non-positive</li>
          *     <li>Resolution dimensions (nX, nY) are non-positive</li>
        */
        public Camera build(){
            if(camera.p0 == null)
                throw new MissingResourceException("p0 must be set",
                                                   "Camera",
                                                   "Camera position");
            if (camera.vTo == null)
                throw new MissingResourceException("vTo cannot be null",
                                                   "Camera",
                                                   "The direction vector in which to send the rays");
            if (camera.vUp == null)
                throw new MissingResourceException("vUp cannot be null",
                                                    "Camera",
                                                    "Defines what is at the top of the image. Should be perpendicular to vTo");
            if (camera.vRight == null)
                this.camera.vRight = camera.vTo.crossProduct(this.camera.vUp).normalize();

            if (camera.distance <= 0)
                throw new MissingResourceException("View plane distance must be positive",
                                                   "Camera",
                                                   "The distance from the camera (p0) to view plan – must be positive");
            if (camera.height <= 0)
                throw new MissingResourceException("View plane height cannot be negative",
                                                   "Camera",
                                                   "Screen height (View Plane) in the world – in real dimensions");
            if (camera.width <= 0)
                throw new MissingResourceException("View plane width cannot be negative",
                                                   "Camera",
                                                   "The width of the screen (View Plane) in the world – in real dimensions");
            if (camera.nX < 0 || camera.nY < 0)
                throw new MissingResourceException("Resolution (nX, nY) must be positive",
                                                   "Camera",
                                                   "How many pixels wide – screen resolution (X)\n" +
                                                   "How many pixels high – screen resolution (Y)");
            if (camera.imageWriter == null)
                camera.imageWriter = new ImageWriter(camera.nX, camera.nY);

            if (camera.rayTracer == null)
                camera.rayTracer = new SimpleRayTracer(new Scene("default"));


            return (Camera) camera.clone();
        }
    }
       /*========================================= end builder ========================================= */


    /**
      * Creates and returns a deep copy (clone) of this {@code Camera} instance.
      * The cloned {@code Camera} will contain new copies of all non-primitive fields
      * (such as {@link Point} and {@link Vector}) to ensure full independence
      * from the original object.
      * @return a new {@code Camera} object that is a deep copy of this instance
      * @throws RuntimeException if cloning fails (should never happen)
    */
    public Camera clone(){
        try {
            Camera cloneCamera = (Camera) super.clone();
            cloneCamera.p0 = this.p0.clone();
            cloneCamera.vTo = this.vTo.clone();
            cloneCamera.vUp = this.vUp.clone();
            cloneCamera.vRight = this.vRight.clone();
            return cloneCamera;
        }
        catch (CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }


    /**
      * Constructs a ray from the camera through a specific pixel on the view plane.
      * The view plane is assumed to be located at a fixed distance from the camera
      * and is divided into a grid of {@code nX} by {@code nY} pixels.
      * This method calculates the exact direction from the camera's position to the center of
      * the pixel located at column {@code j} and row {@code i}, and returns a normalized ray.
      * @param nX number of columns (width resolution) in the view plane
      * @param nY number of rows (height resolution) in the view plane
      * @param j  pixel column index (X-axis)
      * @param i  pixel row index (Y-axis)
      * @return a {@link Ray} from the camera location through the center of the pixel (i,j)
    */

    public Ray constructRay(int nX, int nY, int j, int i){
        // Step 1: Find the center of the view plane
        Point viewPlanCenter = p0.add(this.vTo.scale(this.distance));

        // Step 2: Calculate pixel size
        double pixelWidth = this.width/nX;
        double pixelHeight = this.height/nY;

        // Step 3: Calculate x and y shift from the center by formula.
        double xShift = 0, yShift = 0;
        xShift = (j - (nX - 1) / 2.0) * pixelWidth;
        yShift = (i - (nY - 1) / 2.0) * pixelHeight;

        // Step 4: Calculate center of pixel
        Point pixelCenter = viewPlanCenter;
        if (!Util.isZero(xShift))
            pixelCenter = viewPlanCenter.add(this.vRight.scale(xShift));

        if (!Util.isZero(yShift))
            pixelCenter = pixelCenter.add(this.vUp.scale(-yShift));

        // Step 5: Calculate the direction and return the ray
        Vector direction = pixelCenter.subtract(p0).normalize();

        // Step 6: returned the ray
        return new Ray(p0, direction);
    }

    /**
      * Returns a new {@link Builder} instance used to construct a {@code Camera}.
      * This static method is the entry point to the Builder design pattern,
      * allowing users to build a {@code Camera} object step by step using chained method calls.
      * @return a new {@link Builder} for creating a {@code Camera}
    */
    public static Builder getBuilder(){
        return new Builder();
    }

    /**
      * Returns the forward direction vector (vTo) of the camera.
      * This vector points in the direction the camera is facing,
      * from the camera's location toward the center of the view plane.
      * @return the forward direction vector (vTo)
    */
    public Vector getVTo(){
        return this.vTo;
    }

    /**
      * Returns the upward direction vector (vUp) of the camera.
      * This vector defines what is "up" in the image, and is orthogonal
      * to both the forward direction (vTo) and the right direction (vRight).
      * @return the upward direction vector (vUp)
    */
    public Vector getVUp(){
        return this.vUp;
    }

    /**
      * Returns the right direction vector (vRight) of the camera.
      * This vector is perpendicular to both the forward (vTo) and upward (vUp) vectors,
      * and points to the right side of the view plane relative to the camera.
      * @return the right direction vector (vRight)
    */
    public Vector getVRight(){
        return this.vRight;
    }


    /**
      * Renders the entire image by casting rays through all pixels in the view plane.
      * The method iterates over each pixel in the image grid, constructs a ray through that pixel,
      * traces the ray to determine the color, and writes the color to the image.
      * @return this {@code Camera} instance (to allow method chaining)
      * @throws UnsupportedOperationException if {@code imageWriter} or {@code rayTracer} is not initialized
    */
    public Camera renderImage(){
        if(this.imageWriter == null || this.rayTracer == null)
            throw new UnsupportedOperationException ("Not supported yet.");
        for(int i=0; i<this.nX; i++)
            for(int j=0; j<this.nY; j++)
                castRay(i,j);
        return this;
    }

    /**
      * Draws a grid over the rendered image by coloring horizontal and vertical lines
      * at regular intervals with the specified color. The grid is drawn directly on the
      * image managed by the {@link ImageWriter}, and the image is saved to a file after rendering.
      * For each pixel (x, y), if x or y is divisible by {@code interval}, the pixel
      * will be colored using the given {@code color}, forming a grid pattern.
      * @param interval the spacing (in pixels) between grid lines; must be positive
      * @param color    the {@link Color} to use for drawing the grid lines
      * @return this {@link Camera} object (to allow method chaining)
      * @throws IllegalArgumentException if {@code interval} is zero or negative
    */
    public Camera printGrid(int interval, Color color){
        if (interval <= 0)
            throw new IllegalArgumentException("Interval must be positive and greater than zero.");

        for(int x=0; x<this.imageWriter.nX(); x++)
            for (int y=0; y<this.imageWriter.nY(); y++)
                if(x%interval==0 || y%interval==0)
                    imageWriter.writePixel(x,y,color);

        imageWriter.writeToImage("des");
        return this;
    }

    /**
      * Exports the rendered image to a PNG file.
      * Delegates the writing operation to the associated {@link renderer.ImageWriter} object.
      * This method enables method chaining by returning the {@code Camera} instance itself.
      * @param nameImage the name of the image file to create (without file extension)
      * @return the current {@code Camera} object (for chaining)
    */
    public Camera writeToImage(String nameImage){
        this.imageWriter.writeToImage(nameImage);
        return this;
    }

    /**
      * Casts a single ray through the specified pixel on the view plane and determines its color.
      * This method performs the following steps:
      *     1. Constructs a ray through the center of the specified pixel using {@link #constructRay}.
      *     2. Uses the {@link renderer.RayTracerBase} to trace the ray and compute the resulting color.
      *     3. Colors the corresponding pixel using the {@link renderer.ImageWriter}.
      * @param i the row index of the pixel (Y-axis)
      * @param j the column index of the pixel (X-axis)
    */
    private void castRay(int i, int j){
        //Creating a ray through the pixel center
        Ray castRay = constructRay(this.nX, this.nY, j, i);

        //Calculating the light intensity of the ray
        Color colorRay = this.rayTracer.traceRay(castRay);

        //Pixel coloring
        this.imageWriter.writePixel(j, i, colorRay);
    }
}
