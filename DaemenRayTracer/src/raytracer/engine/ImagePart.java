package raytracer.engine;

import java.util.Iterator;

public class ImagePart implements Iterable<Ray> {
	
	int startRayIndex = 0;
	int endRayIndex = 0;
	RaytracingCamera camera;
	
	public ImagePart(RaytracingCamera camera, int numPart, int numTotal) {
		int partsize = (int) Math.round(camera.getNumberOfRays()/ numTotal+0.5);
		
		this.startRayIndex = (numPart-1)*partsize;
		this.endRayIndex = (numPart)*partsize;
		if (this.endRayIndex > camera.getNumberOfRays())
			this.endRayIndex = camera.getNumberOfRays();
		this.camera = camera;
	}
	
	
	public int getNumberOfRays() {
		return endRayIndex-startRayIndex;
	}
	
	
	public Ray getRayAtIndex(int i) {		
		return camera.getRayAtIndex((i+startRayIndex));
	}
	
	public RaytracingCamera getCamera() {
		return camera;
	}
	
	@Override
	public Iterator<Ray> iterator() {
		return new Iterator<Ray>() {

			private int index = startRayIndex;
			
			@Override
			public boolean hasNext() {
				return index != endRayIndex;
			}

			@Override
			public Ray next() {
				Ray ray = camera.getRayAtIndex(index);
				index++;
				return ray;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
