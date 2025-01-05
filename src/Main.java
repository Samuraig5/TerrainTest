import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Lighting.CameraLight;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Scene;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Model.*;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        Camera camera = new Camera(frame);
        camera.translate(new Vector3D(0,5,0));
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                camera.onFrameSizeChange();
            }
        });

        Scene testScene = new Scene(camera);
        CameraLight cl = new CameraLight(camera, testScene, new Vector3D());
        cl.setLightRange(100);
        CameraLight cl1 = new CameraLight(camera, testScene, new Vector3D(Math.toRadians(-45),0,0));
        cl1.setLightIntensity(0.5);
        cl1.setLightRange(100);
        CameraLight cl2 = new CameraLight(camera, testScene, new Vector3D(Math.toRadians(45),0,0));
        cl2.setLightIntensity(0.5);
        cl2.setLightRange(100);
        CameraLight cl3 = new CameraLight(camera, testScene, new Vector3D(0,Math.toRadians(-45),0));
        cl3.setLightIntensity(0.5);
        cl3.setLightRange(100);
        CameraLight cl4 = new CameraLight(camera, testScene, new Vector3D(0,Math.toRadians(45),0));
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

        Object3D map = ObjParser.loadFromObjFile("Resources/Models/BloodGulch", "bloodgulch.obj");
        map.translate(new Vector3D(0, -90, 5));
        map.showWireFrame(false);
        testScene.addObject(map);


        Object3D rockGolem = ObjParser.loadFromObjFile("Resources/Models/RockGolem", "Stone.obj");
        rockGolem.translate(new Vector3D(0, 0, 7));
        rockGolem.rotate(new Vector3D(0,Math.toRadians(180),0));
        rockGolem.showWireFrame(false);
        testScene.addObject(rockGolem);

        //Object3D axis = new Object3D();
        //ObjLoader.loadFromObjFile("src/Engine3d.Testing/axis.obj", axis, true);
        //axis.translate(new Vector3D(0,0,5));
        //axis.showWireFrame(false);
        //testScene.addObject(axis);

        SceneRenderer renderer = new SceneRenderer(testScene);

        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(renderer);
        cameraController.attachTranslatable(testScene.getCamera());
        cameraController.attachRotatable(testScene.getCamera());
        testScene.subscribeToTime(cameraController);

        frame.add(renderer);
        frame.setVisible(true);
    }
}