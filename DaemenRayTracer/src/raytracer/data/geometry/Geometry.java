package raytracer.data.geometry;

import math.Vector;
import raytracer.acceleration.hierarchicalbb.BoundingBoxDimensions;
import raytracer.data.render.Material;
import raytracer.engine.Ray;

public interface Geometry {

	public abstract IntersectionSolution intersects(Ray ray);
	
	public abstract Material getMaterial();
	
	public abstract boolean isNeighbor(Geometry geometry);
	
	public abstract BoundingBoxDimensions getBoundingBoxDimensions();
		
	public abstract Vector getCenter();
}
