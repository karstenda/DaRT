package rasteriser.display;

public class ToneMapper {

	private double worldLuminance = 0;
	private double maxLum = 0;

	private double[][] data;

	public ToneMapper(double[][] rgbData) {

		this.data = new double[rgbData.length][];

		worldLuminance = 0;
		for (int i = 0; i <rgbData.length; i++) {
			this.data[i] = RGBtoxyY(rgbData[i]);
			worldLuminance =worldLuminance+rgbData[i][2];
		}
		worldLuminance = worldLuminance / ((double) rgbData.length);
		for (int i = 0; i < rgbData.length; i++) {
			data[i][2] = (data[i][2] / worldLuminance) * 0.72;
			if (data[i][2] > maxLum)
				maxLum = data[i][2];
		}

		this.data = rgbData;
	}

	public double[][] getTonedColors() {
		for (int i = 0; i < data.length; i++) {
			data[i][2] = data[i][2] * (1 + data[i][2] / (maxLum * maxLum)) / (1 + data[i][2]);

			data[i] = RGBtoxyY(data[i]);
			
			data[i][0] = Math.min(1, data[i][0]);
			data[i][1] = Math.min(1, data[i][1]);
			data[i][2] = Math.min(1, data[i][2]);
			
			data[i][0] = Math.max(0, data[i][0]);
			data[i][1] = Math.max(0, data[i][1]);
			data[i][2] = Math.max(0, data[i][2]);
		}
		return data;
	}

	public double[] RGBtoxyY(double[] rgb) {
		// Convert from RGB to XYZ
		double X = rgb[0] * 0.4124 + rgb[1] * 0.3576 + rgb[2] * 0.1805;
		double Y = rgb[0] * 0.2126 + rgb[1] * 0.7152 + rgb[2] * 0.0722;
		double Z = rgb[0] * 0.0193 + rgb[1] * 0.1192 + rgb[2] * 0.9505;

		// Convert from XYZ to xyY
		double L = (X + Y + Z);
		double x = X / L;
		double y = Y / L;

		return new double[] {x,y,Y};
	}

	public double[] xyYtoRGB(double[] xyY) {
		// Convert from xyY to XYZ
		double[] rgb = new double[3];
		double X = xyY[0] * (xyY[2] / xyY[1]);
		double Y = xyY[2];
		double Z = (1 - xyY[0] - xyY[1]) * (xyY[2] / xyY[1]);

		// Convert from XYZ to RGB
		rgb[0] = X * 3.2406 + Y * -1.5372 + Z * -0.4986;
		rgb[1] = X * -0.9689 + Y * 1.8758 + Z * 0.0415;
		rgb[2] = X * 0.0557 + Y * -0.2040 + Z * 1.0570;
		
		return rgb;
	}
}
