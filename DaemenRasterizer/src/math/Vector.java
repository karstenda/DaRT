package math;

/**
 * <p>
 * This class represents a vector on which some basic operations are defined. An
 * unchecked method means that the arguments of the method will not be checked,
 * the method will behave unexpectedly if these arguments are incorrect or
 * illegal. It's the responsibility of the caller to take care of this.
 * </p>
 * 
 * @author Karsten Daemen
 */
public class Vector {

	private final double[] data;

	/**
	 * <p>
	 * Constructs a new Vector, using a clone of double[] data.
	 * </p>
	 * 
	 * @param data
	 *            data[i] == this.get(i)
	 */
	public Vector(double[] data) {
		this.data = data.clone();
	}

	/**
	 * <p>
	 * Construct a new vector with n elements, which are all 0's.
	 * </p>
	 * 
	 * @param length
	 *            The number of elements in this vector.
	 */
	public Vector(int length) {
		this.data = new double[length];
	}

	/**
	 * Returns the length of the vector.
	 */
	public int getLength() {
		return this.data.length;
	}

	/**
	 * <p>
	 * This constructor is private because it uses a reference to data, not a
	 * clone of data to make this vector. This method is used in the method
	 * {@link wrapAround}.
	 * </p>
	 * 
	 * @param data
	 *            data[i] == this.get(i)
	 */
	private Vector(double[] data, int length) {
		this.data = data;
	}

	/**
	 * <p>
	 * Creates a vector from double[][] data, not from a clone of double[][]
	 * data.
	 * </p>
	 * 
	 * @param data
	 *            data[i][j] == this.get(j,i)
	 */
	public static Vector wrapAround(double[] data) {
		return new Vector(data,data.length);
	}

	/**
	 * <p>
	 * Get a reference to the inner array.
	 * </p>
	 */
	public double[] getData() {
		return data;
	}

	/**
	 * <p>
	 * Get the element on position i.
	 * </p>
	 */
	public double get(int i) {
		return data[i];
	}

	/**
	 * <p>
	 * Sets a new value for the element on place i.
	 * </p>
	 */
	public void set(int i, double element) {
		data[i] = element;
	}

	/**
	 * <p>
	 * Scales the vector A with a factor scale.
	 * </p>
	 */
	public void scale(double scale) {
		for (int i = 0; i < data.length; i++)
			data[i] = scale * data[i];
	}

	/**
	 * <p>
	 * Divides the vector A with a factor scale.
	 * </p>
	 */
	public void divide(double scale) {
		if (scale == 0)
			scale = Double.MIN_NORMAL;
		for (int i = 0; i < data.length; i++)
			data[i] = data[i] / scale;
	}


	/**
	 * <p>
	 * Normalise this vector.
	 * <p>
	 */
	public void normalise() {
		double norm = 0;
		for (int i = 0; i < data.length; i++)
			norm += data[i] * data[i];
		norm = Math.sqrt(norm);
		divide(norm);
	}
	
	/**
	 * <p>
	 * Get the norm of this vector.
	 * </p>
	 */
	public double getNorm() {
		double norm = 0;
		for (int i = 0; i < data.length; i++)
			norm += data[i] * data[i];
		norm = Math.sqrt(norm);
		return norm;
	}

	/**
	 * <p>
	 * The clone method
	 * </p>
	 */
	@Override
	public Vector clone() {
		return new Vector(data.clone());
	}
	
	@Override
	public String toString() {
		return data.toString();
	}

}
