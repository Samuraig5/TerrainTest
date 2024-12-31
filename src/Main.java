import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Rotating 3D Cube");
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
        cube.translate(new Vector3D(300, 300, 0));
        testScene.addObject(cube);

        SceneRenderer renderer = new SceneRenderer(testScene);
        frame.add(renderer);
        frame.setVisible(true);
    }
}