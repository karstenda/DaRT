package math;

/**
 * And abstract class containing some static common vector and matrix
 * operations.
 * 
 * @author Karsten Daemen
 * 
 */
public abstract class CommonOps {

	/**
	 * <p>
	 * Subtracts the matrix B from A and stores the result in matrix C, this may
	 * be A or B.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             If the dimensions of A, B and C or not the same.
	 */
	public static void sub(Matrix A, Matrix B, Matrix C) {
		if (A.getnCols() != B.getnCols() || A.getnCols() != C.getnCols()
				|| A.getnRows() != B.getnRows() || A.getnRows() != C.getnRows())
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		else
			unCheckedSub(A, B, C);
	}

	/**
	 * <p>
	 * Subtracts the matrix B from A and stores the result in matrix C, this may
	 * be A or B. Remark that it's the users responsibility to make sure that
	 * each matrix has the same dimensions.
	 * </p>
	 */
	public static void unCheckedSub(Matrix A, Matrix B, Matrix C) {
		double[][] dataA = A.getData();
		double[][] dataB = B.getData();
		double[][] dataC = C.getData();
		for (int i = 0; i < A.getnCols(); i++) {
			for (int j = 0; j < B.getnRows(); j++) {
				dataC[i][j] = dataA[i][j] - dataB[i][j];
			}
		}
	}

	/**
	 * <p>
	 * Adds the matrix B to A and stores the result in matrix C, this may be A
	 * or B.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             If the dimensions of A, B and C or not the same.
	 */
	public static void add(Matrix A, Matrix B, Matrix C) {
		if (A.getnCols() != B.getnCols() || A.getnCols() != C.getnCols()
				|| A.getnRows() != B.getnRows() || A.getnRows() != C.getnRows())
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		else
			unCheckedAdd(A, B, C);
	}

	/**
	 * <p>
	 * Adds the matrix B to A and stores the result in matrix C, this may be A
	 * or B. Remark that it's the users responsibility to make sure that each
	 * matrix has the same dimensions.
	 * </p>
	 */
	public static void unCheckedAdd(Matrix A, Matrix B, Matrix C) {
		double[][] dataA = A.getData();
		double[][] dataB = B.getData();
		double[][] dataC = C.getData();
		for (int i = 0; i < A.getnCols(); i++) {
			for (int j = 0; j < B.getnRows(); j++) {
				dataC[i][j] = dataA[i][j] + dataB[i][j];
			}
		}
	}

	/**
	 * <p>
	 * multiplies the matrix A with B and stores the result in matrix C, the
	 * inner data arrays of C may NOT be the same as these in A or B (So be
	 * careful when multiplying matrixes from methods like {@link
	 * Vector.wrapAround()} or {@link Matrix.wrapAround()}).
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             If the dimensions of A, B and C are illegal for a matrix
	 *             multiplication.
	 * @throws IllegalArgumentException
	 *             If the inner data arrays of A and C or B and C are the same.
	 */
	public static void mult(Matrix A, Matrix B, Matrix C) {
		if (A.getData().equals(C.getData()) || B.getData().equals(C.getData()))
			throw new IllegalArgumentException(
					"Inner data structures are the same.");
		else if (A.getnCols() != B.getnRows())
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		else
			unCheckedMult(A, B, C);

	}

	/**
	 * <p>
	 * multiplies the matrix A with B and stores the result in matrix C, the
	 * inner data arrays of C may NOT be the same as these in A or B (So be
	 * careful when multiplying matrixes from methods like {@link
	 * Vector.wrapAround()} or {@link Matrix.wrapAround()}). Remark that it's
	 * the users responsibility to make sure of this.
	 * </p>
	 */
	public static void unCheckedMult(Matrix A, Matrix B, Matrix C) {
		double[][] dataA = A.getData();
		double[][] dataB = B.getData();
		double[][] dataC = C.getData();
		for (int i = 0; i < B.getnCols(); i++) {
			for (int j = 0; j < A.getnRows(); j++) {
				for (int k = 0; k < A.getnCols(); k++)
					dataC[i][j] += dataA[k][j] * dataB[i][k];
			}
		}
	}

	/**
	 * <p>
	 * Multiplies the matrix A with the vector B and stores the result in vector
	 * C, the inner data arrays of C may NOT be the same as the one in B (So be
	 * careful when using vectors from methods like {@link Vector.wrapAround()}
	 * or {@link Matrix.wrapAround()}).
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             If the dimensions of A, B and C are illegal for a
	 *             matrix-vector multiplication.
	 * @throws IllegalArgumentException
	 *             If the inner data arrays of B and C are the same.
	 */
	public static void mult(Matrix A, Vector B, Vector C) {
		if (B.getData().equals(C.getData()))
			throw new IllegalArgumentException(
					"Inner data structures are the same.");
		if (A.getnCols() != B.getLength() || A.getnRows() != C.getLength())
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		else
			unCheckedMult(A, B, C);
	}

	/**
	 * <p>
	 * Multiplies the matrix A with the vector B and stores the result in vector
	 * C, the inner data arrays of C may NOT be the same as the one in B (So be
	 * careful when using vectors from methods like {@link Vector.wrapAround()}
	 * or {@link Matrix.wrapAround()}). It's the user responsibility to make
	 * sure that each vector has the same dimensions.
	 * </p>
	 * 
	 */
	public static void unCheckedMult(Matrix A, Vector B, Vector C) {
		double[][] dataA = A.getData();
		double[] dataB = B.getData();
		double[] dataC = C.getData();
		for (int j = 0; j < A.getnRows(); j++) {
			for (int k = 0; k < A.getnCols(); k++)
				dataC[j] += dataA[k][j] * dataB[k];
		}
	}

	/**
	 * <p>
	 * Adds the vector B to the vector A and stores the result in vector C.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             If the dimensions of A, B and C are not the same.
	 */
	public static void add(Vector A, Vector B, Vector C) {
		if (A.getLength() != B.getLength() || A.getLength() != C.getLength())
			throw new IllegalArgumentException("Illegal vectors dimensions");
		else
			unCheckedAdd(A, B, C);
	}

	/**
	 * <p>
	 * Adds the vector B to the vector A and stores the result in vector C. It's
	 * the user responsibility to make sure that each vector has the same
	 * dimensions.
	 * </p>
	 */
	public static void unCheckedAdd(Vector A, Vector B, Vector C) {
		double[] dataA = A.getData();
		double[] dataB = B.getData();
		double[] dataC = C.getData();
		for (int i = 0; i < dataA.length; i++)
			dataC[i] = dataA[i] + dataB[i];
	}

	/**
	 * <p>
	 * Subtracts the vector B from the vector A and stores the result in vector
	 * C.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             If the dimensions of A, B and C are not the same.
	 */
	public static void sub(Vector A, Vector B, Vector C) {
		if (A.getLength() != B.getLength() || A.getLength() != C.getLength())
			throw new IllegalArgumentException("Illegal vectors dimensions");
		else
			unCheckedSub(A, B, C);
	}

	/**
	 * <p>
	 * Subtracts the vector B from the vector A and stores the result in vector
	 * C. It's the user responsibility to make sure that each vector has the
	 * same dimensions.
	 * </p>
	 */
	public static void unCheckedSub(Vector A, Vector B, Vector C) {
		double[] dataA = A.getData();
		double[] dataB = B.getData();
		double[] dataC = C.getData();
		for (int i = 0; i < dataA.length; i++)
			dataC[i] = dataA[i] - dataB[i];
	}

	/**
	 * <p>
	 * Returns the dot product of the two vectors.
	 * </p>
	 * 
	 * @throws IllegalArgumentException
	 *             When vectors A and B have different dimensions.
	 */
	public static double dotProduct(Vector A, Vector B) {
		if (A.getLength() != B.getLength())
			throw new IllegalArgumentException(
					"Both vectors have different dimensions.");
		else
			return unCheckedDotProduct(A, B);
	}

	/**
	 * <p>
	 * Returns the dot product of the two vectors. It's the user responsibility
	 * to make sure that each vector has the same dimensions.
	 * </p>
	 */
	public static double unCheckedDotProduct(Vector A, Vector B) {
		double result = 0;
		double[] dataA = A.getData();
		double[] dataB = B.getData();
		for (int i = 0; i < A.getLength(); i++)
			result += dataA[i] * dataB[i];
		return result;
	}
	
	
	/**
	 * <p>
	 * Returns the angle between the two vectorsn. It's the user responsibility
	 * to make sure that each vector has the same dimensions.
	 * </p>
	 */
	public static double unCheckedAngle(Vector A, Vector B) {
		double product = unCheckedDotProduct(A, B);
		product = product / (A.getNorm()*B.getNorm());
		return Math.acos(product);
	}

	
	public static void crossProduct3D(Vector A, Vector B, Vector C) {
		if (B.getData().equals(C.getData()))
			throw new IllegalArgumentException(
					"Inner data structures are the same.");
		if (A.getLength() != 3 || B.getLength() != 3)
			throw new IllegalArgumentException(
					"Vectors should both be 3D vectors");
		unCheckedCrossPorduct3D(A, B, C);
		
	}
	
	public static void unCheckedCrossPorduct3D(Vector A, Vector B, Vector C) {
		C.set(0, A.get(1)*B.get(2)-A.get(2)*B.get(1));
		C.set(1, A.get(2)*B.get(0)-A.get(0)*B.get(2));
		C.set(2, A.get(0)*B.get(1)-A.get(1)*B.get(0));
	}
}
