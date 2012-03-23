package raytracer.engine;

import java.util.List;

import math.CommonOps;
import math.Vector;
import raytracer.acceleration.AccelerationStructure;
import raytracer.data.geometry.Geometry;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.data.render.Material;
import raytracer.data.scene.Scene;

public class GraphicsPipeline {

	private final int id;

	public GraphicsPipeline(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public double[] renderRay(Scene scene, AccelerationStructure structure,
			Ray ray) {

		double[] rgbColor = new double[] { 0, 0, 0 };

		// closest intersection distance/solution
		IntersectionSolution closestSolution = null;

		/*
		 * See if Ray intersects with a triangle on the mesh of the object.
		 */
		
		
		// Get intersection data	
		List<IntersectionSolution> solution = structure.getIntersections(ray);

		if (solution != null) {
			closestSolution = solution.get(0);
		}	
		

		/*
		 * If there has not been found any solution, return the value of the
		 * environment map
		 */
		if (closestSolution == null) {
			if (scene.getEnvironment() == null)
				return new double[] { 0, 0, 0 };
			else {
				rgbColor = scene.getEnvironment().getColorForRay(ray);
				if (rgbColor == null)
					return new double[] { 0, 0, 0 };
				else
					return rgbColor;
			}
		} else /* Otherwise calculate the color */{

			/*
			 * Determine which collor effects should be applied.
			 */
			// If the angle with the normal is negative you should only add the
			// color
			// of the refracted ray because you are on the inside of the
			// material.
			if (CommonOps.dotProduct(closestSolution.getNormal(),
					ray.getDirection()) > 0) {
				
				// Only keep refracting for n-levels
				if (ray.getReflectionLevel() > 0) {
					rgbColor = addRefractionAndReflectionColor(rgbColor,
							closestSolution, scene, structure, true);
				} else {
					
					ray = new Ray(closestSolution.getIntersectionPoint(),ray.getDirection(),1);
					return renderRay(scene, structure, ray);
				}
			}
			// Otherwise you may add all the light effects.
			else {

				// Apply the standard light effects
				rgbColor = addNormalColor(rgbColor, closestSolution, scene, structure);

				// If the max number of reflections/refractions is not yet
				// reached ...

				if (ray.getReflectionLevel() > 0) {
					// If the material is dielectric, refract en reflect.
					Material material = closestSolution.getGeometry()
							.getMaterial();
					if (material.isDielectric()) {
						rgbColor = addRefractionAndReflectionColor(rgbColor,
								closestSolution, scene, structure, false);
					} else
					// If the material is a mirror, reflect.
					if (material.isMirror()) {
						rgbColor = addMirrorColor(rgbColor, closestSolution,
								scene, structure);
					}
				}

			}

		}
		return rgbColor;
	}

	private double[] addMirrorColor(double[] color,
			IntersectionSolution solution, Scene scene, AccelerationStructure structure) {

		Material material = solution.getGeometry().getMaterial();
		double reflectivity = material.getReflectionRatio();
		double[] reflColor = getReflectedRayColor(solution, scene, structure,
				material.getReflectionRatio(), false);

		color[0] = reflColor[0] * reflectivity + color[0]*(1-reflectivity);
		color[1] = reflColor[1] * reflectivity + color[1]*(1-reflectivity);
		color[2] = reflColor[2] * reflectivity + color[2]*(1-reflectivity);

		return color;
	}

	private double[] addNormalColor(double[] color,
			IntersectionSolution solution, Scene scene, AccelerationStructure structure) {

		// Calc the light effects
		double[] lightColors = RayTraceMath.calcRgbWithLightEffects(solution,
				scene, structure);

		// Add the colors
		Material material = solution.getGeometry().getMaterial();
		double transparancy = material.getTransparancy();
		color[0] += lightColors[0] * (1-transparancy);
		color[1] += lightColors[1] * (1-transparancy);
		color[2] += lightColors[2] * (1-transparancy);

		return color;
	}

	private double[] addRefractionAndReflectionColor(double[] rgbColor,
			IntersectionSolution solution, Scene scene,
			AccelerationStructure structure, boolean innerReflection) {

		double reflectivity = RayTraceMath.calcSchlikReflectivity(solution,
				innerReflection);
		
		double color[] = new double[3];
		// Apply reflection
		double[] reflColor = getReflectedRayColor(solution, scene, structure,
				reflectivity, innerReflection);

		color[0] = reflColor[0] * reflectivity;
		color[1] = reflColor[1] * reflectivity;
		color[2] = reflColor[2] * reflectivity;

		// If there is no critical reflection, apply refraction.
		if (reflectivity < 1) {
			double[] refrColor = getRefractedRayColor(solution, scene,
					structure, reflectivity, innerReflection);
			color[0] += refrColor[0] * (1 - reflectivity);
			color[1] += refrColor[1] * (1 - reflectivity);
			color[2] += refrColor[2] * (1 - reflectivity);
		}

		
		// Beers law
		Material material = solution.getGeometry().getMaterial();
//		if (innerReflection) {
//					double distance = solution.getDistance();
//					double exp = Math.log(material.getAttenuationFactor())*distance;
//					double beer_factor = Math.exp(exp);
//					beer_factor = Math.min(beer_factor, 1);
//					
//					color[0] = color[0]*beer_factor + (1-beer_factor)*material.getDiffuseColor()[0];
//					color[1] = color[1]*beer_factor + (1-beer_factor)*material.getDiffuseColor()[1];
//					color[2] = color[2]*beer_factor + (1-beer_factor)*material.getDiffuseColor()[2];
//		}
//		
		
		
		double transparancy = material.getTransparancy();
		rgbColor[0] += color[0] * (transparancy);
		rgbColor[1] += color[1] * (transparancy);
		rgbColor[2] += color[2] * (transparancy);

		return rgbColor;
	}

	private double[] getReflectedRayColor(IntersectionSolution solution,
			Scene scene, AccelerationStructure structure, double reflectivity,
			boolean innerReflection) {

		// Calculate the new direction
		Vector newDirection = RayTraceMath.reflect(solution, innerReflection);
		
		// Make the new Ray
		Ray ray = solution.getRay();
		int reflectionLevel = ray.getReflectionLevel();
		reflectionLevel--;
		Ray reflectedRay = new Ray(solution.getIntersectionPoint(),
				newDirection, reflectionLevel);

		// Recursive trace the color of the new Ray
		return renderRay(scene, structure, reflectedRay);
	}

	private double[] getRefractedRayColor(IntersectionSolution solution,
			Scene scene, AccelerationStructure structure, double reflectivity,
			boolean innerRefraction) {

		Ray ray = solution.getRay();
		Material material = solution.getGeometry().getMaterial();

		// Calculate the refracted Direction
		Vector refractedDirection = RayTraceMath.refract(solution,
				innerRefraction);

		// If the incoming ray is too low for refraction, return null, no
		// refraction
		if (refractedDirection == null) {
			return new double[] { 0, 0, 0 };
		}

		// Calculate the refracted intersectionpoint, think of theta.
		Vector epsilon = refractedDirection.clone();
		epsilon.scale(EngineSettings.DELTA_MACHINE);
		Vector start = new Vector(3);
		CommonOps.unCheckedAdd(solution.getIntersectionPoint(), epsilon, start);

		// Calculate the refracted ray
		int reflectionLevel = ray.getReflectionLevel();
		reflectionLevel--;
		Ray refractedRay = new Ray(start, refractedDirection, reflectionLevel);
		refractedRay.setContainedMaterial(material);

		// Recursive trace the color of the new Ray
		return renderRay(scene, structure, refractedRay);
	}

}
