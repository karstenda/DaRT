package raytracer.data.geometry;

import java.util.Comparator;

public class IntersectionsDistanceComparator implements Comparator<IntersectionSolution> {


	@Override
	public int compare(IntersectionSolution o1,IntersectionSolution o2) {
		if (o1.getDistance() < o2.getDistance())
			return -1;
		else
			return 1;
	}

	
}
