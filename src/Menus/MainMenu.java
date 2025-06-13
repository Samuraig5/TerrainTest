package Menus;

import Engine3d.Rendering.PlayerCamera;
import Levels.TerrainLevel;
import Levels.TestLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends Menu {

    public static final String GAME_NAME = "Terrain Test";
    public Settings settings;

    public MainMenu(JFrame frame, Settings settings) {
        super(frame);
        this.settings = settings;
    }

    @Override
    public void showMenu() {
        // Clear any existing content on the frame.
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Create a panel for the title and buttons.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title label.
        JLabel titleLabel = new JLabel(GAME_NAME);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel);

        // Create buttons.
        JButton playButton = new JButton("Play");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton settingsButton = new JButton("Settings");
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add buttons to the panel with spacing.
        panel.add(playButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(settingsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(exitButton);

        // Set up action listeners for each button.
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchGame();
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettings();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });

        // Add the panel to the frame.
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Stub method to launch the game.
    private void launchGame() {
        System.out.println("Launching Game...");

        frame.getContentPane().removeAll();

        PlayerCamera camera = new PlayerCamera(frame);
        //new BloodGulch(camera, settings);
        //new TestLevel(camera, settings);
        new TerrainLevel(camera, settings);
        //new GJKTest(camera, settings);

        frame.revalidate();
        frame.repaint();
    }

    // Stub method to open the settings.
    private void openSettings() {
        // Replace this with your settings logic.
        System.out.println("Opening settings...");
        // For example: new SettingsDialog(frame).setVisible(true);
    }

    // Method to exit the game.
    private void exitGame() {
        System.out.println("Exiting game...");
        System.exit(0);
    }
}
