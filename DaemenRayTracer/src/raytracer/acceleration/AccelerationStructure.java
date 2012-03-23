package raytracer.acceleration;

import java.util.List;

import raytracer.data.scene.SceneObject;
import raytracer.data.geometry.Geometry;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.engine.Ray;

public interface AccelerationStructure {

	public abstract void initialise(SceneObject<? extends Geometry> object);
	
	public abstract List<IntersectionSolution> getIntersections(Ray ray);
	
}
