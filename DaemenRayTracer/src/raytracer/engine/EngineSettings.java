package raytracer.engine;

import raytracer.data.scene.Scene;
import raytracer.data.scene.SceneLibrary;

public abstract class EngineSettings {
	
	
	public static final int N_CONCURRENT_THREADS = 4;
	public static final double DELTA_MACHINE = 0.000001;
	
	public static Scene SCENE;
	
	
	static {
		try {
			SCENE = SceneLibrary.getWallEScene();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	
	public static final int WIDTH_RESOLUTION = 1800;
	public static final int HEIGHT_RESOLUTION = 1800;
	
	public static final int ANTI_ALIASINGFACTOR = 1000;
	public static final int REFLECCTION_LEVEL = 6;

	
}
