package raytracer.engine;


import raytracer.acceleration.AccelerationStructure;
import raytracer.data.scene.Scene;
import raytracer.engine.BatchDistributor3.Batch;

public class RenderingThread implements Runnable {
	
	private final GraphicsPipeline pipeline;
	private final int id;
	private final BatchDistributor3 distributor;
	private final Scene scene;
	private AccelerationStructure structure;
	
	public RenderingThread(int id, GraphicsPipeline pipeline,Scene scene, AccelerationStructure structure, BatchDistributor3 distributor) {
		this.pipeline = pipeline;
		this.scene = scene;
		this.id = id;
		this.distributor = distributor;
		this.structure = structure;
	}
	
	public int getId() {
		return id;
	}
	
	public void run() {
		try {
			while(distributor.hasNext()) {
				Batch batch = distributor.getNextBatch();
				for (Ray ray: batch) {
					batch.addResult(pipeline.renderRay(scene, structure, ray));
				}
				distributor.returnBatchDone(batch);
			}
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
