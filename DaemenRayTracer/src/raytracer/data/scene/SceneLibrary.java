package raytracer.data.scene;

import java.io.IOException;
import java.util.ArrayList;

import math.Transformation;
import math.Vector;
import raytracer.data.geometry.Mesh;
import raytracer.data.geometry.Sphere;
import raytracer.data.loader.AdvancedObjLoader;
import raytracer.data.render.CompositeEnvironment;
import raytracer.data.render.Environment;
import raytracer.data.render.LightSource;
import raytracer.data.render.Material;
import raytracer.data.render.MeshEnvironment;
import raytracer.data.render.SoftLightSource;
import raytracer.data.render.SphericalEnvironment;
import raytracer.engine.EngineSettings;
import raytracer.engine.RaytracingCamera;

public final class SceneLibrary {

	private SceneLibrary () {}

	
	
	@SuppressWarnings("unchecked")
	public static Scene getTestScene() {
		try {
			
			Material mat = new Material();
			mat.setReflectionRatio(0.8);
			mat.setMirror(true);
//			mat.setGlossyFactor(0.02);
			
			Sphere sphere = new Sphere(new Vector(new double[] {0,0,0}), 1, mat);
			SceneCoreSphereObject scene = new SceneCoreSphereObject(sphere);
			
			
			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { 2, 1, 2}),
			/* dir */new Vector(new double[] { -1, -0.5, -1}),
			/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			LightSource lightSource1 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { 2, 2, 2}),
			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);

			Environment env = new SphericalEnvironment(new Vector(3), 10, "env/env_theathreoo.jpg");
			
			
			return new Scene("test", camera, scene, lightSources, env);

		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}
	
	
	
	public static Scene getLogoScene() {
		try {
			String modelPath1 = "scene_bg.obj";
			String foldername = "models/logo";
			String textureFolderName = "Texture";
			
			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,modelPath1, textureFolderName);
			Mesh bgMesh = loader1.load();
//			bgMesh.transform(Transformation.getScalingTransformation(60));
//			bgMesh.transform(Transformation.getTranslationTransformation(new Vector(new double[] {-50,70,-75})));
			SceneCoreMeshObject bg = new SceneCoreMeshObject(bgMesh);
			bg.applyTransformation(Transformation.getTranslationTransformation(new Vector(new double[] {0,-1,0})));
			bg.applyTransformation(Transformation.getScalingTransformation(new Vector(new double[] {200,1,200})));
			
			Material material1 = new Material();
			material1.setFileNameTextureMap(foldername+"/"+textureFolderName+"/DaRT.jpg");
			material1.setFileNameBumpMap(foldername+"/"+textureFolderName+"/DaRT_bump.jpg");
			material1.setAmbientColor(new double[] {0.5,0.5,0.5});
			Sphere sphereO1 = new Sphere(new Vector(new double[] {0,0.0707,0}), 1, material1);
			SceneCoreSphereObject sphere1 = new SceneCoreSphereObject(sphereO1);
			
			Material material2 = new Material();
			material2.setFileNameTextureMap(foldername+"/"+textureFolderName+"/DaRT.jpg");
			material2.setFileNameBumpMap(foldername+"/"+textureFolderName+"/DaRT_bump.jpg");
			material2.setDielectric(true);
			material2.setTransparancy(0.90);
			material2.setAmbientColor(new double[] {0.5,0.5,0.5});
			Sphere sphereO2 = new Sphere(new Vector(new double[] {2.5,0.0707,2.5}), 1, material2);
			SceneCoreSphereObject sphere2 = new SceneCoreSphereObject(sphereO2);
			
			Material material3 = new Material();
			material3.setFileNameTextureMap(foldername+"/"+textureFolderName+"/DaRT.jpg");
			material3.setFileNameBumpMap(foldername+"/"+textureFolderName+"/DaRT_bump.jpg");
			material3.setMirror(true);
			material3.setReflectionRatio(0.90);
			material3.setAmbientColor(new double[] {0.5,0.5,0.5});
			Sphere sphereO3 = new Sphere(new Vector(new double[] {2.5,0.0707,-2.5}), 1, material3);
			SceneCoreSphereObject sphere3 = new SceneCoreSphereObject(sphereO3);
			
			@SuppressWarnings("unchecked")
			SceneCompositeObject scene = new SceneCompositeObject(bg,sphere1, sphere2, sphere3);
			
			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { -6, 2, 0}),
			/* dir */new Vector(new double[] { 1, -0.3, 0}),
			/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			LightSource lightSource1 = new SoftLightSource("lightSource1",
			/* pos */new Vector(new double[] { -4, 3.5, 3}),
			0.5, /* col */new double[] { 0.8, 0.8, 0.8 });
			LightSource lightSource2 = new SoftLightSource("lightSource2",
			/* pos */new Vector(new double[] { -4, 3.5, -3}),
			0.5, /* col */new double[] { 0.8, 0.8, 0.8 });
			
			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
			lightSources.add(lightSource2);

			
			return new Scene("Logo", camera, scene, lightSources);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}
	
	
	
	
	
	
	public static Scene getBottleCollectionScene() {
		try {
			String modelPath1 = "final5.obj";
			String modelPath2 = "landscape.obj";
			String foldername = "models/BottleCollection";
			String textureFolderName = "Texture";
			
			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,modelPath2, textureFolderName);
			Mesh bgMesh = loader1.load();
			bgMesh.transform(Transformation.getScalingTransformation(60));
			bgMesh.transform(Transformation.getTranslationTransformation(new Vector(new double[] {-50,70,-75})));
			MeshEnvironment env1 = new MeshEnvironment(bgMesh);
			
			SphericalEnvironment env2 = new SphericalEnvironment(new Vector(new double[] {-31,40,0}), 150, foldername+"/loft_env.jpg");
			Environment env = new CompositeEnvironment(env1,env2);
			
			AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername,modelPath1, textureFolderName);
			Mesh sceneMesh = loader2.load();
			SceneCoreMeshObject scene = new SceneCoreMeshObject(sceneMesh);

			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { -31, 47, 70}),
			/* dir */new Vector(new double[] { -0.13, -0.16, -1}),
			/* up */new Vector(new double[] { 0, -1, 0 }), 46);
			LightSource lightSource1 = new SoftLightSource("lightSource1",
			/* pos */new Vector(new double[] { -90, 90, 60}),
			8, /* col */new double[] { 0.6, 0.6, 0.6 });
			LightSource lightSource2 = new SoftLightSource("lightSource2",
			/* pos */new Vector(new double[] { 30, 90, 60}),
			8, /* col */new double[] { 0.6, 0.6, 0.6 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
			lightSources.add(lightSource2);

			
			return new Scene("BottleCollection", camera, scene, lightSources,env);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}
	
	
	public static Scene getCylonScene() {

		try {
			String modelPath1 = "cylon.obj";
			String foldername1 = "models/cylon";
			String textureFolderName1 = "Texture";

			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,modelPath1, textureFolderName1);
			Mesh cylonMesh = loader1.load();
			SceneCoreMeshObject cylon = new SceneCoreMeshObject(cylonMesh);
			

			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { 1100, 1500, -2500 }),
			/* dir */new Vector(new double[] { 0, -0.3, -1 }),
			/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			LightSource lightSource1 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { 1100, 0, -500 }),
			/* col */new double[] { 1, 1, 1 });
//			LightSource lightSource2 = new LightSource("lightSource1",
//			/* pos */new Vector(new double[] { -10, 10, 10 }),
//			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
//			lightSources.add(lightSource2);

			Environment env = new SphericalEnvironment(new Vector(new double[]{1100, 1500, -500}), 5000, "models/env_small.png");
			
			return new Scene("Cylon", camera, cylon, lightSources, env);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	
	
	
	
	public static Scene getTeamScene() {

		try {
			String modelPath1 = "team_evil.obj";
			String foldername1 = "models/team";
			String textureFolderName1 = "Texture";

			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,
					modelPath1, textureFolderName1);
			Mesh teamMesh = loader1.load();
			SceneCoreMeshObject team = new SceneCoreMeshObject(teamMesh);

			String modelPath2 = "plane.obj";
			String foldername2 = "models";
			AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername2,
					modelPath2);
			Mesh floorMesh = loader2.load();
			SceneCoreMeshObject floor = new SceneCoreMeshObject(floorMesh);
			floor.applyTransformation(Transformation
					.getScalingTransformation(999999999));

			SceneCompositeObject comp = new SceneCompositeObject(team, floor);

			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { -10, 150, 200 }),
			/* dir */new Vector(new double[] { 0.2, -0.4, -1 }),
			/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			LightSource lightSource1 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { 70, 200, 200 }),
			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);

			return new Scene("Halo", camera, comp, lightSources);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	public static Scene getIronManScene() {

		try {
			String modelPath1 = "ironman_half.obj";
			String foldername1 = "models/iron_man";
			String textureFolderName1 = "";

			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,
					modelPath1, textureFolderName1);
			Mesh ironMesh = loader1.load();
			SceneCoreMeshObject manLeft = new SceneCoreMeshObject(ironMesh);
			manLeft.applyTransformation(Transformation
					.getTranslationTransformation(new Vector(new double[] {
							2.117425, 0, 0 })));
			SceneCoreMeshObject manRight = new SceneCoreMeshObject(ironMesh);
			manRight.applyTransformation(Transformation
					.getScalingTransformation(new Vector(new double[] { -1, 1,
							1 })));
			manLeft.applyTransformation(Transformation
					.getTranslationTransformation(new Vector(new double[] {
							-2.117425, 0, 0 })));

			SceneCompositeObject scene = new SceneCompositeObject(manRight,
					manLeft);

			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { 0, 20, 30 }),
			/* dir */new Vector(new double[] { 0, -0.5, -1 }),
			/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			LightSource lightSource1 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { 55, 55, 55 }),
			/* col */new double[] { 1, 1, 1 });
			LightSource lightSource2 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { -55, 55, 55 }),
			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
			lightSources.add(lightSource2);

			return new Scene("IronMan", camera, scene, lightSources);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	public static Scene getSoftShadowScene() throws IOException {

		String foldername1 = "models";
		String modelPath1 = "plane.obj";

		AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,
				modelPath1);
		Mesh mesh1 = loader1.load();
		SceneCoreMeshObject plane = new SceneCoreMeshObject(mesh1);
		plane.applyTransformation(Transformation
				.getScalingTransformation(999999));

		Material material = new Material();
		material.setDiffuseColor(new double[] { 1, 0.50, 0.50 });

		Sphere sphere = new Sphere(new Vector(new double[] { 0, 4, -4 }), 4,
				material);
		SceneCoreSphereObject sphereSceneObject = new SceneCoreSphereObject(
				sphere);

		SceneCompositeObject objects = new SceneCompositeObject(plane,
				sphereSceneObject);

		RaytracingCamera camera = new RaytracingCamera(
		/* pos */new Vector(new double[] { 8, 8, 8 }),
		/* dir */new Vector(new double[] { -0.6, -0.5, -1 }),
		/* up */new Vector(new double[] { 0, -1, 0 }), 60);
		SoftLightSource lightSource1 = new SoftLightSource("lightSource1",
		/* pos */new Vector(new double[] { 24, 24, 0 }), 4,
		/* col */new double[] { 1, 1, 1 });

		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);

		return new Scene("reflectscene", camera, objects, lightSources);
	}

	public static Scene getLaraCroftScene() {

		try {
			String modelPath1 = "Lara_Croft.obj";
			String foldername1 = "models/lara_croft";
			String textureFolderName1 = "Texture";

			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,
					modelPath1, textureFolderName1);
			Mesh laraMesh = loader1.load();
			SceneCoreMeshObject lara = new SceneCoreMeshObject(laraMesh);

			String modelPath2 = "plane.obj";
			String foldername2 = "models";
			String textureFolderName2 = "test";
			AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername2,
					modelPath2, textureFolderName2);
			Mesh floorMesh = loader2.load();
			SceneCoreMeshObject floor = new SceneCoreMeshObject(floorMesh);
			floor.applyTransformation(Transformation
					.getScalingTransformation(999999999));

			SceneCompositeObject scene = new SceneCompositeObject(lara, floor);

			RaytracingCamera camera = new RaytracingCamera(
			 /* pos */new Vector(new double[] {55, 55, 55}),
			 /* dir */new Vector(new double[] {-1, -0.3, -1}),
			 /* up */new Vector( new double[] {0, -1, 0}), 60);

			LightSource lightSource1 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { 55, 55, 55 }),
			/* col */new double[] { 1, 1, 1 });
			LightSource lightSource2 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { 55, 55, -55 }),
			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
			lightSources.add(lightSource2);

			return new Scene("Lara", camera, scene, lightSources);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	public static Scene getWallEScene() {

		try {
			String modelPath1 = "walle2_0.obj";
			String foldername1 = "models/wall_e_3";
			String textureFolderName = "";

			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,
					modelPath1, textureFolderName);
			Mesh mesh1 = loader1.load();
			SceneCoreMeshObject wall_e = new SceneCoreMeshObject(mesh1);
			wall_e.applyTransformation(Transformation.getTranslationTransformation(new Vector(new double[] {0,5,0})));
			Material defMat = new Material();
			defMat.setFileNameTextureMap(foldername1+"/walle2_0/Metal_Seamed.jpg");
			defMat.setFileNameBumpMap(foldername1+"/walle2_0/bump_Metal_Seamed.jpg");
			wall_e.setDefaultMaterial(defMat);
			
			
			String modelPath2 = "mars_surface2.obj";
			AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername1,modelPath2,"");
			Mesh mesh2 = loader2.load();
			SceneCoreMeshObject surface = new SceneCoreMeshObject(mesh2);
			surface.applyTransformation(Transformation.getScalingTransformation(new Vector(new double[] {20,20,20})));
			surface.applyTransformation(Transformation.getTranslationTransformation(new Vector(new double[] {96,0,-66.5})));
			
			String modelPath3 = "cyl.obj";
			AdvancedObjLoader loader3 = new AdvancedObjLoader(foldername1,modelPath3,"");
			Mesh mesh3 = loader3.load();
			mesh3.transform(Transformation.getScalingTransformation(new Vector(new double[] {100,100,100})));
			mesh3.transform(Transformation.getTranslationTransformation(new Vector(new double[] {96,-1700,-66.5})));
			Environment env = new MeshEnvironment(mesh3);
			
			
			
			
			
			@SuppressWarnings("unchecked")
			SceneCompositeObject scene = new SceneCompositeObject(surface,wall_e);

			 RaytracingCamera camera = new RaytracingCamera(
			 /* pos */new Vector(new double[] {250, 100, 110}),
			 /* dir */new Vector(new double[] {-0.8, -0.15, -1}),
			 /* up */new Vector( new double[] {0, -1, 0}), 60);
//			RaytracingCamera camera = new RaytracingCamera(
//			/* pos */new Vector(new double[] { 75, 100, 70 }),
//			/* dir */new Vector(new double[] { 0.2, -0.25, -1 }),
//			/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			
			
			
//			RaytracingCamera camera = new RaytracingCamera(
//					/* pos */new Vector(new double[] { 75, 100, 70 }),
//					/* dir */new Vector(new double[] { 0.2, -0.25, -1 }),
//					/* up */new Vector(new double[] { 0, -1, 0 }), 60);
			SoftLightSource lightSource1 = new SoftLightSource("lightSource1",
			/* pos */new Vector(new double[] { 125, 150, 90 }), 10d,
			/* col */new double[] { 1, 1, 1});
			SoftLightSource lightSource2 = new SoftLightSource("lightSource2",
			/* pos */new Vector(new double[] { 200, 150, 0 }), 10d,
			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
			lightSources.add(lightSource2);
			
			
			
			return new Scene("final_Wall_e", camera, scene, lightSources, env);

		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	public static Scene getDepthOfFieldScene() {
		try {
			String foldername = "models";
			String modelPath1 = "venus.obj";
			String modelPath2 = "plane.obj";
			String modelPath3 = "sphere.obj";

			AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,
					modelPath1);
			Mesh mesh1 = loader1.load();
			SceneCoreMeshObject cylinder = new SceneCoreMeshObject(mesh1);
			cylinder.applyTransformation(Transformation
					.getTranslationTransformation(new Vector(new double[] { 0,
							6.459324, 0 })));
			// cylinder.applyTransformation(Transformation.getScalingTransformation(5));

			AdvancedObjLoader loader3 = new AdvancedObjLoader(foldername,
					modelPath3);
			Mesh mesh3 = loader3.load();
			SceneCoreMeshObject sphere = new SceneCoreMeshObject(mesh3);
			sphere.applyTransformation(Transformation
					.getTranslationTransformation(new Vector(new double[] { -7,
							1, -3.5 })));
			sphere.applyTransformation(Transformation
					.getScalingTransformation(10));

			AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername,
					modelPath2);
			Mesh mesh2 = loader2.load();
			SceneCoreMeshObject plane = new SceneCoreMeshObject(mesh2);
			plane.applyTransformation(Transformation
					.getScalingTransformation(10000));

			SceneCompositeObject objects = new SceneCompositeObject(plane,
					cylinder, sphere);

			RaytracingCamera camera = new RaytracingCamera(
			/* pos */new Vector(new double[] { 15, 15, 15 }),
			/* dir */new Vector(new double[] { -1, -0.40, -1 }),
			/* up */new Vector(new double[] { 0, -1, 0 }), 60, 1000, 1 / 5d);

			// Math.sqrt(2*15*15)
			LightSource lightSource1 = new LightSource("lightSource1",
			/* pos */new Vector(new double[] { -3, 15, 15 }),
			/* col */new double[] { 1, 1, 1 });
			LightSource lightSource2 = new LightSource("lightSource2",
			/* pos */new Vector(new double[] { 15, 15, -3 }),
			/* col */new double[] { 1, 1, 1 });

			ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
			lightSources.add(lightSource1);
			lightSources.add(lightSource2);

			return new Scene("depth_of_field", camera, objects, lightSources);
		} catch (IOException e) {
			e.printStackTrace();
			assert (false);
			return null;
		}
	}

	public static Scene getRefractingSphereScene() throws IOException {

		String foldername1 = "models/checkboard";
		String modelPath1 = "board.obj";
		String textureFolder = "";
		String foldername2 = "models";
		String modelPath2 = "cube.obj";

		AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername1,
				modelPath1, textureFolder);
		Mesh mesh1 = loader1.load();
		SceneCoreMeshObject plane = new SceneCoreMeshObject(mesh1);
		Material boardMaterial = new Material();
		boardMaterial.setFileNameTextureMap(foldername1 + "/board.jpg", 70);
		plane.setMaterial(boardMaterial);
		plane.applyTransformation(Transformation.getScalingTransformation(600));

		Material material = new Material();
		material.setTransparancy(0.95d);
		material.setDielectric(true);
		material.setGlossyFactor(0.05);
		material.setDiffuseColor(new double[] { 0.35, 0.35, 0.7 });
		Sphere sphere = new Sphere(new Vector(new double[] { 0, 4, -4 }), 3,
				material);
		SceneCoreSphereObject sphereSceneObject = new SceneCoreSphereObject(
				sphere);

		AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername2,
				modelPath2);
		Mesh mesh2 = loader2.load();
		SceneCoreMeshObject cube = new SceneCoreMeshObject(mesh2);
		cube.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(new double[] { 0,
						1 + EngineSettings.DELTA_MACHINE, -1 })));
		cube.applyTransformation(Transformation
				.getScalingTransformation(new Vector(new double[] { 3, 3, 3 })));
		cube.setMaterial(material);

		SceneCompositeObject objects = new SceneCompositeObject(plane,
				sphereSceneObject);

		double depthOfField = Math.sqrt(8 * 8 + 12 * 12);
		RaytracingCamera camera = new RaytracingCamera(
		/* pos */new Vector(new double[] { 8, 6, 8 }),
		/* dir */new Vector(new double[] { -0.6, -0.25, -1 }),
		/* up */new Vector(new double[] { 0, -1, 0 }), 60, depthOfField, 1 / 5d);
		SoftLightSource lightSource1 = new SoftLightSource("lightSource1",
		/* pos */new Vector(new double[] { 0, 15, 15 }), 3d,
		/* col */new double[] { 1, 1, 1 });

		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);
		// lightSources.add(lightSource2);

		return new Scene("refract", camera, objects, lightSources);
	}

	public static Scene getSpaceScene() throws IOException {

		Material material = new Material();
		material.setSpecularPower(1d);
		material.setFileNameTextureMap("models/world/world.jpg");
		Sphere sphere = new Sphere(new Vector(new double[] { 0, 0, 0 }), 1,
				material);
		SceneCoreSphereObject sphereSceneObject = new SceneCoreSphereObject(
				sphere);

		RaytracingCamera camera = new RaytracingCamera(
		/* pos */new Vector(new double[] { -1, 1, -2 }),
		/* dir */new Vector(new double[] { 0.5, -0.5, 1 }),
		/* up */new Vector(new double[] { 0, -1, 0 }), 60);
		LightSource lightSource1 = new LightSource("lightSource1",
		/* pos */new Vector(new double[] { -1, 1, -2 }),
		/* col */new double[] { 1, 1, 1 });
		LightSource lightSource2 = new LightSource("lightSource1",
		/* pos */new Vector(new double[] { -1, 1, 2 }),
		/* col */new double[] { 1, 1, 1 });

		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);
		lightSources.add(lightSource2);

		Environment environment = new SphericalEnvironment(new Vector(new double[] { 0,
				0, 0 }), 4, "models/world/sky.jpg");

		return new Scene("space", camera, sphereSceneObject, lightSources,
				environment);
	}

	public static Scene getChessBoardScene() throws IOException {

		String foldername = "models/checkboard";
		String textureFolder = "";
		String modelPath1 = "board.obj";
		String modelPath2 = "horse.obj";
		String modelPath3 = "tower.obj";
		String modelPath4 = "king.obj";

		AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,
				modelPath1, textureFolder);
		Mesh mesh1 = loader1.load();
		SceneCoreMeshObject plane = new SceneCoreMeshObject(mesh1);
		Material boardMaterial = new Material();
		boardMaterial.setFileNameTextureMap(foldername + "/board.jpg", 7);
		plane.setMaterial(boardMaterial);
		plane.applyTransformation(Transformation.getScalingTransformation(60));

		Material glass = new Material();
		glass.setDielectric(true);
		glass.setRefractionIndex(1.3d);
		glass.setTransparancy(0.95d);
		glass.setDiffuseColor(new double[] { 0.35, 0.35, 0.7 });

		AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername,
				modelPath2);
		Mesh mesh2 = loader2.load();
		SceneCoreMeshObject horse1 = new SceneCoreMeshObject(mesh2);
		horse1.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(new double[] {
						1.197009 - 2.5,
						EngineSettings.DELTA_MACHINE - 0.001435, 2.398642 })));
		horse1.applyTransformation(Transformation
				.getScalingTransformation(new Vector(new double[] { 1.5, 1.5,
						1.5 })));
		horse1.setMaterial(glass);

		AdvancedObjLoader loader3 = new AdvancedObjLoader(foldername,
				modelPath3);
		Mesh mesh3 = loader3.load();
		SceneCoreMeshObject tower1 = new SceneCoreMeshObject(mesh3);
		tower1.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(
						new double[] { -3.685086, EngineSettings.DELTA_MACHINE,
								2.311467 - 2.5 })));
		tower1.applyTransformation(Transformation
				.getScalingTransformation(new Vector(new double[] { 1.5, 1.5,
						1.5 })));
		tower1.setMaterial(glass);

		AdvancedObjLoader loader4 = new AdvancedObjLoader(foldername,
				modelPath4);
		Mesh mesh4 = loader4.load();
		SceneCoreMeshObject king1 = new SceneCoreMeshObject(mesh4);
		king1.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(new double[] {
						-0.038378, EngineSettings.DELTA_MACHINE, 2.337116 })));
		king1.applyTransformation(Transformation
				.getScalingTransformation(new Vector(new double[] { 1.5, 1.5,
						1.5 })));
		king1.setMaterial(glass);

		SceneCompositeObject objects = new SceneCompositeObject(plane, horse1,
				king1, tower1);

		RaytracingCamera camera = new RaytracingCamera(
		/* pos */new Vector(new double[] { 5, 2.5, 5 }),
		/* dir */new Vector(new double[] { -1, -0.2, -1 }),
		/* up */new Vector(new double[] { 0, -1, 0 }), 60, 7, 0.07d);
		SoftLightSource lightSource1 = new SoftLightSource("lightSource1",
		/* pos */new Vector(new double[] { 0, 15, 15 }), 2d,
		/* col */new double[] { 1, 1, 1 });

		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);

		Environment environment = new SphericalEnvironment(new Vector(new double[] { 0,
				0, 0 }), 10, "models/checkboard/sky.jpg");

		return new Scene("chess", camera, objects, lightSources, environment);
	}

	public static Scene getReflectiveBallsScene() throws IOException {

		String foldername = "models";
		String modelPath1 = "plane.obj";
		String modelPath2 = "sphere.obj";

		AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,
				modelPath1);
		Mesh mesh1 = loader1.load();
		SceneCoreMeshObject plane = new SceneCoreMeshObject(mesh1);
		plane.applyTransformation(Transformation.getScalingTransformation(100));

		AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername,
				modelPath2);
		Mesh mesh2 = loader2.load();

		SceneCoreMeshObject sphere2 = new SceneCoreMeshObject(mesh2);
		sphere2.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(new double[] { 0, 1,
						-2 })));
		sphere2.applyTransformation(Transformation.getScalingTransformation(2));

		SceneCoreMeshObject sphere1 = new SceneCoreMeshObject(mesh2);
		sphere1.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(new double[] { -3, 1,
						0 })));

		SceneCoreMeshObject sphere3 = new SceneCoreMeshObject(mesh2);
		sphere3.applyTransformation(Transformation
				.getTranslationTransformation(new Vector(
						new double[] { 4, 1, 1 })));

		SceneCompositeObject objects = new SceneCompositeObject(plane, sphere1,
				sphere2, sphere3);

		RaytracingCamera camera = new RaytracingCamera(
		/* pos */new Vector(new double[] { 0, 8, 8 }),
		/* dir */new Vector(new double[] { 0, -0.7, -1 }),
		/* up */new Vector(new double[] { 0, -1, 0 }), 60);
		LightSource lightSource1 = new LightSource("lightSource1",
		/* pos */new Vector(new double[] { 0, 10, -1 }),
		/* col */new double[] { 1, 1, 1 });
		LightSource lightSource2 = new LightSource("lightSource1",
		/* pos */new Vector(new double[] { 10, 10, -1 }),
		/* col */new double[] { 1, 1, 1 });
		LightSource lightSource3 = new LightSource("lightSource1",
		/* pos */new Vector(new double[] { -10, 10, -1 }),
		/* col */new double[] { 1, 1, 1 });

		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);
		lightSources.add(lightSource2);
		lightSources.add(lightSource3);

		return new Scene("testScene1", camera, objects, lightSources);
	}
	
	public static Scene getSimpleTestScene() throws IOException {
		
		String foldername = "models";
		String modelPath1 = "sphere.obj";

		AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername,
				modelPath1);
		Mesh mesh1 = loader1.load();
		SceneCoreMeshObject sphere = new SceneCoreMeshObject(mesh1);

		RaytracingCamera camera = new RaytracingCamera(
				/* pos */new Vector(new double[] { 3, 3, 3}),
				/* dir */new Vector(new double[] { -1, -1, -1 }),
				/* up */new Vector(new double[] { 0, -1, 0 }), 60);
				LightSource lightSource1 = new LightSource("lightSource1",
				/* pos */new Vector(new double[] { 0, 3, 3}),
				/* col */new double[] { 1, 1, 1 });

		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);
		
		return new Scene("testScene0", camera, sphere, lightSources);
	}
	
	
	public static Scene getBottleScene() throws IOException {
		
		String foldername = "models/BottleCollection";
		String modelPath = "bot2.obj";
		String textureMap = "Texture";
		
		String foldername2 = "models/checkboard";
		String modelPath2 = "board.obj";
		String textureFolder = "";

		
		AdvancedObjLoader loader1 = new AdvancedObjLoader(foldername2,
				modelPath2, textureFolder);
		Mesh mesh1 = loader1.load();
		SceneCoreMeshObject plane = new SceneCoreMeshObject(mesh1);
		Material boardMaterial = new Material();
		boardMaterial.setFileNameTextureMap(foldername2 + "/board.jpg", 2.3);
		plane.setMaterial(boardMaterial);
		plane.applyTransformation(Transformation.getScalingTransformation(20));
		
		AdvancedObjLoader loader2 = new AdvancedObjLoader(foldername,modelPath,textureMap);
		Mesh mesh2 = loader2.load();
		SceneCoreMeshObject bot = new SceneCoreMeshObject(mesh2);
		bot.applyTransformation(Transformation.getScalingTransformation(0.28));

		
		SceneCompositeObject scene = new SceneCompositeObject(plane,bot);
		
		Material mat1 = new Material();
		mat1.setDiffuseColor(new double[] {1,1,0});
		mat1.setMirror(true);
		
		RaytracingCamera camera = new RaytracingCamera(
				/* pos */new Vector(new double[] { 4.5, 3, 4.5}),
				/* dir */new Vector(new double[] { -1, -0.2, -1}),
				/* up */new Vector(new double[] { 0, -1, 0 }), 60,6.36,0.0001);
				LightSource lightSource1 = new SoftLightSource("lightSource1",
				/* pos */new Vector(new double[] { 0, 8, 6}),
				0.5, /* col */new double[] { 0.8, 0.8, 0.8 });
				LightSource lightSource2 = new SoftLightSource("lightSource2",
				/* pos */new Vector(new double[] { 6, 8, 0}),
				0.5, /* col */new double[] { 0.8, 0.8, 0.8 });
			
		ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
		lightSources.add(lightSource1);
		lightSources.add(lightSource2);
				
		Environment env = new SphericalEnvironment(new Vector(new double[] {0,0,0}), 70, "models/checkboard/sky.jpg");
		
		return new Scene("testScene1", camera, scene, lightSources,env);
		
	}

}
