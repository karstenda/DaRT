package raytracer.engine;

import java.util.Iterator;

import raytracer.engine.ImagePart2.Pixel;

public class BatchDistributor2 {

	public static final int BATCH_SIZE = 500;


	private ImagePart2 imagePart;
	private int numberOfBatches = BATCH_SIZE;	
	private int minPixelsBatch;
	private int numMaxPixelsBatches;
	private int currentBatchNr = 0;
	private int currentPixelNumber = 0;
	
	public BatchDistributor2(ImagePart2 imagePart) {
		this.imagePart = imagePart;
		
		if (imagePart.getNumPixels() < numberOfBatches) {
			numberOfBatches = imagePart.getNumPixels();
		}
		
		minPixelsBatch = imagePart.getNumPixels()/numberOfBatches;
		numMaxPixelsBatches = imagePart.getNumPixels()%numberOfBatches;
	}
	
	public synchronized boolean hasNext() {
		return currentBatchNr < numberOfBatches;
	}
	
	public synchronized Batch getNextBatch() {
		
		int endPixelNumber = currentPixelNumber+minPixelsBatch;
		if (currentBatchNr < numMaxPixelsBatches)
			endPixelNumber++;
		Batch batch = new Batch(currentPixelNumber,endPixelNumber);
		currentBatchNr++;
		currentPixelNumber = endPixelNumber;
		
		return batch;
	}
	
	public class Batch  {

		private int nRays;
		private Pixel currentPixel;
		private int nextPixelNum;
		
		public Batch(int startPixel, int endPixel) {
			this.nextPixelNum = startPixel;
			this.nRays = (endPixel-startPixel)*imagePart.getNumRaysPixel();
		}
		
		public int getNumRays() {
			return nRays;
		}
		
		public Ray getRay(int index) {
			if (index % imagePart.getNumRaysPixel() == 0) {
				currentPixel = imagePart.getPixel(nextPixelNum);
				nextPixelNum++;
			}
			return currentPixel.getRay(index % imagePart.getNumRaysPixel());
		}
		
		public void setResult(int index, double[] color) {
			currentPixel.setColorForRay(index % imagePart.getNumRaysPixel(), color);
		}
				
	}
}
