package raytracer.acceleration.hierarchicalbb;

public class BoundingBoxDimensions {

	private final double[] smallPoint;
	private final double[] bigPoint;
	
	public BoundingBoxDimensions(double[] smallPoint, double[] bigPoint) {
		this.smallPoint = smallPoint;
		this.bigPoint = bigPoint;
	}
	
	public BoundingBoxDimensions(BoundingBox box1, BoundingBox box2) {
		this(box1.getDimensions(), box2.getDimensions());
	}
	
	public BoundingBoxDimensions(BoundingBoxDimensions dim1, BoundingBoxDimensions dim2) {
		
		this.smallPoint = new double[3];
		this.bigPoint = new double[3];
		
		double[] smallPoint1 = dim1.getSmallPoint();
		double[] bigPoint1 = dim1.getBigPoint();
		double[] smallPoint2 = dim2.getSmallPoint();
		double[] bigPoint2 = dim2.getBigPoint();
		
		this.smallPoint[0] = Math.min(smallPoint1[0], smallPoint2[0]);
		this.smallPoint[1] = Math.min(smallPoint1[1], smallPoint2[1]);
		this.smallPoint[2] = Math.min(smallPoint1[2], smallPoint2[2]);
		
		this.bigPoint[0] = Math.max(bigPoint1[0], bigPoint2[0]);
		this.bigPoint[1] = Math.max(bigPoint1[1], bigPoint2[1]);
		this.bigPoint[2] = Math.max(bigPoint1[2], bigPoint2[2]);
	}

	public double[] getSmallPoint() {
		return smallPoint;
	}

	public double[] getBigPoint() {
		return bigPoint;
	}
}
