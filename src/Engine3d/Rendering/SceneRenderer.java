package Engine3d.Rendering;

import Engine3d.Time.TimeMeasurer;

import javax.swing.*;
import java.awt.*;

public class SceneRenderer extends JPanel
{
    private Scene activeScene;
    private TimeMeasurer timeMeasurer;

    public SceneRenderer(Scene initialScene)
    {
        setFocusable(true);
        requestFocusInWindow();
        timeMeasurer = new TimeMeasurer();

        activeScene = initialScene;
        activeScene.addTimeMeasurer(timeMeasurer);

        repaint();
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        repaint();
        revalidate();

        if (activeScene != null) {
            paintActiveScene(g);
        }
        else {
            System.err.println("Engine3d.Rendering.SceneRenderer: No scene is loaded!");
        }
    }

    private void paintActiveScene(Graphics g)
    {
        timeMeasurer.clearMeasurements();
        timeMeasurer.startMeasurement("DrawScene");

        activeScene.drawScene(g);

        long sceneDrawTime = timeMeasurer.getMeasurement("DrawScene");

        g.setColor(Color.white);
        g.drawString("FPS: " + timeMeasurer.getFPS(), 20, 20);
        g.drawString(timeMeasurer.getSelfMeasurement(), 20, 40);
        g.drawString(timeMeasurer.getMsPrintOut("DrawScene"), 20, 60);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("Get Matrices", sceneDrawTime), 30, 80);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("ObjWorldToScreen", sceneDrawTime), 30, 100);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("TriangleClipping", sceneDrawTime),30, 120);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("Texturizer", sceneDrawTime),30, 140);
    }
}
