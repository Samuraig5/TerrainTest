import Engine3d.Rendering.PlayerCamera;
import Levels.BloodGulch;
import Levels.GJKTest;
import Levels.TerrainLevel;
import Levels.TestLevel;
import Menus.MainMenu;

import javax.swing.*;
import java.util.Objects;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);

        if (Objects.equals(args[0], "MainMenu"))
        {
            new MainMenu(frame);
        }
        else if (Objects.equals(args[0], "TerrainTest"))
        {
            PlayerCamera camera = new PlayerCamera(frame);
            //new BloodGulch(camera);
            //new TestLevel(camera);
            new TerrainLevel(camera);
            //new GJKTest(camera);
        }


        frame.setVisible(true);
    }
}
