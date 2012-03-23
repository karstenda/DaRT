package math;


public abstract class Transformation {

	public static Matrix getScalingTransformation(double scale) {
		return new Matrix(new double[][] {	new double[] {scale,0,0,0},
											new double[] {0,scale,0,0},
											new double[] {0,0,scale,0},
											new double[] {0,0,0,scale}});
	}
	
	
	public static Matrix getScalingTransformation(Vector scaling) {
		
		return new Matrix(new double[][] {	new double[] {scaling.get(0),0,0,0},
											new double[] {0,scaling.get(1),0,0},
											new double[] {0,0,scaling.get(2),0},
											new double[] {0,0,0,1}});
	}
	
	public static Matrix getTranslationTransformation(Vector translation) {
		
		return new Matrix(new double[][] {	new double[] {1,0,0,0},
											new double[] {0,1,0,0},
											new double[] {0,0,1,0},
											new double[] {translation.get(0),translation.get(1),translation.get(2),1}});
	}
	
	public static Matrix getRotationAroundX(double radians) {
		return new Matrix(new double[][] {	new double[] {1,0,0,0},
											new double[] {0,Math.cos(radians),Math.sin(radians),0},
											new double[] {0,-Math.sin(radians),Math.cos(radians),0},
											new double[] {0,0,0,1}});
	}
	
	public static Matrix getRotationAroundY(double radians) {
		return new Matrix(new double[][] {	new double[] {Math.cos(radians),0,-Math.sin(radians),0},
											new double[] {0,1,0,0},							
											new double[] {Math.sin(radians),0,Math.cos(radians),0},
											new double[] {0,0,0,1}});
	}
	
	public static Matrix getRotationAroundZ(double radians) {
		return new Matrix(new double[][] {	new double[] {Math.cos(radians),Math.sin(radians),0,0},							
											new double[] {-Math.sin(radians),Math.cos(radians),0,0},
											new double[] {0,0,1,0},
											new double[] {0,0,0,1}});
	}
}
