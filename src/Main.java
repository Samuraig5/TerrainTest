import Menus.MainMenu;

import javax.swing.*;
import java.util.Arrays;

public class Main {

    static int WINDOW_HEIGHT = 800;
    static int WINDOW_WIDTH = 800;

    public static void main(String[] args) {
        boolean DEV_MODE = Arrays.asList(args).contains("DEV");

        JFrame frame = new JFrame("Hell");
        ImageIcon imgicon = new ImageIcon("Resources/Textures/The White Sign.png");
        frame.setIconImage(imgicon.getImage());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        if (!DEV_MODE) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        }

        frame.setLocationRelativeTo(null);

        new MainMenu(frame);

        frame.setVisible(true);
    }
}
