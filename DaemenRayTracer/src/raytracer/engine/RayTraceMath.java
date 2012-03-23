package raytracer.engine;

import java.util.List;

import math.CommonOps;
import math.Matrix;
import math.Vector;
import raytracer.acceleration.AccelerationStructure;
import raytracer.acceleration.hierarchicalbb.BoundingBox;
import raytracer.data.geometry.Geometry;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.data.geometry.Mesh;
import raytracer.data.geometry.Sphere;
import raytracer.data.geometry.SphereIntersectionSolution;
import raytracer.data.geometry.TriangleIntersectionSolution;
import raytracer.data.render.LightSource;
import raytracer.data.render.Material;
import raytracer.data.scene.Scene;

public abstract class RayTraceMath {

	/**
	 * Optimized method for computing the intersection of Triangle 'triangle'
	 * with a ray coming from vector 'from' with direction vector 'direction'.
	 */
	public static IntersectionSolution intersects(Ray ray,
			final Mesh.Triangle triangle) {

		Vector[] vertices = triangle.getVerticesAsVectors();
		Vector baryCentric = new Vector(3);

		double a = vertices[0].get(0) - vertices[1].get(0);
		double b = vertices[0].get(1) - vertices[1].get(1);
		double c = vertices[0].get(2) - vertices[1].get(2);

		double d = vertices[0].get(0) - vertices[2].get(0);
		double e = vertices[0].get(1) - vertices[2].get(1);
		double f = vertices[0].get(2) - vertices[2].get(2);

		double g = ray.getDirection().get(0);
		double h = ray.getDirection().get(1);
		double i = ray.getDirection().get(2);

		double j = vertices[0].get(0) - ray.getOrigin().get(0);
		double k = vertices[0].get(1) - ray.getOrigin().get(1);
		double l = vertices[0].get(2) - ray.getOrigin().get(2);

		double M = a * (e * i - h * f) + b * (g * f - d * i) + c
				* (d * h - e * g);

		double gamma = (i * (a * k - j * b) + h * (j * c - a * l) + g
				* (b * l - k * c))
				/ M;
		if (gamma < 0 || gamma > 1)
			return null;

		double beta = (j * (e * i - h * f) + k * (g * f - d * i) + l
				* (d * h - e * g))
				/ M;

		if (beta < 0 || beta > (1 - gamma))
			return null;

		double t = -(f * (a * k - j * b) + e * (j * c - a * l) + d
				* (b * l - k * c))
				/ M;

		baryCentric.set(0, beta);
		baryCentric.set(1, gamma);
		baryCentric.set(2, t);

		return new TriangleIntersectionSolution(baryCentric, triangle, ray);
	}

	public static IntersectionSolution intersects(Ray ray, final Sphere sphere) {
		
		
		Vector c = sphere.getCenter();
		Vector d = ray.getDirection();
		Vector e = ray.getOrigin();
		double R = sphere.getRange();

		// Calculate the root term
		double dd = CommonOps.unCheckedDotProduct(d, d);
		Vector e_c = new Vector(3);
		CommonOps.unCheckedSub(e, c, e_c);
		double rTermRoot = dd
				* (CommonOps.unCheckedDotProduct(e_c, e_c) - R * R);
		double lTermRoot = CommonOps.unCheckedDotProduct(d, e_c);
		lTermRoot *= lTermRoot;
		double discriminant = lTermRoot - rTermRoot;

		// negative discriminant, no intersection, returning null
		if (discriminant < 0)
			return null;

		// calculate the numerator
		discriminant = Math.sqrt(discriminant);
		Vector _d = d.clone();
		_d.scale(-1);
		double lTerm = CommonOps.unCheckedDotProduct(_d, e_c);

		// calculate t
		double t1 = (lTerm + discriminant) / dd;
		double t2 = (lTerm - discriminant) / dd;

		// Calculate the intersectionpoint
		Vector solution = d.clone();
		double t;
		
		
		if (t1 < 0 && t2 < 0) {
			return null;
		} else if (t1 < 0 && t2 > 0) {
			solution.scale(t2);
			t = t2;
		} else if (t1 > 0 && t2 < 0) {
			solution.scale(t1);
			t = t1;
		} else if (t1 > t2) {
			solution.scale(t2);
			t = t2;
		} else {
			solution.scale(t1);
			t = t1;
		}
		
		CommonOps.add(solution, e, solution);
		return new SphereIntersectionSolution(solution, t, sphere, ray);

	}

	public static Vector interpolateNormal(Vector intersectionSolution,
			Mesh.Triangle triangle) {

		// Calculate the barrycentric cordinates
		double[] barryCentric = new double[3];
		barryCentric[0] = 1 - intersectionSolution.get(0)
				- intersectionSolution.get(1);
		barryCentric[1] = intersectionSolution.get(0);
		barryCentric[2] = intersectionSolution.get(1);

		// Get the weighted normals
		Vector[] normals = triangle.getNormals();

		normals[0].scale(barryCentric[0]);
		normals[1].scale(barryCentric[1]);
		normals[2].scale(barryCentric[2]);
		// Calculate the central normal
		CommonOps.unCheckedAdd(normals[0], normals[1], normals[0]);
		CommonOps.unCheckedAdd(normals[0], normals[2], normals[0]);

		// Normalise
		normals[0].normalise();

		return normals[0];
	}

	public static Vector calcNormal(Vector intersectionSolution, Sphere sphere) {
		Vector normal = new Vector(3);
		CommonOps.sub(intersectionSolution, sphere.getCenter(), normal);
		normal.divide(sphere.getRange());
		return normal;
	}

	public static Vector interpolateTextureCo(Vector intersectionSolution,
			Mesh.Triangle triangle) {

		// Calculate the barrycentric cordinates
		double[] barryCentric = new double[3];
		barryCentric[0] = 1 - intersectionSolution.get(0)
				- intersectionSolution.get(1);
		barryCentric[1] = intersectionSolution.get(0);
		barryCentric[2] = intersectionSolution.get(1);

		// Get the weighted normals
		Vector[] texturesCo = triangle.getTextureCo();
		if (texturesCo == null) {
			return null;
		}

		texturesCo[0].scale(barryCentric[0]);
		texturesCo[1].scale(barryCentric[1]);
		texturesCo[2].scale(barryCentric[2]);

		// Calculate the central normal
		CommonOps.unCheckedAdd(texturesCo[0], texturesCo[1], texturesCo[0]);
		CommonOps.unCheckedAdd(texturesCo[0], texturesCo[2], texturesCo[0]);

		return texturesCo[0];
	}
	
	
	public static double[] calcRgbWithLightEffects(
			IntersectionSolution solution, Scene scene, AccelerationStructure structure) {

		// If no Light drops directly on the pixel, the pixel will only benefit
		// from ambidient light. If no light effects can be applied this will be
		// the final value.
		Ray ray = solution.getRay();
		Vector normal = solution.getNormal();
		Material material = solution.getGeometry().getMaterial();
		Vector interSectionPoint = solution.getIntersectionPoint();
		Vector textureCo = solution.getTextureCo();

		// Calc the ambient color
		double[] matColor = material.getMaterialRgbColor(textureCo);
		double[] ambientColor = material.getAmbientRgbColor(textureCo);
		double[] pixelColor = new double[] { matColor[0] * ambientColor[0],
				matColor[1] * ambientColor[1], matColor[2] * ambientColor[2] };

		/**
		 * Apply the light effects for each lightSource
		 */
		newLight: for (LightSource lightSource : scene.getLightSources()) {
			
			/*
			 * Calculate the direction to the light
			 */
			Vector directionToLight = new Vector(3);
			CommonOps.unCheckedSub(lightSource.getPosition(),
					interSectionPoint, directionToLight);
			double distanceToLight = directionToLight.getNorm();
			directionToLight.normalise();
			Ray toLight = new Ray(interSectionPoint,directionToLight);

			/*
			 * See if another doesn't drop a shadow on this point
			 */
			
			
//			// Try cache first
//			BoundingBox box = lightSource.getCachedBoundingBox();
			List<IntersectionSolution> solutions;
//			if (box != null && box.intersects(ray)) {
//				solutions = box.getIntersectingGeometry(ray);
//				if (solutions == null) {
//					// No geometry hit, try all
//					solutions = structure.getIntersections(toLight);
//				}
//			} 
//			// No cache or no cache hit
//			else {
//				solutions = structure.getIntersections(toLight);	
//			}
			
			solutions = structure.getIntersections(toLight);
			

			if (solutions != null 
					&& solutions.get(0).getDistance() > EngineSettings.DELTA_MACHINE
					&& solutions.get(0).getDistance() < distanceToLight
					&& !solution.getGeometry().isNeighbor(solutions.get(0).getGeometry())
//					&& solutions.get(0).getGeometry().getMaterial().getTransparancy() > 0.5
					) {
				lightSource.setCachedBoundingBox(solutions.get(0).getBoundingBox());
				continue newLight;
			}

			/*
			 * Calculate diffuse shading color
			 */
			double diffuseShadingFactor = CommonOps.dotProduct(normal,
					directionToLight);

			diffuseShadingFactor = Math.max(0, diffuseShadingFactor);
			diffuseShadingFactor = diffuseShadingFactor
					* material.getLambertionCoeff();

			pixelColor[0] += matColor[0] * lightSource.getColor()[0]
					* diffuseShadingFactor;
			pixelColor[1] += matColor[1] * lightSource.getColor()[1]
					* diffuseShadingFactor;
			pixelColor[2] += matColor[2] * lightSource.getColor()[2]
					* diffuseShadingFactor;

			/*
			 * Calculate blinn-phong factor
			 */
			Vector bissectrice = new Vector(3);
			Vector negativeDirection = ray.getDirection().clone();
			negativeDirection.scale(-1);
			CommonOps.unCheckedAdd(directionToLight, negativeDirection,
					bissectrice);
			bissectrice.normalise();

			double blinnPhongFactor = CommonOps.dotProduct(bissectrice, normal);

			
			if (material.getSpecularPower() < 1.1) {
				return pixelColor;
			}
			
			blinnPhongFactor = Math.pow(blinnPhongFactor,
					material.getSpecularPower());
			blinnPhongFactor = Math.max(0, blinnPhongFactor);
			
			
			double[] specularColor = material.getSpecularRgbColor(textureCo);
			
			pixelColor[0] += specularColor[0] * lightSource.getColor()[0]
					* blinnPhongFactor;
			pixelColor[1] += specularColor[1] * lightSource.getColor()[1]
					* blinnPhongFactor;
			pixelColor[2] += specularColor[2] * lightSource.getColor()[2]
					* blinnPhongFactor;
		}

		return pixelColor;

	}

	public static Vector reflect(IntersectionSolution solution,boolean innerRefraction) {

		Vector normal = solution.getNormal();
		Vector incoming = solution.getRay().getDirection();
		
		
		// Maak de reflectie glossy
		Material material = solution.getGeometry().getMaterial();
		double glosFactor = material.getGlossyFactor();
		if (glosFactor > 0) {
			Vector[] tangentSpace = solution.getTangentSpace();
			
			tangentSpace[1].scale(Math.random()*glosFactor);
			tangentSpace[2].scale(Math.random()*glosFactor);
			CommonOps.add(tangentSpace[0], tangentSpace[1], tangentSpace[0]);
			CommonOps.add(tangentSpace[0], tangentSpace[2], tangentSpace[0]);
			normal = tangentSpace[0];
		}
		
		
		if (innerRefraction) {
			normal.scale(-1);
		}
		
		Vector outcoming = new Vector(3);
		Matrix P = new Matrix(3, 3);
		Matrix transposeNormalMatrix = new Matrix(normal.clone(), false);
		Matrix normalMatrix = new Matrix(normal, true);
		CommonOps.unCheckedMult(normalMatrix, transposeNormalMatrix, P);
		P.scale(2);
		CommonOps.unCheckedMult(P, incoming, outcoming);
		CommonOps.unCheckedSub(incoming, outcoming, outcoming);
		return outcoming;
	}

	public static Vector refract(IntersectionSolution solution,boolean innerRefraction) {

		// incoming
		double fraction = 1/solution.getGeometry().getMaterial().getRefractionIndex();
		Vector direction = solution.getRay().getDirection().clone();
		Vector normal = solution.getNormal().clone();
		
		// in case of innerRefraction
		if (innerRefraction) {
			fraction = 1/fraction;
			normal.scale(-1);
		}
		
		// Help vars
		double dn = CommonOps.unCheckedDotProduct(direction, normal);
		double discrimant = 1-(fraction*fraction*(1-dn*dn));
		
		// If there is no refraction ...
		if (discrimant < 0)
			return null;
		
		// Calc the righthand
		Vector rightHand = normal.clone();
		rightHand.scale(Math.sqrt(discrimant));
		
		// Calc the LeftHand
		Vector leftHand = normal.clone();
		leftHand.scale(dn);
		CommonOps.unCheckedSub(direction, leftHand, leftHand);
		leftHand.scale(fraction);
		
		// Result
		Vector refracDir = new Vector(3);
		CommonOps.unCheckedSub(leftHand, rightHand, refracDir);
		refracDir.normalise();
		
		return refracDir;

	}
	
	
	public static double calcSchlikReflectivity(IntersectionSolution solution, boolean innerRefraction) {
		
		// incoming
		Material material = solution.getGeometry().getMaterial();
		Vector direction = solution.getRay().getDirection().clone();
		direction.scale(-1);
		Vector normal = solution.getNormal().clone();
		double n = 1;
		double nt = material.getRefractionIndex();;
		
		// in case of innerRefraction
		if (innerRefraction) {
			direction = refract(solution, innerRefraction);
			if (direction == null)
				return 1;
			normal.scale(-1);
			nt = 1;
			n = material.getRefractionIndex();
		}
		
		// calc normal reflectivity
		double R0 = (nt-n)/(nt+n);
		R0 *= R0;
		
		// calc reflectivity
		double dn = Math.abs(CommonOps.unCheckedDotProduct(direction, normal));
		double R = R0 + (1-R0)*(1-dn)*(1-dn)*(1-dn)*(1-dn)*(1-dn);
		
		// TODO
		R = R+0.18;
		
		
		
		return R;
	}
	
	
	
	public static Vector getSpehericalTextureCo(Vector vector, Sphere sphere) {
		
		double theta = Math.acos((vector.get(1)-sphere.getCenter().get(1))/sphere.getRange());
		double sigma = Math.atan2(vector.get(2)-sphere.getCenter().get(2), vector.get(0)-sphere.getCenter().get(0));
		
		if (sigma < 0)
			sigma += 2*Math.PI;
		
		double u = sigma/(2*Math.PI);
		double v = (Math.PI-theta)/(Math.PI);
		
		return new Vector(new double[] {u,v});
	}
}
