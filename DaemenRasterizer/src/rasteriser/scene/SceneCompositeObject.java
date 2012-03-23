package rasteriser.scene;

import java.util.Iterator;

import rasteriser.data.Mesh.Triangle;

import math.Matrix;
import math.Vector;


public class SceneCompositeObject extends SceneObject {

	public final SceneObject[] objects;
	
	public SceneCompositeObject(SceneObject ... objects) {
		this.objects = objects;
	}
	
	public void applyTransformation(Matrix T) {
		for (SceneObject object: objects)
			object.applyTransformation(T);
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return new Iterator<Triangle>() {

			int i = 0;
			Iterator<Triangle> currentIterator = objects[0].iterator();
			
			@Override
			public boolean hasNext() {
				return currentIterator.hasNext() || i != objects.length-1;
			}

			@Override
			public Triangle next() {
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
		
		for (SceneObject object: objects ) {
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
