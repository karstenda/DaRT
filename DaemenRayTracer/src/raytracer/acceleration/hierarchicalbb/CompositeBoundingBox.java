package raytracer.acceleration.hierarchicalbb;

import java.util.List;

import raytracer.data.geometry.IntersectionSolution;
import raytracer.engine.Ray;

public class CompositeBoundingBox extends BoundingBox {

	private final BoundingBox box1;
	private final BoundingBox box2;
	private final BoundingBoxDimensions dim ;
	
	public CompositeBoundingBox(BoundingBox box1, BoundingBox box2) {
		dim = new BoundingBoxDimensions(box1, box2);
		this.box1 = box1;
		this.box2 = box2;
	}
	
	@Override
	public BoundingBoxDimensions getDimensions() {
		return dim;
	}

	@Override
	public int size() {
		return box1.size()+box2.size();
	}
	
	public List<IntersectionSolution> getIntersectingGeometry(Ray ray) {
		List<IntersectionSolution> intersections = null;
		
		// Intersecting first box
		if (box1.intersects(ray)) {
			intersections = box1.getIntersectingGeometry(ray);
		}
		
		// Intersecting second box only
		if (box2.intersects(ray)) {
			List<IntersectionSolution> result = box2.getIntersectingGeometry(ray);
			
			if (result != null) {
				if (intersections == null) {
					intersections = result;
				}
				intersections.addAll(result);
			}
			
		}
		
		return intersections;
	}

	@Override
	public int getNumBoxes() {
		return box1.getNumBoxes()+box2.getNumBoxes()+1;
	}

	@Override
	public int getMaxDepth() {
		return Math.max(box1.getMaxDepth(), box2.getMaxDepth())+1;
	}

}
