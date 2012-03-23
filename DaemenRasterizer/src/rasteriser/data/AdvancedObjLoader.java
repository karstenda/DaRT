package rasteriser.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rasteriser.data.Material;
import rasteriser.data.Mesh;

import math.Vector;

public class AdvancedObjLoader {

	private String filename;
	private String foldername;
	private String textureFoldername;
	private Map<String, Material> loadedMaterials = new HashMap<String, Material>();
	private Material constructingMaterial;
	private int usedMaterial;

	private List<double[]> vertices = new ArrayList<double[]>();
	private List<double[]> verticeNormals = new ArrayList<double[]>();
	private List<double[]> textureCoordinates = new ArrayList<double[]>();
	private List<int[]> trianglesTextures = new ArrayList<int[]>();
	private List<int[]> trianglesVertices = new ArrayList<int[]>();
	private List<int[]> trianglesNormals = new ArrayList<int[]>();
	private List<Integer> trianglesMaterials = new ArrayList<Integer>();
	private List<Material> materials = new ArrayList<Material>();

	private double[] xBounds = new double[] { Double.MAX_VALUE,
			-Double.MAX_VALUE };
	private double[] yBounds = new double[] { Double.MAX_VALUE,
			-Double.MAX_VALUE };
	private double[] zBounds = new double[] { Double.MAX_VALUE,
			-Double.MAX_VALUE };

	public AdvancedObjLoader(String foldername, String filename) {
		this(foldername, filename, foldername);
	}

	public AdvancedObjLoader(String foldername, String filename,
			String textureFoldername) {
		this.filename = filename;
		this.foldername = foldername;
		this.textureFoldername = textureFoldername;
		Material material = new Material();
		this.materials.add(material);
		this.usedMaterial = 0;

	}

	public Mesh load() throws IOException {
		startParsingObj(foldername + "/" + filename);

		System.out.println("Parsed " + filename + " to single Mesh");
		System.out.println("    -> found " + trianglesVertices.size()
				+ " triangles.");
		System.out.println("    -> found " + vertices.size() + " vertices.");
		System.out.println("    -> found " + verticeNormals.size()
				+ " vertice normals.");
		System.out.println("    -> found " + textureCoordinates.size()
				+ " texture coordinates.");
		System.out.println("    -> found " + materials.size() + " materials.");
		System.out.println("Bounding box is from (" + xBounds[0] + ", "
				+ yBounds[0] + ", " + zBounds[0] + ") " + "to (" + xBounds[1]
				+ ", " + yBounds[1] + ", " + zBounds[1] + ")");

		Vector vector1 = new Vector(new double[] { xBounds[0], yBounds[0],
				zBounds[0] });
		Vector vector2 = new Vector(new double[] { xBounds[1], yBounds[1],
				zBounds[1] });
		Vector[] boundingBox = new Vector[] { vector1, vector2 };

		return new Mesh(vertices, verticeNormals, textureCoordinates,
				trianglesTextures, trianglesVertices, trianglesNormals,
				trianglesMaterials, materials, boundingBox);
	}

	private void startParsingObj(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = in.readLine()) != null) {
			String[] parts = line.trim().split("(\\s+)");
			handleObjLine(parts);
		}
	}

	private void startParsingMtl(String filename) throws IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (IOException e) {
			System.out.println("COULD NOT FIND MTL FILE: " + filename
					+ ", USING STANDARD COLOR!");
			return;
		}
		try {
			String line;
			while ((line = in.readLine()) != null) {
				String[] parts = line.trim().split("(\\s+)");
				handleMtlLine(parts);
			}
		} catch (IOException e) {
			System.out.println("ERROR LOADING MATERIAL: " + e.getMessage());
		}

	}

	private void handleObjLine(String[] parts) throws IOException {
		if (parts[0].equals("v"))
			parseVertice(parts);
		else if (parts[0].equals("vt"))
			parseTextureCoordinate(parts);
		else if (parts[0].equals("vn"))
			parsVerticeNormal(parts);
		else if (parts[0].equals("f"))
			parseFace(parts);
		else if (parts[0].equals("usemtl"))
			useNewMaterial(parts);
		else if (parts[0].equals("mtllib"))
			loadNewLib(parts);
	}

	private void handleMtlLine(String[] parts) throws IOException {
		if (parts[0].equals("newmtl"))
			parseMaterial(parts);
		else if (parts[0].equals("Ka"))
			parseAmbientColor(parts);
		else if (parts[0].equals("Kd"))
			parseDiffuseColor(parts);
	}

	private void parseMaterial(String[] parts) {
		this.constructingMaterial = new Material();
		loadedMaterials.put(parts[1], this.constructingMaterial);
	}

	private void parseDiffuseColor(String[] parts) {
		double[] color = new double[3];
		color[0] = Double.parseDouble(parts[1]);
		color[1] = Double.parseDouble(parts[2]);
		color[2] = Double.parseDouble(parts[3]);
		this.constructingMaterial.setDiffuseColor(color);
	}

	private void parseAmbientColor(String[] parts) {
		double[] color = new double[3];
		color[0] = Double.parseDouble(parts[1]);
		color[1] = Double.parseDouble(parts[2]);
		color[2] = Double.parseDouble(parts[3]);
		this.constructingMaterial.setAmbientColor(color);
	}

	private void loadNewLib(String[] parts) throws IOException {
		startParsingMtl(foldername + "/" + parts[1]);
	}

	private void useNewMaterial(String[] parts) {
		Material material = loadedMaterials.get(parts[1]);
		if (material == null) {
			usedMaterial = 0;
		} else {
			int index = materials.indexOf(material);
			if (index == -1) {
				index = materials.size();
				materials.add(material);
			}
			usedMaterial = index;
		}
	}

	private void parseVertice(String[] parts) {
		double[] vertice = new double[3];
		vertice[0] = Double.parseDouble(parts[1]);
		vertice[1] = Double.parseDouble(parts[2]);
		vertice[2] = Double.parseDouble(parts[3]);
		vertices.add(vertice);

		// find the bounding box
		if (vertice[0] < xBounds[0])
			xBounds[0] = vertice[0];
		else if (vertice[0] > xBounds[1])
			xBounds[1] = vertice[0];

		if (vertice[1] < yBounds[0])
			yBounds[0] = vertice[1];
		else if (vertice[1] > yBounds[1])
			yBounds[1] = vertice[1];

		if (vertice[2] < zBounds[0])
			zBounds[0] = vertice[2];
		else if (vertice[2] > zBounds[1])
			zBounds[1] = vertice[2];
	}

	private void parseFace(String[] parts) {

		for (int i = 0; i < parts.length - 3; i++) {

			int[] triangleVertices = new int[3];
			int[] triangleTextures = new int[3];
			int[] triangleNormals = new int[3];

			String[] partsVertice = parts[1].split("/");
			triangleVertices[0] = Integer.parseInt(partsVertice[0]) - 1;
			try {
				triangleTextures[0] = Integer.parseInt(partsVertice[1]) - 1;
			} catch (Exception e) {
				triangleTextures[0] = 0;
			}

			try {
				triangleNormals[0] = Integer.parseInt(partsVertice[2]) - 1;
			} catch (Exception e) {
				triangleNormals[0] = 0;
			}

			partsVertice = parts[2 + i].split("/");
			triangleVertices[1] = Integer.parseInt(partsVertice[0]) - 1;
			try {
				triangleTextures[1] = Integer.parseInt(partsVertice[1]) - 1;
			} catch (Exception e) {
				triangleTextures[1] = 0;
			}
			try {
				triangleNormals[1] = Integer.parseInt(partsVertice[2]) - 1;
			} catch (Exception e) {
				triangleNormals[1] = 0;
			}

			partsVertice = parts[3 + i].split("/");
			triangleVertices[2] = Integer.parseInt(partsVertice[0]) - 1;
			try {
				triangleTextures[2] = Integer.parseInt(partsVertice[1]) - 1;
			} catch (Exception e) {
				triangleTextures[2] = 0;
			}
			try {
				triangleNormals[2] = Integer.parseInt(partsVertice[2]) - 1;
			} catch (Exception e) {
				triangleNormals[2] = 0;
			}

			this.trianglesMaterials.add(usedMaterial);
			this.trianglesVertices.add(triangleVertices);
			this.trianglesTextures.add(triangleTextures);
			this.trianglesNormals.add(triangleNormals);
		}
	}

	private void parsVerticeNormal(String[] parts) {
		double[] normal = new double[3];
		normal[0] = Double.parseDouble(parts[1]);
		normal[1] = Double.parseDouble(parts[2]);
		normal[2] = Double.parseDouble(parts[3]);
		verticeNormals.add(normal);
	}

	private void parseTextureCoordinate(String[] parts) {
		double[] textureCoordinate = new double[2];
		textureCoordinate[0] = Double.parseDouble(parts[1]);
		textureCoordinate[1] = Double.parseDouble(parts[2]);
		textureCoordinates.add(textureCoordinate);
	}
}
