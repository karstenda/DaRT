package rasteriser.scene;

import java.io.IOException;
import java.util.ArrayList;

import math.Transformation;
import math.Vector;
import rasteriser.data.AdvancedObjLoader;
import rasteriser.data.Mesh;
import rasteriser.engine.LightSource;
import rasteriser.engine.RasterisationCamera;

// final class zodat deze klasse nooit kan worden overgeërft.
public final class SceneLibrary {

	// Privé constructor zodat deze klasse nooit gemaakt kan worden.
	private SceneLibrary () {};
	
	public static Scene getSphereScene() throws IOException {
	
	String foldername = "../DaemenRayTracer/models";
	String modelPath1 = "tablecloth.obj";
	
	AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,modelPath1,"test");
	Mesh mesh1 = loader1.load();
	SceneCoreMeshObject venus = new SceneCoreMeshObject(mesh1);

	RasterisationCamera camera = new RasterisationCamera(
	/* pos */new Vector(new double[] {15, 15, 15}),
	/* dir */new Vector(new double[] {-1, -1, -1}),
	/* up */new Vector(new double[] {0, 1, 0}), 45);
	LightSource lightSource1 = new LightSource("lightSource1",
	/* pos */new Vector(new double[] {15, 15, 15}),
	/* col */new double[] { 1, 1, 1 });

	ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
	lightSources.add(lightSource1);
	
	return new Scene("sphere",camera,venus,lightSources);
}
	
	
	
}
