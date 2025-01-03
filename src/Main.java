import Controls.CameraController;
import Controls.OldSchoolDungeonCameraControls;
import Rendering.Camera;
import Rendering.Material;
import Rendering.Scene;
import Rendering.SceneRenderer;
import Testing.Cube;
import Testing.RotatingCube;
import WorldSpace.*;

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
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                camera.onFrameSizeChange();
            }
        });

        Scene testScene = new Scene(camera);

        //Cube cube = new Cube(1);
        //cube.translate(new Vector3D(-0.5,-0.5,0.5));
        //cube.showWireFrame(false);
        //testScene.addObject(cube);

        //RotatingCube cubeRot = new RotatingCube(2, new Vector3D(0.001f, 0.0005f,0.0001f));
        Object3D rockGolem = new Object3D();
        rockGolem.translate(new Vector3D(0, 0, 10));
        rockGolem.rotate(new Vector3D(0,Math.toRadians(180),0));
        ObjLoader.loadFromObjFile("src/Testing/StoneGolem.obj", "src/Testing/StoneGolemRock.tif", rockGolem);
        rockGolem.showWireFrame(false);
        testScene.addObject(rockGolem);

        //Object3D axis = new Object3D();
        //ObjLoader.loadFromObjFile("src/Testing/axis.obj", axis);
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