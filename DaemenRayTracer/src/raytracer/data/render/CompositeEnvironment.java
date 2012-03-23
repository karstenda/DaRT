package raytracer.data.render;

import raytracer.engine.Ray;

public class CompositeEnvironment implements Environment {
	
	Environment[] environments;
	
	public CompositeEnvironment(Environment ... environments) {
		this.environments = environments;
	}

	@Override
	public double[] getColorForRay(Ray ray) {
		double[] color = null;
		for (Environment env: environments) {
			color = env.getColorForRay(ray);
			if (color != null)
				return color;
		}
		return null;
	}
	
}
