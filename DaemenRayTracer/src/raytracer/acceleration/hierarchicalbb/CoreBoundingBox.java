package raytracer.acceleration.hierarchicalbb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.CommonOps;
import math.Vector;

import raytracer.data.geometry.Geometry;
import raytracer.data.geometry.IntersectionSolution;
import raytracer.engine.EngineSettings;
import raytracer.engine.Ray;

public class CoreBoundingBox extends BoundingBox {

	private final Collection<? extends Geometry> containedGeometries;
	private final BoundingBoxDimensions dimension;

	public CoreBoundingBox(Collection<? extends Geometry> containedGeometries) {
		
		this.containedGeometries = containedGeometries;
		double max = Double.MAX_VALUE;
		BoundingBoxDimensions dim = new BoundingBoxDimensions(new double[] {max, max, max },	new double[] {-max, -max, -max});
		for (Geometry geometry : containedGeometries) {
			dim = new BoundingBoxDimensions(dim, geometry.getBoundingBoxDimensions());
		}
		this.dimension = dim;
	}

	@Override
	public BoundingBoxDimensions getDimensions() {
		return dimension;
	}
	
	public CoreBoundingBox[] split(int nAxis) {
		Vector center = calcCenter();
		int estimatedSize = containedGeometries.size()/2;
		ArrayList<Geometry> upperGeometries = new ArrayList<Geometry>(estimatedSize);
		ArrayList<Geometry> lowerGeometries = new ArrayList<Geometry>(estimatedSize);
		for (Geometry geometry: containedGeometries) {
			if (geometry.getCenter().get(nAxis) < center.get(nAxis))
				lowerGeometries.add(geometry);
			else
				upperGeometries.add(geometry);
		}
		
		CoreBoundingBox lowerBox = new CoreBoundingBox(lowerGeometries);
		CoreBoundingBox upperBox = new CoreBoundingBox(upperGeometries);
		
		return new CoreBoundingBox[] {lowerBox, upperBox};
	}
	
	private Vector calcCenter() {
		Vector center = new Vector(3);
		for (Geometry geometry: containedGeometries) {
			CommonOps.unCheckedAdd(center, geometry.getCenter(), center);
		}
		center.divide(containedGeometries.size());
		return center;
	}

	@Override
	public int size() {
		return containedGeometries.size();
	}

	@Override
	public List<IntersectionSolution> getIntersectingGeometry(Ray ray) {
		
		List<IntersectionSolution> solutions = null;
		
		for (Geometry geometry: containedGeometries) {
			IntersectionSolution solution = geometry.intersects(ray);
			if (solution != null && solution.getDistance() > EngineSettings.DELTA_MACHINE) {
				if (solutions == null) {
					solutions = new ArrayList<IntersectionSolution>();
				}
				solution.setBoundingBox(this);
				solutions.add(solution);
			}
		}
		
		return solutions;
	}

	@Override
	public int getNumBoxes() {
		return 1;
	}

	@Override
	public int getMaxDepth() {
		return 1;
	}

}
