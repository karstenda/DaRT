package raytracer.data.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import math.Vector;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.data.geometry.Sphere;
import raytracer.engine.Ray;
import raytracer.engine.RayTraceMath;

public class SphericalEnvironment implements Environment {

private final Sphere sphere;
	
	public SphericalEnvironment (Vector center, double R, String map) throws IOException {
		Material material = new Material();
		material.setFileNameTextureMap(map);
		this.sphere = new Sphere(center, R, material);
	}

	@Override
	public double[] getColorForRay(Ray ray) {
		IntersectionSolution sol = RayTraceMath.intersects(ray, sphere);
		if (sol == null)
			return new double[] {0,0,0};
		Vector texCo = sol.getTextureCo();
		return sol.getGeometry().getMaterial().getMaterialRgbColor(texCo);
	}
	
	
	public List<LightSource> transFormToLights(int nLightsX, int nLightsY) {
		
		List<LightSource> lightSources = new ArrayList<LightSource>();
		
		
		return lightSources;
	}
	
}
