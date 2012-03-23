package raytracer.distributed;

import java.io.DataOutputStream;

import java.io.FileOutputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import raytracer.acceleration.AccelerationStructure;
import raytracer.acceleration.hierarchicalbb.HierarchicalBoundingBoxStructure;
import raytracer.data.scene.Scene;
import raytracer.engine.ImageFormatter;
import raytracer.engine.LocalEngine;
import raytracer.engine.EngineSettings;
import raytracer.engine.ImagePart;
import raytracer.engine.RaytracingCamera;

public class DistributedEngine {

	private static String jobName;
	private static int numJob;
	private static int totJob;

	public static void main(String args[]) {

		try {
			jobName = args[0];
			numJob = Integer.parseInt(args[1]);
			totJob = Integer.parseInt(args[2]);

			if (numJob > totJob) {
				System.out
						.println("The second argument <numJob> has to be greater then the third argument <totJob> ... terminating");
				System.exit(1);
			}

			if (numJob < 0) {
				System.out
						.println("The second argument <numJob> has to be greater then zero ... terminating");
				System.exit(1);
			}

		} catch (NumberFormatException e) {
			System.out
					.println("The last two arguments have to be numeric arguments ... terminating");
			System.exit(1);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out
					.println("Not the right number of arguments ... terminating");
			System.exit(1);
		}

		Scene scene = EngineSettings.SCENE;
		scene.getCamera().setViewResolution(EngineSettings.WIDTH_RESOLUTION,
				EngineSettings.HEIGHT_RESOLUTION,
				EngineSettings.ANTI_ALIASINGFACTOR);
		HierarchicalBoundingBoxStructure structure = new HierarchicalBoundingBoxStructure();
		structure.initialise(scene.getRootObject());
		ImageFormatter form = new ImageFormatter();
		double[][] rgbData = LocalEngine.render(scene, structure,
				new ImagePart(scene.getCamera(), numJob, totJob), form);
		saveDataZip(jobName + "_" + numJob + "_" + totJob, scene, rgbData);
	}

	public static void saveDataZip(String fileName, Scene scene,
			double[][] rgbData) {
		try {

			// Open de file.
			String dataFileName = "result_data/" + fileName + ".dat";
			String zipFileName = dataFileName + ".zip";
			ZipOutputStream fileOutput = new ZipOutputStream(
					new FileOutputStream(zipFileName));
			DataOutputStream dataOut = new DataOutputStream(fileOutput);
			fileOutput.putNextEntry(new ZipEntry(fileName + ".dat"));

			// Schrijf eerst de settings weg.
			RaytracingCamera camera = scene.getCamera();
			dataOut.writeInt(camera.getyResolution());
			dataOut.writeInt(camera.getxResolution());
			dataOut.writeInt(camera.getAntiAliasingFactor());
			dataOut.writeBoolean(camera.isHasDepthOfField());
			dataOut.writeFloat((float) camera.getDepthOfField());

			// Write the color data
			for (double[] color : rgbData) {
				dataOut.writeFloat((float) color[0]);
				dataOut.writeFloat((float) color[1]);
				dataOut.writeFloat((float) color[2]);
			}
			fileOutput.close();

		} catch (Exception e) {
			System.out.println("Error writing away results: " + e.getMessage());
		}
	}

}
