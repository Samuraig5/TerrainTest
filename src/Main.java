import Engine3d.Rendering.PlayerCamera;
import Levels.TestLevel;
import Menus.MainMenu;

import javax.swing.*;
import java.util.Objects;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Bug Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true);

        frame.setLocationRelativeTo(null);

        if (args.length == 0 || Objects.equals(args[0], "MainMenu"))
        {
            new MainMenu(frame);
        }
        else if (Objects.equals(args[0], "TestLevel"))
        {
            PlayerCamera camera = new PlayerCamera(frame);
            new TestLevel(camera);
        }

        frame.setVisible(true);
    }
}
