import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Lighting.CameraLight;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Scene;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Model.*;
import Levels.BloodGulch;

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

        new BloodGulch(camera);

        frame.setVisible(true);
    }
}