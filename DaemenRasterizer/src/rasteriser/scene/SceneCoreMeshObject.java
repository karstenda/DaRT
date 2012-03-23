package rasteriser.scene;

import java.util.ArrayList;
import java.util.Iterator;

import rasteriser.data.Material;
import rasteriser.data.Mesh;
import rasteriser.data.Mesh.Triangle;

import math.Matrix;
import math.Vector;



public class SceneCoreMeshObject extends SceneObject {
	
	private final Mesh mesh;
	private ArrayList<Matrix> T = new ArrayList<Matrix>();
	private Vector[] boundingbox;
	
	
	public SceneCoreMeshObject(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Mesh getMesh() {
		return mesh;
	}

	public void applyTransformation(Matrix T) {
		if (T.getnCols() != 4 || T.getnRows() != 4) {
			throw new IllegalArgumentException("Transformation matrix has illegal dimensions (not 3x3).");
		} else {
			this.T.add(T);
			// Reset the boundingbox
			boundingbox = null;
		}
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return doTransformations().iterator();
	}

	private Mesh doTransformations () {
		Mesh transformedMesh = this.mesh.clone();
		for (Matrix Transformation: T)
			transformedMesh.transform(Transformation);
		boundingbox =transformedMesh.getBoundingBox();
		return transformedMesh;
	}
	
	public void invertNormals() {
		mesh.invertNormals();
	}

	@Override
	public Vector[] getBoundingBox() {
		if (boundingbox == null) {
			doTransformations();
		}
		return boundingbox;	
	}
	
	
	public void setMaterial(Material material) {
		mesh.setMaterial(material);
	}
}