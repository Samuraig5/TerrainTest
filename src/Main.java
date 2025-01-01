import Controls.CameraController;
import Rendering.Camera;
import Rendering.Scene;
import Rendering.SceneRenderer;
import Testing.Cube;
import Testing.RotatingCube;
import WorldSpace.Vector3D;

import javax.swing.*;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        Camera camera = new Camera(frame, new Vector3D(0,0,0), new Vector3D(0,0,0), 1000);
        Scene testScene = new Scene(camera);

        RotatingCube cube = new RotatingCube(1, 0.005f);
        cube.translate(new Vector3D(0, 0, 3));
        testScene.addObject(cube);

        SceneRenderer renderer = new SceneRenderer(testScene);

        CameraController cameraController = new CameraController(renderer);
        cameraController.attachTranslatable(testScene.getCamera());

        frame.add(renderer);
        frame.setVisible(true);

    }
}