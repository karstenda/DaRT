package raytracer.engine;

import java.util.ArrayList;

public class ImagePart2 {
	
	private RaytracingCamera camera;
	private final int nRaysPixel;
	private final int antiAliasing;
	private final int screenWidth;
	private final int numPixels;
	private final int startPixel;
	
	public ImagePart2(RaytracingCamera camera, int numPart, int numTotal) {
		this.camera = camera;
		this.screenWidth = camera.getxResolution();
		this.antiAliasing = camera.getAntiAliasingFactor();
		this.nRaysPixel = antiAliasing * antiAliasing;

		int numPixelsPart = (int) Math.round(camera.getNumberOfRays()/(antiAliasing*antiAliasing*numTotal)+0.5d);
		
		startPixel = numPart*numPixelsPart;
		
		int endPixel = (numPart+1)*numPixelsPart;
		if (endPixel > camera.getNumberOfRays()/(antiAliasing*antiAliasing)) {
			endPixel = camera.getNumberOfRays()/(antiAliasing*antiAliasing);
		}
		
		numPixels = endPixel - startPixel;
	}
	
	
	public int getNumPixels() {
		return numPixels;
	}
	
	public int getNumRaysPixel() {
		return nRaysPixel;
	}
	
	public Pixel getPixel(int index) {
		return new Pixel(startPixel+index,camera);
	}
	
	
	
	public class Pixel {
		
		private final RaytracingCamera camera;
		private final int startIndex;
		
		private boolean isCalculated = false;
		private ArrayList<double[]> colors;
		private double[] pixelColor = new double[3];
		
		public Pixel(int num, RaytracingCamera camera) {
			this.camera = camera;
			this.startIndex = num*nRaysPixel;
			this.colors = new ArrayList<double[]>(nRaysPixel);
		}

		public int getNumRays() {
			return nRaysPixel;
		}
		
		public Ray getRay(int index) {
			return camera.getRayAtIndex(startIndex+index%antiAliasing+(index/antiAliasing)*screenWidth);
		}
		
		public boolean isCalculated() {
			return isCalculated;
		}
		
		public void setColorForRay(int index,double[] color) {
			colors.add(color);
			if (colors.size() == nRaysPixel) {
				calcColor();
			}
		}
		
		private void calcColor() {
			for (double[] color: colors) {
				pixelColor[0] += color[0];
				pixelColor[1] += color[1];
				pixelColor[2] += color[2];
			}
			
			pixelColor[0] = pixelColor[0]/nRaysPixel;
			pixelColor[1] = pixelColor[1]/nRaysPixel;
			pixelColor[2] = pixelColor[2]/nRaysPixel;
			
			colors = null;
			isCalculated = true;
		}
	}
}
