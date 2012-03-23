package raytracer.data.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {

	private BufferedImage image;
	private final int height;
	private final int width;
	
	private double xRep = 1;
	private double yRep = 1;
	
	public Texture (String pathname) throws IOException {
		image = ImageIO.read(new File(pathname));
		height = image.getHeight();
		width = image.getWidth();
	}
	
	
	public double[] getColor(double x, double y) {
		
		x = x % (1d/xRep);
		y = y % (1d/yRep);
		
		x = x*xRep;
		y = y*yRep;
		
		
		int XPix = (int) Math.round(x*width) % width-1;
		int YPix = (int) Math.round(y*height) % height-1;
		
		while (XPix < 0)
			XPix += width;
		while (YPix < 0)
			YPix += height;

		
		if (XPix == 0) 
			XPix = 1;
		if (YPix == 0)
			YPix = 1;
		
		if (XPix == width-1) 
			XPix = width-2;
		if (YPix == height-1)
			YPix = height-2;
		
		
		Color color = new Color(image.getRGB(XPix, height-1-YPix));

		double red = (((double) color.getRed())/255);
		double green = (((double) color.getGreen())/255);
		double blue = (((double) color.getBlue())/255);
		return new double[] {red, green, blue};
	}
	
	
	public double getTransparancy(double x, double y) {
		
		x = x % (1d/xRep);
		y = y % (1d/yRep);
		
		x = x*xRep;
		y = y*yRep;
		
		
		int XPix = (int) Math.round(x*width) % width-1;
		int YPix = (int) Math.round(y*height) % height-1;
		
		while (XPix < 0)
			XPix += width;
		while (YPix < 0)
			YPix += height;

		Color color = new Color(image.getRGB(XPix, height-1-YPix));

		return color.getAlpha()/255d;

	}


	public void setRepeating(double rep) {
		xRep = rep;
		yRep = rep;
	}
}
