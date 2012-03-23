package raytracer.acceleration.hierarchicalbb;

import java.util.List;

import math.Vector;

import raytracer.data.geometry.IntersectionSolution;
import raytracer.engine.EngineSettings;
import raytracer.engine.Ray;

public abstract class BoundingBox {

	public abstract BoundingBoxDimensions getDimensions();

	public abstract int size();

	public abstract List<IntersectionSolution> getIntersectingGeometry(Ray ray);
	
	public abstract int getNumBoxes();
	
	public abstract int getMaxDepth();

	public boolean intersects(Ray ray) {

		Vector d = ray.getDirection();
		Vector e = ray.getOrigin();
		
		BoundingBoxDimensions dim = getDimensions();
		double[] dimMin = dim.getSmallPoint();
		double[] dimMax = dim.getBigPoint();
		
		double t0 = EngineSettings.DELTA_MACHINE;
		double t1 = Double.MAX_VALUE;
		
		double tMin; double tMax; double tYMax; double tYMin; double tZMax; double tZMin;
		
		if (d.get(0) >= 0) {
			tMin = (dimMin[0]-e.get(0))/d.get(0);
			tMax = (dimMax[0]-e.get(0))/d.get(0);
		} else {
			tMin = (dimMax[0]-e.get(0))/d.get(0);
			tMax = (dimMin[0]-e.get(0))/d.get(0);
		}
		if (d.get(1) >= 0) {
			tYMin = (dimMin[1]-e.get(1))/d.get(1);
			tYMax = (dimMax[1]-e.get(1))/d.get(1);
		} else {
			tYMin = (dimMax[1]-e.get(1))/d.get(1);
			tYMax = (dimMin[1]-e.get(1))/d.get(1);
		}
		if ((tMin > tYMax) || (tYMin > tMax)) {
			return false;
		}
		if (tYMin > tMin) {
			tMin = tYMin;
		}
		if (tYMax < tMax) {
			tMax =tYMax;
		}
		if (d.get(2) >= 0) {
			tZMin = (dimMin[2]-e.get(2))/d.get(2);
			tZMax = (dimMax[2]-e.get(2))/d.get(2);
		} else {
			tZMin = (dimMax[2]-e.get(2))/d.get(2);
			tZMax = (dimMin[2]-e.get(2))/d.get(2);
		}
		if ((tMin > tZMax) || (tZMin > tMax)) {
			return false;
		}
		if (tZMin > tMin) {
			tMin = tZMin;
		}
		if (tZMax < tMax) {
			tMax = tZMax;
		}
		return (tMin < t1) && (tMax > t0);
	}

}
