import Controls.Controller;
import Rendering.Camera;
import Rendering.Scene;
import Rendering.SceneRenderer;
import WorldSpace.Object3D;
import WorldSpace.Point3D;
import WorldSpace.Vector3D;

import javax.swing.*;
import java.util.Random;

public class Main {

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);

        Camera camera = new Camera(frame, new Vector3D(0,0,0), new Vector3D(0,0,0), 1000);
        Scene testScene = new Scene(camera);

        int size = 100;
        Point3D[] points = {
                new Point3D(-size, -size, -size),
                new Point3D(size, -size, -size),
                new Point3D(size, size, -size),
                new Point3D(-size, size, -size),
                new Point3D(-size, -size, size),
                new Point3D(size, -size, size),
                new Point3D(size, size, size),
                new Point3D(-size, size, size)
        };

        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Front face
                {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Back face
                {0, 4}, {1, 5}, {2, 6}, {3, 7}  // Connectors
        };
        Object3D cube = new Object3D(points, edges);
        cube.translate(new Vector3D(0, 0, 1000));
        testScene.addObject(cube);

        SceneRenderer renderer = new SceneRenderer(testScene);

        double cameraX = -300;
        double cameraY = -300;
        testScene.getCamera().translate(new Vector3D(cameraX, cameraY, 0));

        Controller controller = new Controller(renderer);
        controller.attachTranslatable(testScene.getCamera());
        controller.setAttachedRotatables(testScene.getCamera());

        frame.add(renderer);
        frame.setVisible(true);

    }
}