package raytracer.data.geometry;

import math.CommonOps;
import math.Vector;
import raytracer.data.geometry.Mesh.Triangle;
import raytracer.engine.Ray;
import raytracer.engine.RayTraceMath;

public class TriangleIntersectionSolution extends IntersectionSolution {

	final private Triangle triangle;
	final private Vector intersectionPoint;
	final private Vector baryCentric;
	final private Ray ray;
	
	public TriangleIntersectionSolution(Vector baryCentric, Triangle triangle, Ray ray) {
		this.triangle = triangle;
		this.intersectionPoint= ray.getDirection().clone();
		this.intersectionPoint.scale(baryCentric.get(2));
		CommonOps.add(ray.getOrigin(), intersectionPoint, intersectionPoint);
		this.baryCentric = baryCentric;
		this.ray = ray;
	}

	@Override
	public Vector getIntersectionPoint() {
		return intersectionPoint;
	}

	@Override
	public double getDistance() {
		return baryCentric.get(2);
	}
	
	@Override
	public Vector getTextureCo() {
		return RayTraceMath.interpolateTextureCo(baryCentric, triangle);
	}
	
	@Override
	public Vector getNormal() {
		
		if (triangle.getMaterial().hasBumpMap()) {
			Vector[] tangentSpace = getTangentSpace();
				Vector bump = triangle.getMaterial().getBumpVector(getTextureCo());
				tangentSpace[0].scale(bump.get(2));
				tangentSpace[2].scale(bump.get(1));
				tangentSpace[1].scale(bump.get(0));
				CommonOps.add(tangentSpace[0], tangentSpace[2], tangentSpace[0]);
				CommonOps.add(tangentSpace[0], tangentSpace[1], tangentSpace[0]);
				return tangentSpace[0];
			
		} else {
			return RayTraceMath.interpolateNormal(baryCentric, triangle);
		}
	}
	
	@Override
	public Ray getRay() {
		return ray;
	}

	@Override
	public Geometry getGeometry() {
		return triangle;
	}

	
	@Override
	public Vector[] getTangentSpace() {
		
		Vector normal = RayTraceMath.interpolateNormal(baryCentric, triangle);
		
		Vector[] vertices = triangle.getVerticesAsVectors();
		Vector[] verticesUV = triangle.getTextureCo();
		
		Vector v1 = new Vector(3);
		CommonOps.sub(vertices[1], vertices[0], v1);
		
		Vector v2 = new Vector(3);
		CommonOps.sub(vertices[2], vertices[0], v2);
		
		double uva = verticesUV[1].get(0) - verticesUV[0].get(0);
		double uvb = verticesUV[2].get(0) - verticesUV[0].get(0);
		
		double uvc = verticesUV[1].get(1) - verticesUV[0].get(1);
		double uvd = verticesUV[2].get(1) - verticesUV[0].get(1);

		double uvk = uvb*uvc - uva*uvd;

		Vector tangent = new Vector(3);
		Vector biTangent = new Vector(3);
		
		if (uvk != 0.0d) {
			
			// FNormalize((uvc*v2-uvd*v1)/uvk);
			Vector temp1 = v1.clone();
			Vector temp2 = v2.clone();	
			temp2.scale(uvc);
			temp1.scale(uvd);
			CommonOps.unCheckedSub(temp2, temp1, tangent);
			tangent.divide(uvk);

		} else {
			
			if (uva != 0.0d) {
				// FNormalize(v1/uva);
				tangent = v1.clone();
				tangent.divide(uva);
			} else if(uvb != 0.0d) {
				// FNormalize(v2/uvb);
				tangent = v2.clone();
				tangent.divide(uvb);
			} else {
				tangent = new Vector(3);
			}
		}
		
		tangent.normalise();
		CommonOps.unCheckedCrossPorduct3D(normal, tangent, biTangent);
		biTangent.normalise();
		
		return new Vector[] {normal,tangent,biTangent};
	}
	
}
