import Engine3d.Rendering.PlayerCamera;
import Levels.TerrainLevel;
import Levels.TestLevel;
import Menus.MainMenu;
import Menus.Settings;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class Main {

    public static String SETTINGS_PATH = "Resources/Settings";
    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        Settings settings = new Settings(SETTINGS_PATH);

        JFrame frame = new JFrame("Terrain Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(settings.WINDOW_WIDTH(), settings.WINDOW_HEIGHT());
        frame.setLocationRelativeTo(null);

        if (args.length == 0 || Objects.equals(args[0], "MainMenu"))
        {
            new MainMenu(frame, settings);
        }
        else if (Objects.equals(args[0], "TerrainTest"))
        {
            PlayerCamera camera = new PlayerCamera(frame);
            new TerrainLevel(camera, settings);
        }
        else if (Objects.equals(args[0], "TestLevel"))
        {
            PlayerCamera camera = new PlayerCamera(frame);
            new TestLevel(camera, settings);
        }

        frame.setVisible(true);
    }
}
