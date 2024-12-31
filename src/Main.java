import Controls.Controller;
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

        Scene testScene = new Scene();
        int size = 100;
        Point3D[] points = {
                new Point3D(-size, -size-50, -size),
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
        //cube.translate(new Vector3D(300, 300, 0));
        testScene.addObject(cube);

        SceneRenderer renderer = new SceneRenderer(testScene);
        renderer.setFocusable(true);

        double cameraX = -300;
        double cameraY = -300;
        testScene.getCamera().translate(new Vector3D(cameraX, cameraY, 0));

        Controller controller = new Controller();
        renderer.addKeyListener(controller);
        renderer.requestFocusInWindow();

        controller.attachTranslatable(testScene.getCamera());

        frame.add(renderer);
        frame.setVisible(true);

    }
}