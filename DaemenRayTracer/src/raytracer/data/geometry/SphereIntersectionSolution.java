package raytracer.data.geometry;

import math.CommonOps;
import math.Vector;
import raytracer.engine.Ray;
import raytracer.engine.RayTraceMath;

public class SphereIntersectionSolution extends IntersectionSolution {
	
	private final Vector intersectionPoint;
	private final double distance;
	private final Sphere sphere;
	private final Ray ray;
	
	public SphereIntersectionSolution(Vector intersectionPoint,double distance, Sphere sphere, Ray ray) {
		this.sphere = sphere;
		this.intersectionPoint = intersectionPoint;
		this.distance = distance;
		this.ray = ray;
	}
	
	@Override
	public Vector getIntersectionPoint() {
		return intersectionPoint;
	}

	@Override
	public double getDistance() {
		return distance;
	}

	@Override
	public Vector getTextureCo() {
		return RayTraceMath.getSpehericalTextureCo(intersectionPoint, sphere);
	}
	
	@Override 
	public Vector getNormal() {
		if (sphere.getMaterial().hasBumpMap()) {
			Vector[] tangentSpace = getTangentSpace();
				Vector bump = sphere.getMaterial().getBumpVector(getTextureCo());
				tangentSpace[0].scale(bump.get(2));
				tangentSpace[2].scale(bump.get(1));
				tangentSpace[1].scale(bump.get(0));
				CommonOps.add(tangentSpace[0], tangentSpace[2], tangentSpace[0]);
				CommonOps.add(tangentSpace[0], tangentSpace[1], tangentSpace[0]);
				return tangentSpace[0];
			
		} else {
			return RayTraceMath.calcNormal(intersectionPoint, sphere);
		}
	}

	@Override
	public Geometry getGeometry() {
		return sphere;
	}

	@Override
	public Ray getRay() {
		return ray;
	}

	@Override
	public Vector[] getTangentSpace() {
		
		Vector normal = RayTraceMath.calcNormal(intersectionPoint, sphere);
		
		Vector toNorth = new Vector(3);
		Vector north = new Vector(new double[] {0,sphere.getRange(),0});
		CommonOps.unCheckedSub(intersectionPoint, sphere.getCenter(), toNorth);
		CommonOps.unCheckedAdd(toNorth, north, toNorth);
		
		Vector biTangent = new Vector(3);
		CommonOps.unCheckedCrossPorduct3D(normal, north, biTangent);
		Vector tangent = new Vector(3);
		CommonOps.unCheckedCrossPorduct3D(normal, biTangent, tangent);		
		
		return new Vector[] {normal,tangent, biTangent};
	}
	
	
}
