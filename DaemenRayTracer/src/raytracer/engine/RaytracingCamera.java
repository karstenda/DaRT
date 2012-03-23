package raytracer.engine;


import math.CommonOps;
import math.Matrix;
import math.Vector;



public class RaytracingCamera {
	
	final int fovy;
	final Vector position;
	final Vector direction;
	final Vector wayUp;

	private Vector xVector;
	private Vector yVector;
	private Vector baseVector;

	private double widthInterval;
	private double heightInterval;
	private int xResolution;
	private int yResolution;
	private double width;
	private double height;
	private boolean isFullyConfigured;
	
	private int xPic;
	private int yPic;
	private int antiAliasingFactor;
	private double depthOfField;
	private int nRays;
	private boolean hasDepthOfField = false;
	private double depthPerturbationFactor = 1/5d;
	
	
	
	public RaytracingCamera(Vector position, Vector direction, Vector up, int fovy) {
		this.fovy = fovy;
		this.direction = direction;
		this.position = position;
		this.wayUp = up;
		this.depthOfField = 1;
		
	}
		
	public RaytracingCamera(Vector position, Vector direction, Vector up, int fovy, double depthOfField, double depthPerturbationFactor) {
		this(position,direction,up,fovy);
		this.depthOfField = depthOfField;
		this.hasDepthOfField = true;
		this.depthPerturbationFactor = depthPerturbationFactor;
	}
	
	
	public void setViewResolution(int xResolution, int yResolution, int antiAliasingFactor) {
		this.xPic = xResolution;
		this.yPic = yResolution;
		this.antiAliasingFactor = antiAliasingFactor;
		calculateBaseData();
	}
	
	public Ray getRayAtIndex(int i) {
		
		if (!isFullyConfigured)
			throw new IllegalStateException("Is fully configured ...");
		
		double widthFactor = (i % xResolution)*widthInterval - width/2;
		double heightFactor = (i / xResolution)*heightInterval - height/2;
		
		Vector xLocVector = xVector.clone();
		Vector yLocVector = yVector.clone();
		
		yLocVector.scale(heightFactor);
		xLocVector.scale(widthFactor);


		CommonOps.add(yLocVector, xLocVector, xLocVector);
		CommonOps.add(xLocVector, baseVector, xLocVector);
		
		if (hasDepthOfField) {

			//Generate random small perturbation to create depth of field
			Vector pertXLocVector = xVector.clone();
			Vector pertYLocVector = yVector.clone();
			Vector perturbatedEye = position.clone();
			pertXLocVector.scale((Math.random()-0.5d)*depthPerturbationFactor);
			pertYLocVector.scale((Math.random()-0.5d)*depthPerturbationFactor);
			CommonOps.add(perturbatedEye,pertXLocVector,perturbatedEye);
			CommonOps.add(perturbatedEye,pertYLocVector,perturbatedEye);
			CommonOps.add(pertYLocVector,pertXLocVector,pertXLocVector);
			
			// Generate the pertubed ray.
			CommonOps.sub(xLocVector,pertXLocVector,xLocVector);
			xLocVector.normalise();
			return new Ray(perturbatedEye, xLocVector);
		} else {
			xLocVector.normalise();
			return new Ray(position.clone(), xLocVector);
		}
	}

	
	private void calculateBaseData() {
		
		xResolution = antiAliasingFactor*xPic;
		yResolution = antiAliasingFactor*yPic;
		
		//Make yVector orthogonal to direction
		yVector = new Vector(3);
		Matrix P = new Matrix(3,3);
		direction.normalise();
		Matrix normalDirection = new Matrix(direction, true);
		Matrix transposeDirection  = new Matrix(direction, false);
		CommonOps.mult(normalDirection, transposeDirection, P);
		
		CommonOps.mult(P, wayUp, yVector);
		CommonOps.sub(wayUp, yVector, yVector);

		//Create the xVector orthogonal to directen and yVector
		xVector = new Vector(3);
		xVector.set(0, direction.get(1)*yVector.get(2)-direction.get(2)*yVector.get(1));
		xVector.set(1, direction.get(2)*yVector.get(0)-direction.get(0)*yVector.get(2));
		xVector.set(2, direction.get(0)*yVector.get(1)-direction.get(1)*yVector.get(0));
		
		
		// Normalise all the vectors
		yVector.normalise();
		xVector.normalise();
		
		// generate the vector who touches the window in the center
		width = 2*depthOfField*Math.tan(Math.toRadians(fovy/2));
		height = width*((double) yResolution) / ((double) xResolution);
		baseVector = direction.clone();
		baseVector.normalise();
		baseVector.scale(depthOfField);	
		
		widthInterval = width/(xResolution-1);
		heightInterval = height/(yResolution-1);
		
		nRays = xResolution*yResolution;
		
		this.isFullyConfigured = true;
	}
	
	
	public int getNumberOfRays() {
		return nRays;
	}
	
	public double[][] toViewResulotion(double[][] rgbData) {
		double[][] rightData = new double[xPic*yPic][]; 
	
		for(int i = 0; i <xPic; i++) {
			for(int j = 0; j <yPic; j++) {
				
				
				rightData[i+j*xPic] = new double[3];
						
				int baseEntry = i*antiAliasingFactor+j*antiAliasingFactor*antiAliasingFactor*xPic;
				for (int k = 0; k < antiAliasingFactor; k++) {
					for (int l = 0; l < antiAliasingFactor; l++) {
						rightData[i+j*xPic][0] += rgbData[baseEntry+l+k*antiAliasingFactor*xPic][0];
						rightData[i+j*xPic][1] += rgbData[baseEntry+l+k*antiAliasingFactor*xPic][1];
						rightData[i+j*xPic][2] += rgbData[baseEntry+l+k*antiAliasingFactor*xPic][2];
					}
				}
				
				rightData[i+j*xPic][0] /= antiAliasingFactor*antiAliasingFactor;
				rightData[i+j*xPic][1] /= antiAliasingFactor*antiAliasingFactor;
				rightData[i+j*xPic][2] /= antiAliasingFactor*antiAliasingFactor;

			}
		}
		return rightData;
	}
	
	
	public int getxResolution() {
		return xPic;
	}

	public int getyResolution() {
		return yPic;
	}

	public int getAntiAliasingFactor() {
		return antiAliasingFactor;
	}

	public double getDepthOfField() {
		return depthOfField;
	}

	public boolean isHasDepthOfField() {
		return hasDepthOfField;
	}

	
	public Vector getPosition() {
		return position;
	}

	
	public Vector getDirection() {
		return direction;
	}
}
