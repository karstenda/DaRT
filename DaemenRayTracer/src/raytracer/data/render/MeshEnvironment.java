package raytracer.data.render;

import math.Vector;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.data.geometry.Mesh;
import raytracer.data.geometry.Mesh.Triangle;
import raytracer.engine.Ray;

public class MeshEnvironment implements Environment {
	
	Mesh mesh;
	
	public MeshEnvironment(Mesh mesh) {
		this.mesh = mesh;
	}

	@Override
	public double[] getColorForRay(Ray ray) {
		IntersectionSolution solution;
		for (Triangle triangle: mesh) {
			solution = triangle.intersects(ray);
			if (solution != null && solution.getDistance() > 0) {
				Vector textureCo = solution.getTextureCo();
				return solution.getGeometry().getMaterial().getMaterialRgbColor(textureCo);
			}
		}
		return null;
	}

}
