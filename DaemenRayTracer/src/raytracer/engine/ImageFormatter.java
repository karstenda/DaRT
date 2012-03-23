package raytracer.engine;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import raytracer.display.RenderedImageDisplay;
import raytracer.engine.BatchDistributor3.Batch;

public class ImageFormatter {

	int xResolution = EngineSettings.WIDTH_RESOLUTION;
	int yResolution = EngineSettings.HEIGHT_RESOLUTION;
	int antiAliasingFactor = EngineSettings.ANTI_ALIASINGFACTOR;
	
	private RenderedImageDisplay image = new RenderedImageDisplay(xResolution, yResolution);
	private int currentRowIndex = 0;
	
	ArrayList<float[]> rgbData = new ArrayList<float[]>(xResolution*antiAliasingFactor*antiAliasingFactor*10);
	
	private JFrame frame;

	private String dataLoc = "/tmp/DaRT/";
	
	
	
	
	public void drawBatches(List<Batch> batches) {
		
		for (Batch batch: batches) {
			rgbData.addAll(batch.getResult());
		}
		
		drawSoFar();
	}
	
	
	private void drawSoFar() {

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
	
	private void updateImage(float[][] pixelsToDraw) {

		for (int i = 0; i < pixelsToDraw.length; i++) {
			image.drawPixel(xResolution - i % xResolution - 1, currentRowIndex
					+ i / xResolution, pixelsToDraw[i]);
		}

		image.update();
		if (frame == null)
			createFrame();
		frame.repaint();
	}
	
	private void createFrame() {
		frame = new JFrame();
		frame.setTitle("DaemenRayTracer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(image, BorderLayout.CENTER);
		frame.setSize(xResolution, yResolution + 25);
		frame.setVisible(true);
	}
	
	public void saveImage() {
		image.saveImage(dataLoc+"/backup.png");
		image.saveImage("result_pictures/"+EngineSettings.SCENE.getName()+"_"+xResolution+"_"+yResolution+".png");
	}
}
