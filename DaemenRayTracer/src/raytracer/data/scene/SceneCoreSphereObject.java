package raytracer.data.scene;

import java.util.Iterator;

import math.CommonOps;
import math.Matrix;
import math.Vector;

import raytracer.data.geometry.Sphere;


public class SceneCoreSphereObject implements SceneObject<Sphere> {

	private Sphere sphere;
	
	public SceneCoreSphereObject (Sphere sphere) {
		this.sphere = sphere;
	}
	
	@Override
	public Iterator<Sphere> iterator() {
		return new Iterator<Sphere>() {

			boolean done = false;
			
			@Override
			public boolean hasNext() {
				return !done;
			}

			@Override
			public Sphere next() {
				done = true;
				return sphere;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public void applyTransformation(Matrix T) {
		// do nothing, not supported
	}

	@Override
	public Vector[] getBoundingBox() {
		Vector range = new Vector(new double[] {1,1,1});
		range.scale(sphere.getRange());
		Vector biggest= new Vector(3);
		Vector smallest= new Vector(3);
		CommonOps.unCheckedAdd(sphere.getCenter(), range, biggest);
		CommonOps.unCheckedSub(sphere.getCenter(), range, smallest);
		return new Vector[] {smallest,biggest};
	}

}
