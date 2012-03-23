package raytracer.data.scene;

import java.util.List;

import raytracer.data.geometry.Geometry;
import raytracer.data.render.Environment;
import raytracer.data.render.LightSource;
import raytracer.engine.RaytracingCamera;

public class Scene {
	
	
	private final String name;
	private final SceneObject<? extends Geometry> rootObject;
	private final List<LightSource> lightSources;
	private final RaytracingCamera camera;
	private Environment environment;
	
	
	public Scene (String name, RaytracingCamera camera, SceneObject<? extends Geometry> rootObject, List<LightSource> lightSources,Environment environment) {
		this(name,camera,rootObject,lightSources);
		this.environment = environment;
	}
	
	public Scene (String name, RaytracingCamera camera, SceneObject<? extends Geometry> rootObject, List<LightSource> lightSources) {
		this.camera = camera;
		this.name = name;
		this.rootObject = rootObject;
		this.lightSources = lightSources;
	}

	public String getName() {
		return name;
	}

	public SceneObject<? extends Geometry> getRootObject() {
		return rootObject;
	}

	public List<LightSource> getLightSources() {
		return lightSources;
	}

	public RaytracingCamera getCamera() {
		return camera;
	}

	public Environment getEnvironment() {
		return environment;
	}
	
}
