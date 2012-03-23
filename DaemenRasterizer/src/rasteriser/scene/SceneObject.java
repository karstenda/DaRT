package rasteriser.scene;


import math.Matrix;
import math.Vector;
import rasteriser.data.Mesh;

public abstract class SceneObject implements Iterable<Mesh.Triangle> {
	
	public abstract void applyTransformation(Matrix T);
	
	public abstract Vector[] getBoundingBox();

}
