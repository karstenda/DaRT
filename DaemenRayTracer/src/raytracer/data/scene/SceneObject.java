package raytracer.data.scene;


import math.Matrix;
import math.Vector;
import raytracer.data.geometry.Geometry;


public abstract interface SceneObject<T extends Geometry> extends Iterable<T> {
	
	public abstract void applyTransformation(Matrix T);
	
	public abstract Vector[] getBoundingBox();

}
