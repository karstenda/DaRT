package rasteriser.display;

import java.awt.BorderLayout;

import javax.swing.*;

public class Display {

	
	private RenderedImageDisplay image;

	private JFrame frame;
	
	public Display(int widthResolution, int heightResolution) {
		
		frame = new JFrame();
		frame.setTitle("DaemenRayTracer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new java.awt.BorderLayout());
		image = new RenderedImageDisplay(widthResolution, heightResolution);		
		frame.getContentPane().add(image,BorderLayout.CENTER);
		frame.setSize(widthResolution, heightResolution+25);
		frame.setVisible(true);
	}
	
	public void show() {
		frame.setVisible(true);
	}

	public void hide() {
		frame.setVisible(false);
	}
	
	public void drawPixels(double[][] rgbData) {
		
		
		for (int i=0; i < rgbData.length; i++) {
			rgbData[i][0] = Math.min(1, rgbData[i][0]);
			rgbData[i][1] = Math.min(1, rgbData[i][1]);
			rgbData[i][2] = Math.min(1, rgbData[i][2]);
			rgbData[i][0] = Math.max(0, rgbData[i][0]);
			rgbData[i][1] = Math.max(0, rgbData[i][1]);
			rgbData[i][2] = Math.max(0, rgbData[i][2]);
		}
		
		image.draw(rgbData);
		frame.repaint();
	}
	
	
	public void saveImage(String file) {
		image.saveImage(file);
	}
}
