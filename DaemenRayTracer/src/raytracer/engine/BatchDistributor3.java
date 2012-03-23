package raytracer.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class BatchDistributor3 {

	public static final int BATCH_SIZE = 500;
	
	private final ImagePart imagePart;
	private final int numberOfBatches;
	private final HashMap<Integer,Batch> finishedBatches;
	private final ProgressUpdater updater;
	private int currentBatchNumber = 0;
	private int nBatchesDone = 0;
	private int firstBatchToDraw;
	private ImageFormatter imageFormatter;
	
	public BatchDistributor3(ImagePart imagePart, ImageFormatter imageFormatter) {
		this.imagePart = imagePart;
		this.updater = new ProgressUpdater("Overall rendering");
		this.numberOfBatches = Math.round(((float) imagePart.getNumberOfRays())/BATCH_SIZE +0.5f);
		this.finishedBatches = new HashMap<Integer,Batch>(50);
		this.imageFormatter = imageFormatter;
	}
	
	public synchronized boolean hasNext() {
		return currentBatchNumber < numberOfBatches;
	}
	
	public synchronized void returnBatchDone(Batch batch) {
		
		
		if (batch.getNr() == firstBatchToDraw) {
			ArrayList<Batch> toDraw = new ArrayList<Batch>();
			toDraw.add(batch);
			firstBatchToDraw++;
			
			Batch nextBatch;
			while ((nextBatch = finishedBatches.get(firstBatchToDraw)) != null) {
				toDraw.add(nextBatch);
				finishedBatches.remove(nextBatch);
				firstBatchToDraw++;
			}
			
			imageFormatter.drawBatches(toDraw);
		} else  {
			finishedBatches.put(batch.getNr(),batch);
		}
		nBatchesDone++;
		updater.updateProgress(nBatchesDone,numberOfBatches);
	}
	

	public synchronized Batch getNextBatch() {
		Batch batch;
		if (currentBatchNumber != numberOfBatches-1)
			batch = new Batch(currentBatchNumber,currentBatchNumber*BATCH_SIZE, (currentBatchNumber+1)*BATCH_SIZE);
		else
			batch = new Batch(currentBatchNumber,currentBatchNumber*BATCH_SIZE, imagePart.getNumberOfRays());
		currentBatchNumber++;
		return batch;
	}
	
	
	
	public class Batch implements Iterable<Ray> {
		
		private final int[] interval = new int[2];
		private final int nr;
		private List<float[]> result = new ArrayList<float[]>(BATCH_SIZE);
		
		private Batch(int nr, int start, int end) {
			this.interval[0] = start;
			this.interval[1] = end;
			this.nr = nr;
		}
		
		public int getNr() {
			return this.nr;
		}

		public int getStartRay() {
			return interval[0];
		}
		
		public int getEndRay() {
			return interval[1];
		}
		
		
		public void addResult(double[] color) {
			float[] col = new float[3];
			col[0] = (float) color[0];
			col[1] = (float) color[1];
			col[2] = (float) color[2];
			this.result.add(col);
		}
		
		public List<float[]> getResult() {
			return this.result;
		}

		@Override
		public Iterator<Ray> iterator() {
			return new Iterator<Ray>() {
				
				
				@Override
				public boolean hasNext() {
					return interval[0] != interval[1];
				}

				@Override
				public Ray next() {
					Ray ray = imagePart.getRayAtIndex(interval[0]);
					interval[0]++;
					return ray;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
				
			};
		}
	}
	
}
