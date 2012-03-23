package raytracer.data.geometry;

import math.Vector;
import raytracer.acceleration.hierarchicalbb.BoundingBoxDimensions;
import raytracer.data.render.Material;
import raytracer.engine.Ray;
import raytracer.engine.RayTraceMath;

public class Sphere implements Geometry {

	private Vector center;
	private double range;
	private Material material;
	
	public Sphere(Vector center, double range, Material material) {
		this.center = center;
		this.range = range;
		this.material = material;
	}
	
	@Override
	public IntersectionSolution intersects(Ray ray) {
		return RayTraceMath.intersects(ray, this);
	}
	
	@Override
	public Material getMaterial() {
		return material;
	}
	
	@Override
	public boolean isNeighbor(Geometry geometry) {
		return false;
	}
	
	public Vector getNormal(Vector vector) {
		Vector normal =  RayTraceMath.calcNormal(vector, this);
		return normal;
	}

	
	@Override
	public Vector getCenter() {
		return center;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	@Override
	public BoundingBoxDimensions getBoundingBoxDimensions() {
		double displacement = Math.sqrt(3*range*range);
		
		double[] smallPoint = new double[] {center.get(0)-displacement,center.get(1)-displacement,center.get(2)-displacement};
		double[] bigPoint = new double[] {center.get(0)+displacement,center.get(1)+displacement,center.get(2)+displacement};
		
		return new BoundingBoxDimensions(smallPoint, bigPoint);
	}

	
}
