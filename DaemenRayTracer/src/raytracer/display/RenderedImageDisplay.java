package raytracer.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class RenderedImageDisplay extends JPanel{
	
	
	private final BufferedImage image;
	private final int width;
	private final int height;
	
	public RenderedImageDisplay(int width, int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.width = width;
		this.height = height;
	}
	
	
	public void draw(double[][] rgbData) {
		if (rgbData.length != this.width*this.height) {
			throw new IllegalArgumentException("rgbData does not fit the image");
		}
		
		for (int i = 0; i < this.width*this.height; i++) {
			drawPixel(width-i%width-1,i/width,rgbData[i]);
		}
	}
	
	public void drawPixel(int x, int y, double[] rgb) {
		
		if (rgb.length != 3) {
			throw new IllegalArgumentException("rgb array does not contain 3 values, invalid format");
		}
		Color color = new Color((float) rgb[0],(float) rgb[1],(float) rgb[2]);
		this.image.setRGB(x, y, color.getRGB());
	}
	
	
	
	public void drawPixel(int x, int y, float[] rgb) {
		if (rgb.length != 3) {
			throw new IllegalArgumentException("rgb array does not contain 3 values, invalid format");
		}
		Color color = new Color(rgb[0],rgb[1],rgb[2]);
		this.image.setRGB(x, y, color.getRGB());
	}
	
	 @Override
	public void paintComponent(Graphics g) {
		 super.paintChildren(g);
		 g.drawImage(image, 0, 0, null);
	 }
	
	public BufferedImage getBufferedImage() {
		return image;
	}

	public void update() {
		image.flush();
	}
	
	public void saveImage(String file) {
	      try {
	         ImageIO.write(image, "png", new File(file));
	         System.out.println("Saving of image to " + file + " succeeded.");
	      }
	      catch (Exception e) {
	         System.out.println("Saving of image to " + file + " failed.");
	      }
	}


	

}
