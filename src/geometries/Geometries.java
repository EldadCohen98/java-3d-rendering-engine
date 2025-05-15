package geometries;

import primitives.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Geometries implements Intersectable{

    private final List<Intersectable> geo = new LinkedList<>();

    public Geometries(){}

    public Geometries(Intersectable...geometries){
        this.add(geometries);
    }

    public void add(Intersectable...geometries){
        Collections.addAll(geo, geometries);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> allIntersections = new LinkedList<>();

        for(Intersectable geometry : this.geo){
            List<Point> currentIntersections = geometry.findIntersections(ray);
            if(currentIntersections != null){
                allIntersections.addAll(currentIntersections);
            }
        }
        if(allIntersections.isEmpty()){
            return null;
        }
        return allIntersections;
    }
}
