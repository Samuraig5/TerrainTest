package Menus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings
{
    private String settingsPath;
    private File settingsFile;
    private Properties properties;

    public Settings(String settingsPath) {
        this.settingsPath = settingsPath;
        settingsFile = new File(settingsPath);

        // Check if file exists
        if (!settingsFile.exists()) {
            createSettingsFile();
        }
        else {
            System.out.println("Settings file already exists at: " + settingsFile.getAbsolutePath());
            reloadSettingFile();
        }
    }

    public void createSettingsFile() {
        deleteSettingsFile();
        try {
            // Attempt to create the file
            if (settingsFile.createNewFile()) {
                System.out.println("Settings file created: " + settingsFile.getAbsolutePath());
            } else {
                System.out.println("Failed to create the Settings file.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the Settings file.");
            e.printStackTrace();
        }

        // Write default content to the file
        try (FileWriter writer = new FileWriter(settingsFile)) {
            writer.write("WINDOW_HEIGHT=800\n");
            writer.write("WINDOW_WIDTH=800\n");
            writer.write("DEFAULT_GRAVITY=1.0\n");
            writer.write("VOLUME_SIZE=10\n");
            writer.write("RENDER_SIZE=4\n");
            writer.write("SEED=12345678\n");

            System.out.println("Default settings written.");
        }
        catch (IOException e) {
            System.out.println("An error occurred while writing to the Settings file.");
            e.printStackTrace();
        }
        reloadSettingFile();
    }

    public void deleteSettingsFile() {
        if (settingsFile.delete()) {
            System.out.println("Settings file deleted successfully.");
        }
        else {
            System.out.println("Failed to delete the Settings file.");
        }
    }

    public void reloadSettingFile() {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream(settingsPath)) {
            properties.load(input);
            System.out.println("Successfully loaded settings.");
        } catch (IOException e) {
            System.out.println("Error reading settings.");
            e.printStackTrace();
        }
    }

    private String safeAccessProperty(String key) {
        String val = properties.getProperty(key);
        if (val == null) {
            System.out.println("Error loading setting property.\nRegenerating settings file...");
            createSettingsFile();
            val = properties.getProperty(key);
        }
        return val;
    }

    public int WINDOW_HEIGHT() {
        return Integer.parseInt(safeAccessProperty("WINDOW_HEIGHT"));
    }
    public int WINDOW_WIDTH() {
        return Integer.parseInt(safeAccessProperty("WINDOW_WIDTH"));
    }
    public double DEFAULT_GRAVITY() {
        return Double.parseDouble(safeAccessProperty("DEFAULT_GRAVITY"));
    }
    public double VOLUME_SIZE() {
        return Double.parseDouble(safeAccessProperty("VOLUME_SIZE"));
    }
    public int RENDER_SIZE() {
        return Integer.parseInt(safeAccessProperty("RENDER_SIZE"));
    }
    public long SEED() {
        return Long.parseLong(safeAccessProperty("SEED"));
    }
}
