package scene;

import geometries.*;
import lighting.*;
import primitives.*;
import java.util.*;


/**
  * Scene class represents a 3D scene including geometries, lighting and background.
  * This is a flat data structure (PDS) with builder-style setters.
*/
public class Scene {
    /**
      * name - scene name.
      * background - background color in default is black.
      * ambientLight - ambient light of the scene in default is NONE.
      * geometries - the geometries that contained in the scene.
      * Lights - the list of light sources in the scene Initialized as an empty LinkedList.
    */
    public String name;
    public Color background = new Color(java.awt.Color.BLACK);
    public AmbientLight ambientLight = AmbientLight.NONE;
    public Geometries geometries = new Geometries();
    public List<LightSource> lights = new LinkedList<>();



    /**
     * Constructor that receives the scene name.
     * @param nameScene the name of the scene
    */
    public Scene(String nameScene) {
        this.name = nameScene;
    }

    /**
      * Sets the background color of the scene.
      * @param backgroundScene background color
      * @return this scene object (for builder style)
    */
    public Scene setBackground(Color backgroundScene){
        this.background = backgroundScene;
        return this;
    }

    /**
      * Sets the ambient light of the scene.
      * @param ambientLightScene ambient light object
      * @return this scene object (for builder style)
    */
    public Scene setAmbientLight(AmbientLight ambientLightScene){
        this.ambientLight = ambientLightScene;
        return this;
    }

    /**
      * Sets the geometries of the scene.
      * @param geometriesScene geometry collection
      * @return this scene object (for builder style)
    */
    public Scene setGeometries(Geometries geometriesScene){
        this.geometries = geometriesScene;
        return this;
    }

    /**
      * Sets the list of light sources in the scene.
      * This method replaces the existing list with a new one.
      *
      * @param lights list of light sources to add to the scene
      * @return this scene object (for builder-style chaining)
    */
    public Scene setLights(List<LightSource> lights){
        this.lights = lights;
        return this;
    }
}
