package raytracer.data.render;
import math.CommonOps;
import math.Vector;


public class SoftLightSource extends LightSource{

	private double size;
	
	public SoftLightSource(String id, Vector position, double size, double[] color) {
		super(id, position, color);
		this.size = size;
	}

	@Override
	public Vector getPosition() {
		Vector pert = new Vector(new double[] {(Math.random()-0.5)*size,(Math.random()-0.5)*size,(Math.random()-0.5)*size});
		CommonOps.unCheckedAdd(super.getPosition(), pert, pert);
		return pert;
	}
}
