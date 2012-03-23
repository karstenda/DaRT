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

public class SceneBoundingBoxStructure implements AccelerationStructure {

	CoreBoundingBox box; 
	
	@Override
	public void initialise(SceneObject<? extends Geometry> object) {
		
		Collection<Geometry> geometries = new ArrayList<Geometry>();
		for (Geometry geometry: object) {
			geometries.add(geometry);
		}
		
		box = new CoreBoundingBox(geometries);
		
	}

	@Override
	public List<IntersectionSolution> getIntersections(Ray ray) {
		List<IntersectionSolution> solutions =  box.getIntersectingGeometry(ray);
		if (solutions != null)
			Collections.sort(solutions, new IntersectionsDistanceComparator());
		return solutions;
	}
	

}
