package raytracer.acceleration.hierarchicalbb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import raytracer.acceleration.AccelerationStructure;
import raytracer.data.geometry.Geometry;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.data.geometry.IntersectionsDistanceComparator;
import raytracer.data.scene.SceneObject;
import raytracer.engine.Ray;

public class HierarchicalBoundingBoxStructure implements AccelerationStructure {

	private BoundingBox hierarchy;
	
	@Override
	public void initialise(SceneObject<? extends Geometry> object) {
		
		Collection<Geometry> geometries = new ArrayList<Geometry>();
		for (Geometry geometry: object) {
			geometries.add(geometry);
		}
		
		CoreBoundingBox box = new CoreBoundingBox(geometries);
		hierarchy = divideUntil(10, 0, box);
	}
	
	
	private BoundingBox divideUntil(int nGeometries, int nAxis, CoreBoundingBox box) {
		
		
		if (box.size() <=  nGeometries)
			return box;
		
		nAxis = (nAxis+1) % 3;
		CoreBoundingBox[] boxes = box.split(nAxis);
		
		BoundingBox box1;
		BoundingBox box2;
		
		if (boxes[0].size() > nGeometries) {
			box1 = divideUntil(nGeometries, nAxis, boxes[0]);
		} else {
			box1 = boxes[0];
		}
		
		
		if (boxes[1].size() > nGeometries) {
			box2 = divideUntil(nGeometries, nAxis, boxes[1]);
		} else {
			box2 = boxes[1];
		}
		
		return new CompositeBoundingBox(box1, box2);
	}

	@Override
	public List<IntersectionSolution> getIntersections(Ray ray) {
		List<IntersectionSolution> solutions =  hierarchy.getIntersectingGeometry(ray);
		if (solutions != null) {
			Collections.sort(solutions, new IntersectionsDistanceComparator());
		}
		return solutions;
	}

	
	public int getNumBoxes() {
		return hierarchy.getNumBoxes();
	}
	
	public int getMaxDepth() {
		return hierarchy.getMaxDepth();
	}
}
