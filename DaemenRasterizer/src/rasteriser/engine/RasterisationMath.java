package rasteriser.engine;

import java.util.Collection;

import math.CommonOps;
import math.Vector;
import rasteriser.data.Material;
import rasteriser.data.Mesh;
import rasteriser.data.Mesh.Triangle;
import rasteriser.engine.EngineSettings;

public abstract class RasterisationMath {

	public static double[] getPixelPosition(int index) {
		double xPos = index % EngineSettings.WIDTH_RESOLUTION;
		double yPos = EngineSettings.WIDTH_RESOLUTION - (index / EngineSettings.WIDTH_RESOLUTION)-1;
		return new double[] { xPos, yPos};
	}

	public static double interpolateDepth(int index, Triangle triangle) {

		double[] baryCentric = calculateBaryCentricCoordinates(index, triangle);
		
		if (baryCentric == null)
			return -1;

		Vector[] vectors = triangle.getVerticesAsVectors();
		double z1 = vectors[0].get(2);
		double z2 = vectors[1].get(2);
		double z3 = vectors[2].get(2);


		
		return z1 * baryCentric[0] + z2 * baryCentric[1] + z3 * baryCentric[2];
	}

	public static double[] calculateBaryCentricCoordinates(int index,Triangle triangle) {

		double[] pos = getPixelPosition(index);
		Vector[] vectors = triangle.getVerticesAsVectors();

		double x1 = vectors[0].get(0);
		double y1 = vectors[0].get(1);

		double x2 = vectors[1].get(0);
		double y2 = vectors[1].get(1);

		double x3 = vectors[2].get(0);
		double y3 = vectors[2].get(1);

		double noemer = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);
		double landa1 = ((y2 - y3) * (pos[0] - x3) + (x3 - x2) * (pos[1] - y3))
				/ noemer;

		if (landa1 < 0 || landa1 > 1)
			return null;

		double landa2 = ((y3 - y1) * (pos[0] - x3) + (x1 - x3) * (pos[1] - y3))
				/ noemer;

		if (landa2 < 0 || landa2 > 1)
			return null;

		double landa3 = 1 - landa1 - landa2;

		if (landa3 < 0 || landa3 > 1)
			return null;

		return new double[] { landa1, landa2, landa3 };
	}

	
	public static double[][] calcVerticeColors(Triangle triangle, Collection<LightSource> lightsources) {
		
		Vector[] vertices = triangle.getVerticesAsVectors();
		Vector[] normals = triangle.getNormals();
		double[][] colors = new double[3][];
		
		for (int i=0; i <3; i++) {
			colors[i] = calcRgbWithLightEffects(normals[i], vertices[i], triangle, lightsources);
		}
		
		return colors;
	}
	
	private static double[] calcRgbWithLightEffects(Vector normal, Vector position,
			Mesh.Triangle triangle, Collection<LightSource> lightsources) {

		// If no Light drops directly on the pixel, the pixel will only benefit
		// from ambidient light. If no light effects can be applied this will be
		// the final value.
		Material material = triangle.getMaterial();
		double[] matColor = material.getMaterialRgbColor(null);
		double[] ambientColor = material.getAmbientRgbColor(null);
		double[] pixelColor = new double[] { matColor[0] * ambientColor[0],
				matColor[1] * ambientColor[1], matColor[2] * ambientColor[2] };

		/**
		 * Apply the light effects for each lightSource
		 */
		for (LightSource lightSource : lightsources) {
			
			/*
			 * Calculate the direction to the light
			 */
			Vector directionToLight = new Vector(3);
			CommonOps.unCheckedSub(lightSource.getPosition(),
					position, directionToLight);
			directionToLight.normalise();


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

		}

		return pixelColor;
	}

}
