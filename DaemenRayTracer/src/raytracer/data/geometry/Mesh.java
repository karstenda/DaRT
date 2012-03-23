package raytracer.data.geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math.CommonOps;
import math.Matrix;
import math.Vector;


import raytracer.acceleration.hierarchicalbb.BoundingBoxDimensions;
import raytracer.data.geometry.Mesh.Triangle;
import raytracer.data.render.Material;
import raytracer.engine.Ray;
import raytracer.engine.RayTraceMath;

/**
 * A wireframe made of triangles and vertices to describe the form of a Model.
 * 
 * <p>
 * <b>REMARK:</b> I have chosen to implement a "triangle neighbor mesh"
 * structure. The "winged mesh" structure however is more widely used in
 * modeling programs due to its great flexibility in dynamically changing the
 * mesh geometry (Split and merge operations can be done much more efficient.).
 * But since I don't plan to edit mesh structures in this project, I don't
 * benefit from this. They also have large large storage requirements and they
 * are more complex. Hence my choice for the "triangle neighbor mesh".
 * </p>
 * 
 * <p>
 * <b>REMARK:</b> I have chosen to work with arrays instead of ArrayLists,
 * because there is (however small) difference in performance.
 * </p>
 * 
 * @author Karsten Daemen
 */
public class Mesh implements Iterable<Triangle> {

	// A 3*n matrix, each column represents the x,y,z cordinates of n vertices.
	// A 3*m matrix, each column represents the x,y,z cordinates of m normals of
	// triangles.
	private double[][] vertices;
	private double[][] verticeNormals;
	private double[][] textureCoordinates;
	private int[][] trianglesTextures;
	private int[][] trianglesVertices;
	private int[][] trianglesNormals;
	private List<Integer>[] trianglesNeighbors;
	private List<Integer> trianglesMaterials;
	private Material[] materials;
	private Vector[] boundingBox;
	
	
	/**
	 * <p>
	 * Creates a new mesh made of vertices and triangles.
	 * </p>
	 */
	public Mesh(List<double[]> vertices, List<double[]> verticeNormals,
			List<double[]> textureCoordinates, List<int[]> trianglesTextures,
			List<int[]> trianglesVertices, List<int[]> trianglesNormals,
			List<Integer> trianglesMaterials, List<Material> materials, Vector[] boundingBox) {

		this.vertices = vertices.toArray(new double[vertices.size()][]);
		this.verticeNormals = verticeNormals.toArray(new double[verticeNormals
				.size()][]);
		this.textureCoordinates = textureCoordinates
				.toArray(new double[textureCoordinates.size()][]);
		this.trianglesTextures = trianglesTextures
				.toArray(new int[trianglesTextures.size()][]);
		this.trianglesVertices = trianglesVertices
				.toArray(new int[trianglesVertices.size()][]);
		this.trianglesNormals = trianglesNormals
				.toArray(new int[trianglesNormals.size()][]);
		this.trianglesMaterials = trianglesMaterials;
		this.materials = materials.toArray(new Material[materials.size()]);
		this.boundingBox = boundingBox;

		ArrayList<Triangle>[] adjacentTriangles = findAllAdjacentTriangles();

		if (verticeNormals.size() == 0) {
			System.out.println("-> No normals found, calculating themselve!");
			calculateVerticeNormals(adjacentTriangles);
		}
		findAdjacentNeighbors(adjacentTriangles);
	}
	
	
	/**
	 * <p>
	 * Starting from the set of adjacent Triangles for each vertex this method
	 * will search for all the direct neighbors of a triangle and will save them
	 * in trianglesNeighbors.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public void findStrictNeighbors(ArrayList<Triangle>[] adjacentTriangles) {

		List<Integer>[] memNeighbors = new ArrayList[trianglesVertices.length];

		for (ArrayList<Triangle> triangles : adjacentTriangles) {
			for (Triangle triangle1 : triangles) {
				for (Triangle triangle2 : triangles) {

					List<Integer> l1 = triangle1.getVerticesIndexList();
					List<Integer> l2 = triangle2.getVerticesIndexList();
					l1.retainAll(l2);

					if (!triangle1.equals(triangle2) && l1.size() == 2) {
						if (memNeighbors[triangle1.triangleIndex] == null) {
							ArrayList<Integer> list = new ArrayList<Integer>(3);
							list.add(triangle2.triangleIndex);
							memNeighbors[triangle1.triangleIndex] = list;
						} else {
							memNeighbors[triangle1.triangleIndex]
									.add(triangle2.triangleIndex);
						}
						if (memNeighbors[triangle2.triangleIndex] == null) {
							ArrayList<Integer> list = new ArrayList<Integer>(3);
							list.add(triangle1.triangleIndex);
							memNeighbors[triangle2.triangleIndex] = list;
						} else {
							memNeighbors[triangle2.triangleIndex]
									.add(triangle1.triangleIndex);
						}
					}
				}
			}
		}
		this.trianglesNeighbors = memNeighbors;
	}

	@SuppressWarnings("unchecked")
	private void findAdjacentNeighbors(ArrayList<Triangle>[] adjacentTriangles) {

		this.trianglesNeighbors = new ArrayList[trianglesVertices.length];

		for (ArrayList<Triangle> triangles : adjacentTriangles) {
			for (Triangle triangle : triangles) {
				List<Integer> neighbors = trianglesNeighbors[triangle
						.getIndex()];
				if (neighbors == null) {
					neighbors = new ArrayList<Integer>();
					trianglesNeighbors[triangle.getIndex()] = neighbors;
				}
				for (Triangle otherTriangle : triangles) {
					if (!neighbors.contains(otherTriangle.getIndex())
							&& otherTriangle.getIndex() != triangle.getIndex())
						neighbors.add(otherTriangle.getIndex());
				}
			}
		}
	}

	/**
	 * <p>
	 * This method will search for each vertice the the adjacent triangles and
	 * returns them in an array of Lists. The index of the array represents the
	 * vertice number.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Triangle>[] findAllAdjacentTriangles() {

		// Search all adjacentTriangles
		ArrayList<Triangle>[] adjacentTriangles = new ArrayList[vertices.length];
		Iterator<Triangle> iterator = iterator();
		while (iterator.hasNext()) {
			Triangle triangle = iterator.next();
			for (int index : triangle.getVerticesIndexes()) {
				if (adjacentTriangles[index] == null) {
					adjacentTriangles[index] = new ArrayList<Mesh.Triangle>();
					adjacentTriangles[index].add(triangle);
				} else if (!adjacentTriangles[index].contains(triangle)) {
					adjacentTriangles[index].add(triangle);
				}
			}
		}
		return adjacentTriangles;
	}

	/**
	 * <p>
	 * This method will recalculate all the vertice normals, this is necessary
	 * because some .obj don't give them or give faulty normals.
	 * </p>
	 */
	public void calculateVerticeNormals(ArrayList<Triangle>[] adjacentTriangles) {
		
		this.verticeNormals = new double[this.vertices.length][];
		// Calculate the normals
		for (int i = 0; i < vertices.length; i++) {
			Vector sum = new Vector(3);
			for (Triangle triangle : adjacentTriangles[i]) {
				CommonOps.unCheckedAdd(sum, triangle.calculateNormal(), sum);

			}

			sum.normalise();

			verticeNormals[i] = sum.getData();
		}
	}

	/**
	 * <p>
	 * Recalculates the boundingbox of this Mesh
	 * </p>
	 */
	public void calcBoundingbox () {
		
		double[] xBounds = new double[] { Double.MAX_VALUE,	-Double.MAX_VALUE };
		double[] yBounds = new double[] { Double.MAX_VALUE,	-Double.MAX_VALUE };
		double[] zBounds = new double[] { Double.MAX_VALUE,	-Double.MAX_VALUE };
		
		for (int i = 0; i<vertices.length; i++) {
			// find the bounding box
			if (vertices[i][0] < xBounds[0])
				xBounds[0] = vertices[i][0];
			else if (vertices[i][0] > xBounds[1])
				xBounds[1] = vertices[i][0];

			if (vertices[i][1] < yBounds[0])
				yBounds[0] = vertices[i][1];
			else if (vertices[i][1] > yBounds[1])
				yBounds[1] = vertices[i][1];

			if (vertices[i][2] < zBounds[0])
				zBounds[0] = vertices[i][2];
			else if (vertices[i][2] > zBounds[1])
				zBounds[1] = vertices[i][2];
		}
		
		boundingBox[0] = new Vector(new double[] {xBounds[0],yBounds[0],zBounds[0]});
		boundingBox[1] = new Vector(new double[] {xBounds[1],yBounds[1],zBounds[1]});
	}
	
	/**
	 * <p>
	 * Returns the bounding box of this Mesh. The first vector is the smallest and
	 * the second is the largest
	 * </p>
	 */
	public Vector[] getBoundingBox() {
		return boundingBox;
	}
	
	/**
	 * <p>
	 * Returns the triangle on the mesh with index triangleIndex.
	 * </p>
	 */
	public Triangle getTriangle(int triangleIndex) {
		return new Triangle(triangleIndex);
	}

	/**
	 * <p>
	 * Applies the transformation matrix T to the vertices of the Mesh. Normals
	 * of the vertices will be accordingly adapted.
	 * </p>
	 */
	public void transform(Matrix T) {
		
		// Adjust the vertice coordinates
		Matrix A = new Matrix(vertices);
		A.addHomogeneousCoordinates();
		Matrix mem = new Matrix(A.getnRows(), A.getnCols());
		CommonOps.unCheckedMult(T, A, mem);
		mem.removeHomogeneousCoordinates();
		this.vertices = mem.getData();
		
		// Find the new boundingbox
		calcBoundingbox();
		
		// adjust the normals
		// TODO
	}

	/**
	 * <p>
	 * A single triangle on a Mesh.
	 * </p>
	 * 
	 * <p>
	 * <b>REMARK:</b> I made Triangle an innerclass of Mesh because:<br>
	 * <ol>
	 * <li>A Triangle can't exist without a Mesh.</li>
	 * <li>A Triangle can now behave as a normal object to the outside but it's
	 * implementation remains a set of efficient operations on the same matrix.
	 * </ol>
	 * </p>
	 * 
	 * @author Karsten Daemen
	 */
	public class Triangle implements Geometry {

		private final int triangleIndex;

		/**
		 * <p>
		 * Create a reference to a triangle on the mesh with index
		 * triangleIndex.
		 * </p>
		 */
		private Triangle(int triangleIndex) {
			this.triangleIndex = triangleIndex;
		}

		/**
		 * <p>
		 * Returns the mesh on which this triangle is part of.
		 * </p>
		 */
		public Mesh getMesh() {
			return Mesh.this;
		}

		/**
		 * <p>
		 * Returns the index of this triangle.
		 * </p>
		 */
		public int getIndex() {
			return triangleIndex;
		}

		@Override
		public Material getMaterial() {
			return materials[trianglesMaterials.get(triangleIndex)];
		}

		/**
		 * <p>
		 * Returns the 3 indexes of the vertices on the mesh that make up this
		 * triangle.
		 * </p>
		 */
		public int[] getVerticesIndexes() {
			return trianglesVertices[triangleIndex];
		}

		/**
		 * <p>
		 * Returns the 3 indexes of the vertices on the mesh that make up this
		 * triangle.
		 * </p>
		 */
		public List<Integer> getVerticesIndexList() {
			int[] indexes = getVerticesIndexes();
			List<Integer> intList = new ArrayList<Integer>(indexes.length);
			for (int index = 0; index < indexes.length; index++) {
				intList.add(indexes[index]);
			}
			return intList;
		}

		/**
		 * <p>
		 * Returns the Texture Coordinates for each vertex of the triangle.
		 * </p>
		 */
		public Vector[] getTextureCo() {
			int[] textureCoordinatesIndexes = trianglesTextures[this.triangleIndex];
			Vector[] textureCoordinatesTriangle = new Vector[3];
			try {
				textureCoordinatesTriangle[0] = new Vector(
						textureCoordinates[textureCoordinatesIndexes[0]]);
				textureCoordinatesTriangle[1] = new Vector(
						textureCoordinates[textureCoordinatesIndexes[1]]);
				textureCoordinatesTriangle[2] = new Vector(
						textureCoordinates[textureCoordinatesIndexes[2]]);
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
			return textureCoordinatesTriangle;
		}

		/**
		 * <p>
		 * Returns the indexes of the triangles who have two common vertices
		 * with this triangle on the mesh.
		 * </p>
		 */
		public List<Integer> getNeighBorsIndexes() {
			return trianglesNeighbors[this.triangleIndex];
		}

		/**
		 * <p>
		 * Returns the Triangles who have two common vertices with this triangle
		 * on the mesh.
		 * </p>
		 */
		public List<Triangle> getNeighBors() {
			List<Integer> neighborsIndexes = getNeighBorsIndexes();
			List<Triangle> triangles = new ArrayList<Triangle>();
			for (Integer i : neighborsIndexes)
				triangles.add(new Triangle(i));
			return triangles;
		}

		/**
		 * <p>
		 * Returns the coordinates of the vertices on the mesh that make up this
		 * triangle separately in an array of 3 vectors. The separate vectors
		 * have the same structure as vectors returned by
		 * Mesh.Vertice.asVector().
		 * </p>
		 */
		public Vector[] getVerticesAsVectors() {
			Vector[] vectors = new Vector[3];
			int[] verticeIndexes = this.getVerticesIndexes();
			for (int i = 0; i < 3; i++) {
				vectors[i] = new Vector(vertices[verticeIndexes[i]]);
			}
			return vectors;
		}

		/**
		 * <p>
		 * Writes the coordinates of the number i vertice of this triangle as a
		 * 3x1 vector in matrix starting from the position (startRow, startCol).
		 * </p>
		 * <ul>
		 * <li>The element in (startRow, startCol) is the x coordinate of the
		 * vertice.</li>
		 * <li>The element in (startRow+1, startCol) is the y coordinate of the
		 * vertice.</li>
		 * <li>The element in (startRow+2, startCol) is the z coordinate of the
		 * vertice.</li>
		 * </ul>
		 * <p>
		 * 
		 */
		public Vector getVerticeAsVector(int i) {
			int[] verticeIndexes = this.getVerticesIndexes();
			return new Vector(vertices[verticeIndexes[i]]);
		}

		/**
		 * <p>
		 * Returns the normal on this triangle as 3x1 vector (not normalised).
		 * </p>
		 * <ul>
		 * <li>The element in (startRow, 0) is the x coordinate of the normal.</li>
		 * <li>The element in (startRow+1, 0) is the y coordinate of the normal.
		 * </li>
		 * <li>The element in (startRow+2, 0) is the z coordinate of the normal.
		 * </li>
		 * </ul>
		 */
		public Vector calculateNormal() {
			Vector[] vertices = getVerticesAsVectors();

			CommonOps.unCheckedSub(vertices[1], vertices[0], vertices[1]);
			CommonOps.unCheckedSub(vertices[2], vertices[0], vertices[2]);

			vertices[0].set(0, vertices[1].get(1) * vertices[2].get(2)
					- vertices[1].get(2) * vertices[2].get(1));
			vertices[0].set(1, vertices[1].get(2) * vertices[2].get(0)
					- vertices[1].get(0) * vertices[2].get(2));
			vertices[0].set(2, vertices[1].get(0) * vertices[2].get(1)
					- vertices[1].get(1) * vertices[2].get(0));

			vertices[0].normalise();

			return vertices[0];
		}

		/**
		 * <p>
		 * Checks wether a ray will intersect with this triangle.
		 * It will return the intersectionpoint as solution.
		 * </p>
		 */
		@Override
		public IntersectionSolution intersects(Ray ray) {
			return RayTraceMath.intersects(ray, this);
		}
		
		/**
		 * <p>
		 * Returns wether this triangle is a neighbor of geometry.
		 * </p>
		 */
		@Override
		public boolean isNeighbor(Geometry geometry) {
			try {
				Triangle neighbor = (Triangle) geometry;
				return neighbor.getMesh().equals(Mesh.this) && neighbor.getNeighBorsIndexes().contains(triangleIndex);
			} catch (ClassCastException e) {
				return false;
			}
		}

		/**
		 * Returns the normals on the vertices of this triangle.
		 * 
		 * @return
		 */
		public Vector[] getNormals() {
			Vector[] normals = new Vector[3];
			int i = 0;
			for (int index : trianglesNormals[triangleIndex]) {
				normals[i] = new Vector(verticeNormals[index]);
				i++;
			}
			return normals;
		}

		/**
		 * <p>
		 * Returns the dimensions of the boundingbox of this triangle.
		 * </p>
		 */
		@Override
		public BoundingBoxDimensions getBoundingBoxDimensions() {
			
			Vector[] vertices = getVerticesAsVectors();
			
			double[] smallPoint = new double[] { Double.MAX_VALUE,	Double.MAX_VALUE, Double.MAX_VALUE };
			double[] bigPoint = new double[] { -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE };
			
			for (Vector vertice: vertices) {
				if (vertice.get(0) < smallPoint[0])
					smallPoint[0] = vertice.get(0);
				if (vertice.get(1) < smallPoint[1])
					smallPoint[1] = vertice.get(1);
				if (vertice.get(2) < smallPoint[2])
					smallPoint[2] = vertice.get(2);
				if (vertice.get(0) > bigPoint[0])
					bigPoint[0] = vertice.get(0);
				if (vertice.get(1) > bigPoint[1])
					bigPoint[1] = vertice.get(1);
				if (vertice.get(2) > bigPoint[2])
					bigPoint[2] = vertice.get(2);
			}
			
			return new BoundingBoxDimensions(smallPoint, bigPoint);
		}

		@Override
		public Vector getCenter() {
			Vector sum = new Vector(3);
			Vector[] vertices = getVerticesAsVectors();
			
			CommonOps.add(vertices[0], sum, sum);
			CommonOps.add(vertices[1], sum, sum);
			CommonOps.add(vertices[2], sum, sum);
			sum.divide(3);
			
			return sum;
		}
		
	}

	/**
	 * <p>
	 * Returns a new instance of the same Mesh
	 * </p>
	 */
	@Override
	public Mesh clone() {
		return new Mesh(vertices, verticeNormals, textureCoordinates,
				trianglesTextures, trianglesVertices, trianglesNormals,
				trianglesNeighbors, trianglesMaterials, materials, boundingBox);
	}

	/**
	 * <p>
	 * Private constructor for the clone() method.
	 * </p>
	 */
	private Mesh(double[][] vertices, double[][] verticeNormals,
			double[][] textureCoordinates, int[][] trianglesTextures,
			int[][] trianglesVertices, int[][] trianglesNormals,
			List<Integer>[] trianglesNeighbors,
			List<Integer> trianglesMaterials, Material[] materials, Vector[] Boundingbox) {

		this.vertices = vertices;
		this.verticeNormals = verticeNormals;
		this.textureCoordinates = textureCoordinates;
		this.trianglesTextures = trianglesTextures;
		this.trianglesVertices = trianglesVertices;
		this.trianglesNormals = trianglesNormals;
		this.trianglesNeighbors = trianglesNeighbors;
		this.trianglesMaterials = trianglesMaterials;
		this.materials = materials;
		this.boundingBox = Boundingbox;

	}

	/**
	 * <p>
	 * Returns an iterator object who iterates over all the triangles on this
	 * mesh. This iterator guarantees always to iterate in the same order.
	 * </p>
	 */
	@Override
	public Iterator<Triangle> iterator() {
		return new Iterator<Mesh.Triangle>() {

			int i = 0;

			@Override
			public boolean hasNext() {
				return trianglesVertices.length > i;
			}

			@Override
			public Triangle next() {
				Triangle triangle = new Triangle(i);
				i++;
				return triangle;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	
	public void invertNormals() {
		
		for(double[] verticeNormal: verticeNormals) {
			verticeNormal[0] = -verticeNormal[0];
			verticeNormal[1] = -verticeNormal[1];
			verticeNormal[2] = -verticeNormal[2];
		}
		
	}
	
	public void setMaterial(Material material) {
		for (int i=0; i < materials.length; i++) {
			materials[i] = material;
		}
	}
	
	public Material[] getMaterials() {
		return materials;
	}


	public void setDefaultMaterial(Material defMat) {
		materials[0] = defMat;
	}
}