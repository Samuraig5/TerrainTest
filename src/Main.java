import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.PlayerCamera;
import Levels.BloodGulch;

import javax.swing.*;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        PlayerCamera camera = new PlayerCamera(frame);

        new BloodGulch(camera);

        frame.setVisible(true);
    }
}