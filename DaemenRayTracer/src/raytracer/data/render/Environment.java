package raytracer.data.render;

import raytracer.engine.Ray;


public interface Environment {

	public double[] getColorForRay(Ray ray);
	
}
