package rasteriser.engine;

import java.io.IOException;

import rasteriser.scene.Scene;
import rasteriser.scene.SceneLibrary;

public abstract class EngineSettings {

	public static final double DELTA_MACHINE = 0.000001;
	
	public static final int WIDTH_RESOLUTION = 500;
	public static final int HEIGHT_RESOLUTION = 500;
	
	public static Scene SCENE;
	static {
		try {
			SCENE = SceneLibrary.getSphereScene();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	 
	
}
