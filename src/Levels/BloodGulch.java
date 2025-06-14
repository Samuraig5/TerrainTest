package Levels;

import Engine3d.Controls.CreativeCamera;
import Engine3d.Controls.OldSchoolFlyingControls;
import Engine3d.Lighting.CameraLight;
import Math.Vector.Vector3D;
import Engine3d.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Menus.Settings;

public class BloodGulch extends Scene
{

    public BloodGulch(Camera camera, Settings settings) {
        super(camera, settings);

        camera.translate(new Vector3D(0,7,0));

        CreativeCamera playerObject = new CreativeCamera(this, (PlayerCamera) camera);

        OldSchoolFlyingControls cameraController = new OldSchoolFlyingControls(getSceneRenderer(), playerObject);
        addUpdatable(cameraController);

        //LightSource sun = new LightSource(this);
        //sun.setRotation(new Vector3D(Math.toRadians(-90),0,0));
        //sun.setLightIntensity(0.5);

        CameraLight cl = new CameraLight(camera, this, new Vector3D());
        cl.setLightRange(100);

        CameraLight cl1 = new CameraLight(camera, this, new Vector3D(Math.toRadians(-45),0,0));
        cl1.setLightIntensity(0.5);
        cl1.setLightRange(100);
        CameraLight cl2 = new CameraLight(camera, this, new Vector3D(Math.toRadians(45),0,0));
        cl2.setLightIntensity(0.5);
        cl2.setLightRange(100);

        CameraLight cl3 = new CameraLight(camera, this, new Vector3D(0,Math.toRadians(-45),0));
        cl3.setLightIntensity(0.5);
        cl3.setLightRange(100);
        CameraLight cl4 = new CameraLight(camera, this, new Vector3D(0,Math.toRadians(45),0));
        cl4.setLightIntensity(0.5);
        cl4.setLightRange(100);

        //Cube cube = new Cube(1);
        //cube.translate(new Vector3D(-0.5,-0.5,0.5));
        //cube.showWireFrame(false);
        //testScene.addObject(cube);

        //RotatingCube cubeRot = new RotatingCube(10, new Vector3D(0.001f, 0.0005f,0.0001f));
        //cubeRot.translate(new Vector3D(0,0,20));
        //cubeRot.showWireFrame(false);
        //testScene.addObject(cubeRot);

        Object3D map = loadFromFile("Resources/Models/BloodGulch", "bloodgulch.obj");
        map.translate(new Vector3D(0, -90, 5));
        map.getMesh().getDrawInstructions().drawWireFrame = false;

        Object3D rockGolem = loadFromFile("Resources/Models/RockGolem", "Stone.obj");
        rockGolem.translate(new Vector3D(0, 0, 7));
        rockGolem.rotate(new Vector3D(0,Math.toRadians(180),0));
        map.getMesh().getDrawInstructions().drawWireFrame = false;

        Object3D rockGolem2 = loadFromFile( "Resources/Models/RockGolem", "Stone.obj");
        rockGolem2.translate(new Vector3D(-114.5, 0.7, 142));
        rockGolem2.rotate(new Vector3D(0,Math.toRadians(180),0));
        map.getMesh().getDrawInstructions().drawWireFrame = false;

        //Object3D axis = new Object3D();
        //objParser.loadFromObjFile("src/Engine3d.Testing/axis.obj", axis, true);
        //axis.translate(new Vector3D(0,0,5));
        //axis.showWireFrame(false);
        //testScene.addObject(axis);
    }
}
