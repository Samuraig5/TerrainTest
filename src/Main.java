import Controls.CameraController;
import Controls.OldSchoolDungeonCameraControls;
import Rendering.Camera;
import Rendering.Scene;
import Rendering.SceneRenderer;
import Testing.Cube;
import Testing.RotatingCube;
import WorldSpace.ObjLoader;
import WorldSpace.Object3D;
import WorldSpace.Vector3D;

import javax.swing.*;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        Camera camera = new Camera(frame);
        Scene testScene = new Scene(camera);

        //RotatingCube cube = new RotatingCube(1, new Vector3D(0.001f, 0.0005f,0.0001f));
        //cube.translate(new Vector3D(0, 0, 9));
        //ObjLoader.loadFromObjFile("src/Testing/teapot.obj", cube);
        //cube.showWireFrame(true);
        //testScene.addObject(cube);

        Object3D axis = new Object3D();
        ObjLoader.loadFromObjFile("src/Testing/axis.obj", axis);
        axis.translate(new Vector3D(0,0,5));
        axis.showWireFrame(true);
        testScene.addObject(axis);

        SceneRenderer renderer = new SceneRenderer(testScene);

        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(renderer);
        cameraController.attachTranslatable(testScene.getCamera());
        cameraController.attachRotatable(testScene.getCamera());

        frame.add(renderer);
        frame.setVisible(true);
    }
}