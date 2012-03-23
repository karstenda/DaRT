package raytracer.data.scene;

import java.util.Iterator;

import math.Matrix;
import math.Vector;

import raytracer.data.geometry.Geometry;


public class SceneCompositeObject implements SceneObject<Geometry> {

	public final SceneObject<? extends Geometry>[] objects;
	
	public SceneCompositeObject(SceneObject<? extends Geometry> ... objects) {
		this.objects = objects;
	}
	
	@Override
	public void applyTransformation(Matrix T) {
		for (SceneObject<? extends Geometry> object: objects)
			object.applyTransformation(T);
	}
	
	@Override
	public Iterator<Geometry> iterator() {
		return new Iterator<Geometry>() {

			int i = 0;
			Iterator<? extends Geometry> currentIterator = objects[0].iterator();
			
			@Override
			public boolean hasNext() {
				return currentIterator.hasNext() || i != objects.length-1;
			}

			@Override
			public Geometry next() {
				if (!currentIterator.hasNext()) {
					i++;
					currentIterator = objects[i].iterator();
				}
				return currentIterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public Vector[] getBoundingBox() {
		Vector[] boundingBox = new Vector[] {
				new Vector(new double[] {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE}),
				new Vector(new double[] {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE})};
		
		for (SceneObject<? extends Geometry> object: objects ) {
			Vector[] otherBoundingBox = object.getBoundingBox();
			
			if (otherBoundingBox[0].get(0) < boundingBox[0].get(0))
				boundingBox[0].set(0, otherBoundingBox[0].get(0));
			if (otherBoundingBox[0].get(1) < boundingBox[0].get(1))
				boundingBox[0].set(1, otherBoundingBox[0].get(1));
			if (otherBoundingBox[0].get(2) < boundingBox[0].get(2))
				boundingBox[0].set(2, otherBoundingBox[0].get(2));
			
			if (otherBoundingBox[1].get(0) > boundingBox[1].get(0))
				boundingBox[1].set(0, otherBoundingBox[1].get(0));
			if (otherBoundingBox[1].get(1) > boundingBox[1].get(1))
				boundingBox[1].set(1, otherBoundingBox[1].get(1));
			if (otherBoundingBox[1].get(2) > boundingBox[1].get(2))
				boundingBox[1].set(2, otherBoundingBox[1].get(2));
			
		}
		
		return boundingBox;
	}
}
