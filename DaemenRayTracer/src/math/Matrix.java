package math;

import java.util.Arrays;

/**
 * <p>
 * This class represents a matrix on which some basic operations are defined. An
 * unchecked method means that the arguments of the method will not be checked,
 * the method will behave unexpectedly if these arguments are incorrect or
 * illegal. It's the responsibility of the caller to take care of this.
 * </p>
 * 
 * @author Karsten Daemen
 */
public class Matrix {

	private final double[][] data;
	private int nRows;
	private int nCols;

	/**
	 * <p>
	 * A clone of data is used to construct a new matrix. It is assumed that the
	 * first index i of double[i][j] data corresponds to the collumn number. The
	 * second index j corresponds to the row number.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             The columns of data have to have the same length.
	 * 
	 * @param data
	 *            data[i][j] == this.get(j,i)
	 */
	public Matrix(double[][] data) {
		this.data = data.clone();
		this.nCols = data.length;
		this.nRows = data[0].length;
		for (int i = 0; i < data.length; i++) {
			if (data[i].length != nRows)
				throw new IllegalArgumentException();
		}
	}

	/**
	 * <p>
	 * A clone of the inner data of the vector will be used to construct a
	 * Matrix. The boolean colWise determines whether the vector should be
	 * interpreted as a column or a row.
	 * <p>
	 * 
	 * @param vectorSrc
	 *            The vector to clone from for transformation to a matrix.
	 * @param colWise
	 *            Is the vector a column vector or a row vector? (true -> column
	 *            vector)
	 */
	public Matrix(Vector vectorSrc, boolean colWise) {
		if (colWise) {
			data = new double[1][];
			data[0] = vectorSrc.getData().clone();
			this.nCols = 1;
			this.nRows = vectorSrc.getLength();
		} else {
			data = new double[vectorSrc.getLength()][];
			this.nCols = vectorSrc.getData().length;
			this.nRows = 1;
			for (int i = 0; i < vectorSrc.getLength(); i++) {
				data[i] = new double[1];
				data[i][0] = vectorSrc.getData()[i];
			}

		}
	}

	/**
	 * <p>
	 * Constructs a matrix of dimension nRows x nCols filled with zeros.
	 * </p>
	 * 
	 * @param nRows
	 *            Number of rows of the new matrix.
	 * @param nCols
	 *            Number of columns of the new matrix.
	 */
	public Matrix(int nRows, int nCols) {
		this.nCols = nCols;
		this.nRows = nRows;
		this.data = new double[nCols][];
		for (int i = 0; i < nCols; i++) {
			data[i] = new double[nRows];
		}
	}

	/**
	 * <p>
	 * This constructor is private because it will create a matrix object
	 * without checking of the dimensions of double[][] data and uses a
	 * reference to data, not a clone of data to make this vector. This method
	 * is used in the methods {@link wrapAround} and
	 * {@link unCheckedWrapAroundpAround}.
	 * </p>
	 * 
	 * @param data
	 *            data[i][j] == this.get(j,i)
	 * @param nRows
	 *            Number of rows of the new matrix.
	 * @param nCols
	 *            Number of columns of the new matrix.
	 */
	private Matrix(double[][] data, int nCols, int nRows) {
		this.nCols = nCols;
		this.nRows = nRows;
		this.data = data;
	}

	/**
	 * <p>
	 * Creates a matrix from double[][] data, not from a clone of double[][]
	 * data.
	 * </p>
	 * 
	 * @param data
	 *            data[i][j] == this.get(j,i)
	 * 
	 * @throws IllegalArgumentException
	 *             The columns of data have to have the same length.
	 */
	public static Matrix wrapAround(double[][] data) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].length != data[0].length)
				throw new IllegalArgumentException();
		}
		return unCheckedWrapAround(data);
	}

	/**
	 * <p>
	 * Creates a matrix from double[][] data, not from a clone of double[][]
	 * data. This method does not check the dimensions of double[][] data.
	 * </p>
	 * 
	 * @param data
	 *            data[i][j] == this.get(j,i)
	 */
	public static Matrix unCheckedWrapAround(double[][] data) {
		return new Matrix(data, data.length, data[0].length);
	}

	/**
	 * <p>
	 * Get a reference to the inner double array (column based).
	 * </p>
	 */
	public double[][] getData() {
		return this.data;
	}

	/**
	 * <p>
	 * Returns the element on row i and column j.
	 * </p>
	 */
	public double get(int i, int j) {
		return data[j][i];
	}
	
	/**
	 * <p>
	 * Get the number of rows of this matrix.
	 * </p>
	 */
	public int getnRows() {
		return nRows;
	}

	/**
	 * <p>
	 * Get the number of collumns of this matrix.
	 * </p>
	 */
	public int getnCols() {
		return nCols;
	}

	/**
	 * <p>
	 * Returns column i of this matrix as a new vector.
	 * </p>
	 * 
	 * @param i
	 *            The number of the column.
	 * @return new Vector(this.getData()[i].clone());
	 */
	public Vector getColAsVector(int i) {
		return new Vector(data[i].clone());
	}

	/**
	 * <p>
	 * Returns row i of this matrix as a new vector.
	 * </p>
	 * 
	 * @param i
	 *            The number of the row.
	 * @return A new vector with all the elements of row i.
	 */
	public Vector getRowAsVector(int i) {
		double[] elements = new double[nCols];
		for (int j = 0; j < nCols; j++)
			elements[j] = data[j][i];
		return new Vector(elements);
	}


	/**
	 * <p>
	 * Scales the matrix A with a factor double scale.
	 * </p>
	 * 
	 * @return this.before().getData()[i][j] == this.after().getData()[i][j] /
	 *         scale
	 */
	public void scale(double scale) {
		for (int i = 0; i < nCols; i++)
			for (int j = 0; j < nRows; j++)
				data[i][j] = scale * data[i][j];
	}

	/**
	 * <p>
	 * Divides the matrix A through a factor double scale.
	 * </p>
	 * 
	 * @return this.before().getData()[i][j] == this.after().getData()[i][j] *
	 *         scale
	 */
	public void divide(double scale) {
		for (int i = 0; i < nCols; i++)
			for (int j = 0; j < nRows; j++)
				data[i][j] = data[i][j] / scale;
	}

	/**
	 * <p>
	 * Inserts row filled with ones at the bottom of the matrix.
	 * </p>
	 */
	public void addHomogeneousCoordinates() {
		double[] ones = new double[data[0].length+1];
		Arrays.fill(ones, 1d);
		for (int i = 0; i < nCols; i++) {
			double[] newCol = ones.clone();
			System.arraycopy(data[i], 0, newCol, 0, data[i].length);
			data[i] = newCol;
		}
		nRows++;
	}

	/**
	 * <p>
	 * Removes the last row of the matrix.
	 * </p>
	 */
	public void removeHomogeneousCoordinates() {
		for (int i = 0; i < nCols; i++) {
			double[] newCol = new double[data[i].length - 1];
			System.arraycopy(data[i], 0, newCol, 0, data[i].length - 1);
			data[i] = newCol;	
		}
		nRows--;
	}

	/**
	 * <p>
	 * Returns a new matrix with the exact same elements.
	 * <p>
	 * 
	 * @return new Matrix(this.getData())
	 * 
	 */
	public Matrix copy() {
		return new Matrix(this.data);
	}
}
