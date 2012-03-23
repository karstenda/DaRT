package raytracer.data.geometry;

import math.Vector;
import raytracer.acceleration.hierarchicalbb.BoundingBox;
import raytracer.engine.Ray;

public abstract class IntersectionSolution {

	BoundingBox box;
	
	public abstract Geometry getGeometry();
	
	public abstract Ray getRay();
	
	public abstract Vector getNormal();
	
	public abstract Vector getIntersectionPoint();

	public abstract double getDistance();

	public abstract Vector getTextureCo();
	
	public abstract Vector[] getTangentSpace();
	
	public void setBoundingBox(BoundingBox box) {
		this.box = box;
	}
	
	public BoundingBox getBoundingBox() {
		return box;
	}

}
