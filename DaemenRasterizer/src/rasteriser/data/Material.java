package rasteriser.data;

import java.io.IOException;

import math.Vector;

public class Material {

	private double[] ambientColor;
	private double[] diffuseColor;
	private double lambertionCoeff;

	public Material() {
		this.ambientColor = new double[] { 0.2, 0.2, 0.2 };
		this.diffuseColor = new double[] { 0.7, 0.7, 0.7 };
		this.lambertionCoeff = 0.5d;
	}

	public double[] getMaterialRgbColor(Vector vector) {
		return diffuseColor;
	}

	public double[] getAmbientRgbColor(Vector vector) {
		return ambientColor;
	}

	public void setAmbientColor(double[] ambientColor) {
		this.ambientColor = ambientColor;
	}

	public void setDiffuseColor(double[] diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	public double getLambertionCoeff() {
		return lambertionCoeff;
	}

	public void setLambertionCoeff(double lambertionCoeff) {
		this.lambertionCoeff = lambertionCoeff;
	}
}
