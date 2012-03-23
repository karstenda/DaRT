package rasteriser.engine;

import math.CommonOps;
import math.Matrix;
import math.Vector;

public class RasterisationCamera {
	
	final int fovy;
	final Vector position;
	final Vector direction;
	final Vector wayUp;

	private Vector xVector;
	private Vector yVector;
	private Vector zVector;


	public RasterisationCamera(Vector position, Vector direction, Vector up, int fovy) {
		this.fovy = fovy;
		this.direction = direction;
		this.position = position;
		this.wayUp = up;
		calculateBaseData();
	}

	
	private void calculateBaseData() {
		
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
		
		// zVector is direction
		zVector = direction.clone();
		
		// Normalise all the vectors
		yVector.normalise();
		xVector.normalise();
		
	}
	
	public Vector getPosition() {
		return position;
	}
	
	public Vector getDirection() {
		return direction;
	}
	
	public Vector[] getBaseVectors() {
		return new Vector[]  {
				xVector,
				yVector,
				zVector,
		};
	}
	
	public int getFovY() {
		return fovy;
	}
}