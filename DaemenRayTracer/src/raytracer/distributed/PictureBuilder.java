package raytracer.distributed;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;

import raytracer.display.RenderedImageDisplay;

public class PictureBuilder {

	private static int yResolution;
	private static int xResolution;
	private static int antiAliasingFactor;
	private static boolean hasDepthOfField;
	private static double depthOfField;

	private static List<float[]> rgbData = new ArrayList<float[]>();
	private static String jobName = "distributed";
	private static int jobSize = 500;

	private static RenderedImageDisplay image;
	private static int currentRowIndex = 0;

	private static JFrame frame;

	private static String dataLoc = "/tmp/DaRT/";
	
	
	
	public static void main(String[] args) throws IOException {
		buildImage(jobName, jobSize);
	}

	public static void buildImage(String jobname, int jobsize)
			throws IOException {

		jobName = jobname;
		jobSize = jobsize;

		File dir = new File(dataLoc);
		String[] children = dir.list();

		Map<Integer, String> jobsToDo = new HashMap<Integer, String>();

		loadSettingsFrom(dataLoc + jobname + "_1_" + jobsize + ".dat.zip");
		for (String file : children) {
			String[] parts = file.split("_");
			if (parts[0].equals(jobname)
					&& parts[2].equals(jobSize + ".dat.zip")) {
				jobsToDo.put(Integer.parseInt(parts[1]), dataLoc + file);
			}
		}

		if (yResolution == 0)
			throw new IllegalArgumentException("No such job found");

		for (int i = 1; i <= jobsToDo.size(); i++) {
			System.out.println("Loading data from file: " + jobsToDo.get(i));
			loadDataFrom(jobsToDo.get(i));
			drawSoFar();
			image.saveImage("combined_image.png");
		}

		image.saveImage("combined_image.png");
	}

	private static void loadDataFrom(String string) throws IOException {

		/*
		 * Extract from the archive
		 */
		String datafile = "";
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(string));
			ZipEntry entry = in.getNextEntry();

			datafile = entry.getName();

			OutputStream out = new FileOutputStream(dataLoc + datafile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.close();
			in.close();

			/*
			 * Load the file in ...
			 */
			FileInputStream fileInput = new FileInputStream(new File(dataLoc
					+ datafile));
			DataInputStream dataIn = new DataInputStream(fileInput);

			// First check if the settings are the same.
			boolean settingsAreThesame = (yResolution == dataIn.readInt()
					&& xResolution == dataIn.readInt()
					&& antiAliasingFactor == dataIn.readInt()
					&& hasDepthOfField == dataIn.readBoolean() && depthOfField == dataIn
					.readFloat());
			if (!settingsAreThesame)
				throw new IllegalArgumentException("Settings are not the same!");

			// Start reading the colors
			while (true) {
				float[] color = new float[3];
				try {
					color[0] = dataIn.readFloat();
					color[1] = dataIn.readFloat();
					color[2] = dataIn.readFloat();
				} catch (EOFException eof) {
					break;
				}
				rgbData.add(color);
			}

			try {
				Runtime.getRuntime().exec("rm " + dataLoc + datafile);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
			
			System.err.println("ERROR: " + e.getMessage());
			System.out.println("Typ 'continue' to retry from "
					+ currentRowIndex + ": ");

			while (true) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				if (in.readLine().toUpperCase().equals("CONTINUE")) {
					loadDataFrom(string);
					return;
				}
			}
		}
	}

	private static void loadSettingsFrom(String string) throws IOException {

		/*
		 * Extract from the archive
		 */

		ZipInputStream in = new ZipInputStream(new FileInputStream(string));
		ZipEntry entry = in.getNextEntry();

		String datafile = entry.getName();

		OutputStream out = new FileOutputStream(dataLoc + datafile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		out.close();
		in.close();

		/*
		 * Read from file
		 */
		FileInputStream fileInput = new FileInputStream(new File(dataLoc
				+ datafile));
		DataInputStream dataIn = new DataInputStream(fileInput);

		yResolution = dataIn.readInt();
		xResolution = dataIn.readInt();
		antiAliasingFactor = dataIn.readInt();
		hasDepthOfField = dataIn.readBoolean();
		depthOfField = dataIn.readFloat();
		image = new RenderedImageDisplay(xResolution, yResolution);
		fileInput.close();

		System.out.println("Parsed xResolution is: " + xResolution);
		System.out.println("Parsed yResolution is: " + yResolution);
		System.out.println("Parsed antiAliasingFactor is: "
				+ antiAliasingFactor);
		System.out.println("Parsed hasDepthOfField is: " + hasDepthOfField);
		System.out.println("Parsed depthOfField is: " + depthOfField);

		/*
		 * Delete the file
		 */
		try {
			Runtime.getRuntime().exec("rm " + dataLoc + datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void drawSoFar() {

		int rowSize = xResolution * antiAliasingFactor * antiAliasingFactor;
		int nRowsToDraw = rgbData.size() / rowSize;

		if (nRowsToDraw == 0)
			return;

		float[][] pixelsToDraw = new float[xResolution * nRowsToDraw][];
		for (int i = 0; i < xResolution * nRowsToDraw; i++) {

			float[] pixelColor = new float[3];
			int startIndex = i * antiAliasingFactor;

			pixelColor[0] += rgbData.get(startIndex)[0];
			pixelColor[1] += rgbData.get(startIndex)[1];
			pixelColor[2] += rgbData.get(startIndex)[2];

			for (int row = 0; row < antiAliasingFactor; row++) {
				for (int col = 0; col < antiAliasingFactor; col++) {

					int index = startIndex + col + row * xResolution
							* antiAliasingFactor;

					pixelColor[0] += rgbData.get(index)[0];
					pixelColor[1] += rgbData.get(index)[1];
					pixelColor[2] += rgbData.get(index)[2];

				}
			}

			pixelColor[0] /= (antiAliasingFactor * antiAliasingFactor);
			pixelColor[1] /= (antiAliasingFactor * antiAliasingFactor);
			pixelColor[2] /= (antiAliasingFactor * antiAliasingFactor);

			pixelColor[0] = Math.min(1, pixelColor[0]);
			pixelColor[1] = Math.min(1, pixelColor[1]);
			pixelColor[2] = Math.min(1, pixelColor[2]);

			pixelsToDraw[i] = pixelColor;
		}

		rgbData = new ArrayList<float[]>(rgbData.subList(nRowsToDraw
				* xResolution * antiAliasingFactor * antiAliasingFactor,
				rgbData.size()));

		updateImage(pixelsToDraw);

		currentRowIndex += nRowsToDraw;
	}

	private static void updateImage(float[][] pixelsToDraw) {

		for (int i = 0; i < pixelsToDraw.length; i++) {
			image.drawPixel(xResolution - i % xResolution - 1, currentRowIndex
					+ i / xResolution, pixelsToDraw[i]);
		}

		image.update();
		if (frame == null)
			createFrame();
		frame.repaint();
	}

	private static void createFrame() {
		frame = new JFrame();
		frame.setTitle("DaemenRayTracer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(image, BorderLayout.CENTER);
		frame.setSize(xResolution, yResolution + 25);
		frame.setVisible(true);
	}
}