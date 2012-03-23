package raytracer.data.render;

import java.io.IOException;

import math.Vector;




public class Material {
	
	private double[] ambientColor; 
	private double[] diffuseColor; 
	private double[] specularColor;
	private double specularPower;
	private double transparancy;
	private Texture textureMap;
	private Texture bumpMap;
	private Texture specularMap;
	private Texture ambientMap;
	private Texture transparancyMap;
	private double lambertionCoeff;
	private double refractionIndex;
	private double reflectionRatio;
	private boolean hasBumpMap;
	private double glossyFactor;
	private double attenuationFactor;

	boolean isDielectric = false;
	boolean isMirror = false;
	
	public Material() {
		this.ambientColor = new double[] { 0.2, 0.2, 0.2 };
		this.diffuseColor = new double[] { 0.7, 0.7, 0.7 };
		this.specularColor = new double[] {1.0, 1.0, 1.0};
		this.specularPower = 25;
		this.transparancy = 0;
		this.lambertionCoeff = 0.5d;
		this.refractionIndex = 1.15d;
		this.reflectionRatio = 0.95;
		this.hasBumpMap = false;
		this.glossyFactor = 0;
		this.attenuationFactor = 0.0;
	}

	public double[] getMaterialRgbColor(Vector vector) {
		
		if (textureMap == null || vector == null) {
			return diffuseColor;
			
		}
		else {
			double x = vector.get(0);
			double y = vector.get(1);
			double[] color = textureMap.getColor(x,y);
			
			color[0] *= diffuseColor[0];
			color[1] *= diffuseColor[1];
			color[2] *= diffuseColor[2];
			
			return color;
		}
	}
	
	public Vector getBumpVector(Vector vector) {
		
		if (bumpMap == null || vector == null) {
			return new Vector(3);
			
		}
		else {
			double x = vector.get(0);
			double y = vector.get(1);
			double[] color = bumpMap.getColor(x,y);
			
			color[0] = 2*color[0]-1;
			color[1] = 2*color[1]-1;
			color[2] = 2*color[2]-1;
			
			return new Vector(color);
		}
	}

	public double[] getSpecularRgbColor(Vector vector) {
		
		if (specularMap == null || vector == null) {
			return specularColor;
			
		}
		else {
			double x = vector.get(0);
			double y = vector.get(1);
			double[] color = specularMap.getColor(x,y);
			
			color[0] *= specularColor[0];
			color[1] *= specularColor[1];
			color[2] *= specularColor[2];
			
			return color;
		}
	}

	public double[] getAmbientRgbColor(Vector vector) {
		
		if (ambientMap == null || vector == null) {
			return ambientColor;
			
		}
		else {
			double x = vector.get(0);
			double y = vector.get(1);
			double[] color = ambientMap.getColor(x,y);
			
			color[0] *= ambientColor[0];
			color[1] *= ambientColor[1];
			color[2] *= ambientColor[2];
			
			
			return color;
		}
	}
	
	public double getTransparancy(Vector vector) {
		
		if (transparancyMap == null || vector == null) {
			return transparancy;
			
		}
		else {
			double x = vector.get(0);
			double y = vector.get(1);
			double color = ambientMap.getTransparancy(x, y);
			return color*transparancy;
		}
	}
	
	public void setFileNameTextureMap(String fileNameTextureMap, double rep) throws IOException {
		this.textureMap = new Texture(fileNameTextureMap);
		this.textureMap.setRepeating(rep);
	}
	
	public void setFileNameTextureMap(String fileNameTextureMap) throws IOException {
		this.textureMap = new Texture(fileNameTextureMap);
	}
	
	public void setAmbientColor(double[] ambientColor) {
		this.ambientColor = ambientColor;
	}

	public void setDiffuseColor(double[] diffuseColor) {
		this.diffuseColor = diffuseColor; 
	}

	public void setSpecularColor(double[] specularColor) {
		this.specularColor = specularColor;
	}

	public void setSpecularPower(double specularPower) {
		this.specularPower = specularPower;
	}
	
	public double getSpecularPower() {
		return specularPower;
	}

	public double getTransparancy() {
		return transparancy;
	}

	public void setTransparancy(double transparancy) {
		this.transparancy = transparancy;
	}
	
	public double getRefractionIndex() {
		return refractionIndex;
	}
	
	public double getLambertionCoeff() {
		return lambertionCoeff;
	}
	
	
	public void setFileNameBumpMap(String fileNameBumpMap) throws IOException {
		this.bumpMap = new Texture(fileNameBumpMap);
		this.hasBumpMap = true;
	}

	public void setFileNameTransparancyMap(String string) throws IOException {
		this.transparancyMap = new Texture(string);
		
	}

	public void setFileNameSpecularMap(String string) throws IOException {
		this.specularMap = new Texture(string);
		
	}

	public void setFileNameAmbientMap(String string) throws IOException {
		this.ambientMap = new Texture(string);
	}

	public Texture getTextureMap() {
		return textureMap;
		
	}

	public Texture getBumpMap() {
		return bumpMap;
	}

	public void setBumpMap(Texture bumpMap) {
		this.bumpMap = bumpMap;
	}

	public Texture getSpecularMap() {
		return specularMap;
	}

	public void setSpecularMap(Texture specularMap) {
		this.specularMap = specularMap;
	}

	public Texture getAmbientMap() {
		return ambientMap;
	}

	public void setAmbientMap(Texture ambientMap) {
		this.ambientMap = ambientMap;
	}

	public Texture getTransparancyMap() {
		return transparancyMap;
	}

	public void setTransparancyMap(Texture transparancyMap) {
		this.transparancyMap = transparancyMap;
	}

	public boolean isDielectric() {
		return isDielectric;
	}

	public void setDielectric(boolean isDielectric) {
		this.isDielectric = isDielectric;
	}

	public boolean isMirror() {
		return isMirror;
	}

	public void setMirror(boolean isMirror) {
		this.isMirror = isMirror;
	}

	public double[] getAmbientColor() {
		return ambientColor;
	}

	public double[] getDiffuseColor() {
		return diffuseColor;
	}

	public double[] getSpecularColor() {
		return specularColor;
	}

	public void setTextureMap(Texture textureMap) {
		this.textureMap = textureMap;
	}

	public void setLambertionCoeff(double lambertionCoeff) {
		this.lambertionCoeff = lambertionCoeff;
	}

	public void setRefractionIndex(double refractionIndex) {
		this.refractionIndex = refractionIndex;
	}

	public double getReflectionRatio() {
		return reflectionRatio;
	}

	public void setReflectionRatio(double reflectionRatio) {
		this.reflectionRatio = reflectionRatio;
	}
	
	public boolean hasBumpMap() {
		return hasBumpMap;
	}
	
	public double getGlossyFactor() {
		return glossyFactor;
	}

	public void setGlossyFactor(double glossyFactor) {
		this.glossyFactor = glossyFactor;
	}
	
	public double getAttenuationFactor() {
		return attenuationFactor;
	}

	public void setAttenuationFactor(double attenuationFactor) {
		this.attenuationFactor = attenuationFactor;
	}

}
