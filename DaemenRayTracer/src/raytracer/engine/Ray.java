package raytracer.engine;

import raytracer.data.render.Material;
import math.Vector;

public class Ray {

	
	private final Vector origin;
	private final Vector direction;
	private int reflectionLevel = EngineSettings.REFLECCTION_LEVEL;
	private Material containedMaterial;
	private Vector intersectionSolution;
	
	public Ray(Vector origin, Vector direction, int reflectionLevel) {
		this.origin = origin.clone();
		this.direction = direction.clone();
		this.reflectionLevel = reflectionLevel;
	}
	
	public Ray(Vector origin, Vector direction) {
		this(origin,direction, EngineSettings.REFLECCTION_LEVEL);
	}

	public Vector getOrigin() {
		return origin;
	}


	public Vector getDirection() {
		return direction;
	}


	public int getReflectionLevel() {
		return reflectionLevel;
	}
	
	public Vector getIntersectionSolution() {
		return intersectionSolution;
	}
	
	public void setIntersectionSolution(Vector intersectionSolution) {
		this.intersectionSolution = intersectionSolution;
	}
	
	public Material getContainingMaterial() {
		return containedMaterial;
	}
	
	public void setContainedMaterial(Material containedMaterial) {
		this.containedMaterial = containedMaterial;
	}
	
}
