package Engine3d.Rendering;

import Engine3d.Math.Vector.Vector;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Time.TimeMeasurer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SceneRenderer extends JPanel
{
    private Thread buildThread;
    private Scene activeScene;
    private TimeMeasurer timeMeasurer;
    Vector3D errorMessagePos;
    Vector3D errorPosDelta = new Vector3D(0, 20, 0);
    List<String> errors = new ArrayList<>();

    public SceneRenderer()
    {
        setFocusable(true);
        requestFocusInWindow();
    }

    public void setActiveScene(Scene activeScene) {
        timeMeasurer = new TimeMeasurer();

        this.activeScene = activeScene;
        activeScene.addTimeMeasurer(timeMeasurer);
        startBuildThread();

        errorMessagePos = new Vector3D(20, activeScene.camera.getScreenDimensions().y()/2,0);

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
        //activeScene.drawScene();

        timeMeasurer.startMeasurement("DrawBuffer");
        activeScene.getCamera().drawScreenBuffer(g);
        timeMeasurer.stopMeasurement("DrawBuffer");
        long sceneDrawTime = timeMeasurer.getMeasurement("DrawScene");


        int screenWidth = (int) activeScene.camera.getScreenDimensions().x() - 20;

        String s = "Cam Pos: " + activeScene.camera.getPosition().toString();
        int sWidth = g.getFontMetrics().stringWidth(s);
        g.drawString(s,screenWidth-sWidth,20 );

        s = "Cam Rot: " + activeScene.camera.getRotation().toString();
        sWidth = g.getFontMetrics().stringWidth(s);
        g.drawString(s,screenWidth-sWidth,40 );

        g.drawString("FPS: " + timeMeasurer.getFPS(), 20, 20);
        g.drawString(timeMeasurer.getSelfMeasurement(), 20, 40);
        g.drawString(timeMeasurer.getMsPrintOut("DrawScene"), 20, 60);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("Get Matrices", sceneDrawTime), 30, 80);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("ObjWorldToScreen", sceneDrawTime), 30, 100);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("TriangleClipping", sceneDrawTime),30, 120);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("Texturizer", sceneDrawTime),30, 140);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("DrawBuffer", sceneDrawTime),30, 160);

        g.setColor(Color.red);

        Vector3D cursorPos = new Vector3D(errorMessagePos);
        for (String err : errors) {
            g.drawString(err, (int) cursorPos.x(), (int) cursorPos.y());
            cursorPos.translate(errorPosDelta);
        }
    }

    public void logError(String message) {
        errors.add(message);
    }

    private void startBuildThread() {
        if (buildThread != null && buildThread.isAlive()) {
            buildThread.interrupt(); // Stop the current thread
        }

        buildThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (activeScene != null) {
                    activeScene.drawScene();

                    synchronized (activeScene.getCamera()) {
                        activeScene.getCamera().swapBuffers();
                    }
                }

                try {
                    Thread.sleep(16);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        buildThread.start();
    }
}
