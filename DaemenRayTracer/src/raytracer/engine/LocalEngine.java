package raytracer.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import raytracer.acceleration.AccelerationStructure;
import raytracer.acceleration.hierarchicalbb.HierarchicalBoundingBoxStructure;
import raytracer.acceleration.hierarchicalbb.SceneBoundingBoxStructure;
import raytracer.data.scene.Scene;
import raytracer.display.Display;
import raytracer.distributed.DistributedEngine;

public class LocalEngine {

	public static void main(String args[]) throws IOException {
		Scene scene = EngineSettings.SCENE;
		scene.getCamera().setViewResolution(EngineSettings.WIDTH_RESOLUTION, EngineSettings.HEIGHT_RESOLUTION, EngineSettings.ANTI_ALIASINGFACTOR);
		
		// Create an acceleration structure.
		System.out.println("\n=> MAKING ACCELERATION STRUCTURE ...");
		HierarchicalBoundingBoxStructure structure = new HierarchicalBoundingBoxStructure();
		structure.initialise(scene.getRootObject());
		System.out.println("    ---> constructed acceleration structure!");
		System.out.println("         -> # boxes: "+structure.getNumBoxes());
		System.out.println("         -> Max Depth: "+structure.getMaxDepth()+"\n");
		
		ImageFormatter imageFormatter = new ImageFormatter();
		double[][] rgbData = render(scene,structure,new ImagePart(scene.getCamera(),1,1),imageFormatter);
		imageFormatter.saveImage();
	}
	
	public static double[][] render(Scene scene, AccelerationStructure structure, ImagePart imagePart, ImageFormatter imageFormatter) {
		double[][] rgbData = startMultiRenderingThreads(scene, structure, imagePart, imageFormatter);
		return rgbData;
	}

	/**
	 * Start het renderen van de scene in imagePart, schakel hiervoor meerdere RenderingThreads in.
	 */
	private static double[][] startMultiRenderingThreads(Scene scene, AccelerationStructure structure, ImagePart imagePart, ImageFormatter imageFormatter) {

		double milliesStart = System.currentTimeMillis();

		ExecutorService pool = Executors
				.newFixedThreadPool(EngineSettings.N_CONCURRENT_THREADS);
		ArrayList<RenderingThread> threads = new ArrayList<RenderingThread>();
		BatchDistributor3 distributor = new BatchDistributor3(imagePart, imageFormatter);
		for (int i = 0; i < EngineSettings.N_CONCURRENT_THREADS; i++) {
			System.out.println("=> Starting up Pipeline " + i + ".");
			threads.add(new RenderingThread(i, new GraphicsPipeline(i),
					scene, structure, distributor));
			pool.submit(threads.get(i));
		}
		pool.shutdown();

		/*
		 * Start executing
		 */
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(7, TimeUnit.DAYS)) {

				// Cancel currently executing tasks
				pool.shutdownNow();

				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
					System.err.println("Pool did not terminate");
				}

			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}

		long seconds = Math
				.round((((double) System.currentTimeMillis()) - milliesStart) / 1000d);
		System.out.println("===> Rendered whole Scene in " + seconds
				+ " seconds <===");

		/*
		 * Recombine the results
		 */
//		return distributor.getResult();
		return null;
	}
	
	
	
	private static void showResult(Scene scene, double[][] rgbData) {

		// createResultData
		String resultName = scene.getName() + "_" + EngineSettings.WIDTH_RESOLUTION + "_"
				+ EngineSettings.HEIGHT_RESOLUTION;

		// Save colorData
//		DistributedEngine.saveDataZip(resultName, scene, rgbData);

		
		// Display
		rgbData = scene.getCamera().toViewResulotion(rgbData);
		Display display = new Display(EngineSettings.WIDTH_RESOLUTION, EngineSettings.HEIGHT_RESOLUTION);
		display.drawPixels(rgbData);

		// Save
		display.saveImage("result_pictures/" + resultName + ".png");
		display.saveImage("/tmp/" + resultName + ".png");
	}
}