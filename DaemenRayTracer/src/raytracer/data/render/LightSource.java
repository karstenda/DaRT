package raytracer.data.render;

import raytracer.acceleration.hierarchicalbb.BoundingBox;
import math.Vector;

public class LightSource {
	
	private Vector position;
	private double[] color;
	private String id;
	private ThreadLocal<BoundingBox> cachedBox = new ThreadLocal<BoundingBox>();
	
	
	public LightSource(String id, Vector position, double[] color) {
		this.position = position;
		this.color = color;
		this.id = id;
	}
	
	public double[] getColor() {
		return color;
	}

	public void setColor(double[] color) {
		this.color = color;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}
	
	public void setCachedBoundingBox(BoundingBox box) {
		this.cachedBox.set(box);
	}
	
	public BoundingBox getCachedBoundingBox() {
		return this.cachedBox.get();
	}

}
