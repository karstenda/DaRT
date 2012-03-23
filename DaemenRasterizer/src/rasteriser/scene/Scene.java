package rasteriser.scene;


import java.util.List;



import rasteriser.engine.LightSource;
import rasteriser.engine.RasterisationCamera;

public class Scene {
	
	
	private final String name;
	private final SceneObject rootObject;
	private final List<LightSource> lightSources;
	private final RasterisationCamera camera;
	

	public Scene (String name, RasterisationCamera camera, SceneObject rootObject, List<LightSource> lightSources) {
		this.camera = camera;
		this.name = name;
		this.rootObject = rootObject;
		this.lightSources = lightSources;
	}

	public String getName() {
		return name;
	}

	public SceneObject getRootObject() {
		return rootObject;
	}

	public List<LightSource> getLightSources() {
		return lightSources;
	}

	public RasterisationCamera getCamera() {
		return camera;
	}
}
