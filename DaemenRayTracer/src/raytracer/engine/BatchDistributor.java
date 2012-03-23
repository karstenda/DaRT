package raytracer.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BatchDistributor {

	public static final int BATCH_SIZE = 500;
	
	private final ImagePart imagePart;
	private final int numberOfBatches;
	private final Batch[] finishedBatches;
	private final ProgressUpdater updater;
	private int currentBatchNumber = 0;
	private int nBatchesDone = 0;
	
	
	public BatchDistributor(ImagePart imagePart) {
		this.imagePart = imagePart;
		this.updater = new ProgressUpdater("Overall rendering");
		this.numberOfBatches = Math.round(((float) imagePart.getNumberOfRays())/BATCH_SIZE +0.5f);
		this.finishedBatches = new Batch[numberOfBatches];
	}
	
	public synchronized boolean hasNext() {
		return currentBatchNumber < numberOfBatches;
	}
	
	public synchronized void returnBatchDone(Batch batch) {
		finishedBatches[batch.getNr()] = batch;
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
	
	public double[][] getResult() {
		List<double[]> temp = new ArrayList<double[]>(imagePart.getNumberOfRays());
		for (int i=0; i <finishedBatches.length;i++) {
			temp.addAll(finishedBatches[i].getResult());
		}
		return temp.toArray(new double[imagePart.getNumberOfRays()][]);
	}
	
	public class Batch implements Iterable<Ray> {
		
		private final int[] interval = new int[2];
		private final int nr;
		private List<double[]> result = new ArrayList<double[]>(BATCH_SIZE);
		
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
			this.result.add(color);
		}
		
		public List<double[]> getResult() {
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
