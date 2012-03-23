package rasteriser.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rasteriser.data.Mesh;
import rasteriser.data.Mesh.Triangle;
import rasteriser.display.Display;
import rasteriser.scene.SceneObject;

import math.CommonOps;
import math.Matrix;
import math.Transformation;
import math.Vector;


public class Engine {

	private static Map<Integer,double[][]> verticeColors;
	
	public static void main(String[] args) {

		SceneObject object = EngineSettings.SCENE.getRootObject();
		RasterisationCamera camera = EngineSettings.SCENE.getCamera();
		Collection<LightSource> lightsources = EngineSettings.SCENE.getLightSources();
		
		Vector[] boundingBox = object.getBoundingBox();
		System.out.println("    -> Bounding box: "+
				"\n          -> x: "+boundingBox[0].get(0)+" : "+boundingBox[1].get(0)+
				"\n          -> y: "+boundingBox[0].get(1)+" : "+boundingBox[1].get(1)+
				"\n          -> z: "+boundingBox[0].get(2)+" : "+boundingBox[1].get(2));
		
		
		/*
		 * Calculating vertice colors
		 */
		System.out.println("\n==> Calculating the vertice colors ...");

		verticeColors = new HashMap<Integer,double[][]>();
		for (Triangle triangle: object) {
			double[][] colors = RasterisationMath.calcVerticeColors(triangle, lightsources);
			verticeColors.put(triangle.getIndex(),colors);
		}
		
		
		
		/*
		 * Construct the camera matrixes
		 */
		
		System.out.println("\n==> Constructing the camera matrix ...");
		Vector[] baseVector = camera.getBaseVectors();
		double[] col1 = new double[] {-baseVector[0].get(0),baseVector[1].get(0),-baseVector[2].get(0),0};
		double[] col2 = new double[] {-baseVector[0].get(1),baseVector[1].get(1),-baseVector[2].get(1),0};
		double[] col3 = new double[] {-baseVector[0].get(2),baseVector[1].get(2),-baseVector[2].get(2),0};
		Matrix viewM1 = new Matrix(new double[][] {col1, col2, col3 ,new double[] {0,0,0,1}});
		Matrix viewM2 = Transformation.getTranslationTransformation(new Vector(new double[] {-camera.getPosition().get(0),-camera.getPosition().get(1),-camera.getPosition().get(2)}));
		Matrix Mv = new Matrix(4,4);
		CommonOps.unCheckedMult(viewM1,viewM2,Mv);
		
		
		/*
		 *  Applying camera matrices 
		 */
		
		System.out.println("\n==> Applying camera matrices ...");
		object.applyTransformation(Mv);

		
		/*
		 * Getting the bounding box
		 */
		boundingBox = object.getBoundingBox();
		System.out.println("    -> Bounding box: "+
				"\n          -> x: "+boundingBox[0].get(0)+" : "+boundingBox[1].get(0)+
				"\n          -> y: "+boundingBox[0].get(1)+" : "+boundingBox[1].get(1)+
				"\n          -> z: "+boundingBox[0].get(2)+" : "+boundingBox[1].get(2));
		

		/*
		 * Construct the Canonical View matrix
		 */
		
		System.out.println("\n==> Started construction rasterisation matrixes ...");
		double nx = EngineSettings.WIDTH_RESOLUTION;
		double ny = EngineSettings.HEIGHT_RESOLUTION;
		Matrix cvv = new Matrix(new double[][] {
				new double[] { nx/2d, 0, 0, 0 },
				new double[] { 0, ny/2d, 0, 0 },
				new double[] { 0, 0, 1, 0 },
				new double[] { (nx-1)/2d, (ny-1)/2d, 0, 1 } });

		
		/*
		 * Construct the Orthographic Projection matrix 
		 */
			
		double n = boundingBox[1].get(2);
		double f = boundingBox[0].get(1);
		
		double t = Math.tan(Math.toRadians(camera.getFovY()/2d))*Math.abs(n);
		double r = t*(nx/ny);
		double l = -r;
		double b = -t;

		
		System.out.println("window is: (l: "+l+", b: "+b+", n: "+n+") (r: "+r+", t: "+t+", f: "+f+")");
		
		
		Matrix orthographicScaleProj = Transformation
				.getScalingTransformation(new Vector(new double[] {
						2/(r-l), 2/(t-b), 2/(n-f)}));
		Matrix orthographicTransProj = Transformation
				.getTranslationTransformation(new Vector(new double[] {
						-(l+r)/2, -(b+t)/2, -(n+f)/2 }));

		
		Matrix ortho = new Matrix(4,4);
		CommonOps.unCheckedMult(orthographicScaleProj,orthographicTransProj, ortho);
		
		/*
		 * Perspective matrix
		 */
		
		Matrix Mp = new Matrix(new double[][] {new double[] {n,0,0,0},new double[] {0,n,0,0},new double[] {0,0,n+f,1},new double[] {0,0,-f*n,0}});
		
		
		
		
		/*
		 * Apply all the rasterisation matrices
		 */
		Matrix.debug(Mv);
		System.out.println("Mp");
		Matrix.debug(Mp);
		System.out.println("Ortho");
		Matrix.debug(ortho);
		System.out.println("cvv");
		Matrix.debug(cvv);
		
		System.out.println("\n==> Applying all the rasterisation matrixes ...");
		object.applyTransformation(Mp);
		object.applyTransformation(ortho);
		object.applyTransformation(cvv);
		
		
		
		/*
		 * Start Rendering
		 */
		
		System.out.println("\n==> Start rendering the projected scene ...");
		renderScene(object);
	}

	private static double[] zbuffer = new double[EngineSettings.HEIGHT_RESOLUTION
			* EngineSettings.WIDTH_RESOLUTION];
	private static Triangle[] closestTriangles = new Triangle[EngineSettings.HEIGHT_RESOLUTION
			* EngineSettings.WIDTH_RESOLUTION];

	public static void renderScene(SceneObject object) {
		
		for (Triangle triangle : object) {
			Map<Integer, Double> depth = getDepthPixelsInTriangle(triangle);
			for (int index : depth.keySet()) {

				if (closestTriangles[index] == null	|| zbuffer[index] > depth.get(index)) {

					zbuffer[index] = depth.get(index);
					closestTriangles[index] = triangle;
				}
			}
		}

		double[][] rgbData = new double[zbuffer.length][];
		for (int i = 0; i < rgbData.length; i++) {
			
			if (closestTriangles[i] ==  null) {
				rgbData[i] = new double[3];
			} else {
				double[] baryCentric = RasterisationMath.calculateBaryCentricCoordinates(i, closestTriangles[i]);
				double[][] colors = verticeColors.get(closestTriangles[i].getIndex());
			
				rgbData[i] = new double[3];
				
				rgbData[i][0] = (baryCentric[0]*colors[0][0]+baryCentric[1]*colors[1][0]+baryCentric[2]*colors[2][0]);
				rgbData[i][1] = (baryCentric[0]*colors[0][1]+baryCentric[1]*colors[1][1]+baryCentric[2]*colors[2][1]);
				rgbData[i][2] = (baryCentric[0]*colors[0][2]+baryCentric[1]*colors[1][2]+baryCentric[2]*colors[2][2]);
				
				rgbData[i][0] = Math.min(1, rgbData[i][0]);
				rgbData[i][1] = Math.min(1, rgbData[i][1]);
				rgbData[i][2] = Math.min(1, rgbData[i][2]);
			}
		}

		Display display = new Display(EngineSettings.WIDTH_RESOLUTION,EngineSettings.HEIGHT_RESOLUTION);
		display.drawPixels(rgbData);

	}

	public static Map<Integer, Double> getDepthPixelsInTriangle(Triangle triangle) {
		Map<Integer, Double> pixelsInTriangle = new HashMap<Integer, Double>();
		
		List<Integer> pixelsInBox = getPixelsInBoundingBoxOf(triangle);
		for (int index : pixelsInBox) {
			double depth;
			if ((depth = RasterisationMath.interpolateDepth(index, triangle)) >= 0) {
				pixelsInTriangle.put(index, depth);
			}

		}
		return pixelsInTriangle;
	}

	public static List<Integer> getPixelsInBoundingBoxOf(Triangle triangle) {
		Vector[] box = getBoundingBox(triangle);
		return getPixelIndexesInBoundingBox(box);
	}

	public static Vector[] getBoundingBox(Triangle triangle) {

		double xMin = Double.MAX_VALUE;
		double xMax = -Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = -Double.MAX_VALUE;

		for (Vector vector : triangle.getVerticesAsVectors()) {

			if (vector.get(0) <= xMin)
				xMin = vector.get(0);
			if (vector.get(0) >= xMax)
				xMax = vector.get(0);
			if (vector.get(1) <= yMin)
				yMin = vector.get(1);
			if (vector.get(1) >= yMax)
				yMax = vector.get(1);
		}

		Vector minVec = new Vector(new double[] { xMin, yMin });
		Vector maxVec = new Vector(new double[] { xMax, yMax });

		return new Vector[] { minVec, maxVec };
	}

	public static List<Integer> getPixelIndexesInBoundingBox(Vector[] box) {
		
		int upLeftPixelXCo = (int) Math.round(box[0].get(0)+0.5);
		int upLeftPixelYCo = (int) Math.round(box[1].get(1)-0.5);
		upLeftPixelYCo = EngineSettings.HEIGHT_RESOLUTION- upLeftPixelYCo-1;
		
		int upRightPixelXCo = (int) Math.round(box[1].get(0)-0.5);
		
		int downLeftPixelYCo = (int) Math.round(box[0].get(1)+0.5);
		downLeftPixelYCo = EngineSettings.HEIGHT_RESOLUTION - downLeftPixelYCo-1;
		
		int width = upRightPixelXCo - upLeftPixelXCo+1;
		int height = downLeftPixelYCo - upLeftPixelYCo+1;
		
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		int startPixelIndex = upLeftPixelXCo+upLeftPixelYCo*EngineSettings.WIDTH_RESOLUTION;
		for (int i = 0;i<height;i++) {
			for (int j = 0; j<width;j++) {
				int index = startPixelIndex + j + i*(EngineSettings.WIDTH_RESOLUTION);
				
				if (index > 0 && index < EngineSettings.HEIGHT_RESOLUTION*EngineSettings.WIDTH_RESOLUTION)
					indexes.add(index);
			}
		}
		
		return indexes;

	}
	
}
