package Engine3d.Rendering;

import Engine3d.Math.Vector.Vector3D;
import Engine3d.Time.TimeMeasurer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SceneRenderer extends JPanel
{
    private Thread updateThread;
    private Thread bufferThread;
    private volatile boolean running = true; // For controlled thread shutdown
    private long lastTime;

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

        errorMessagePos = new Vector3D(20, activeScene.camera.getScreenDimensions().y()/2,0);

        startBuildThread();
        startUpdateThread();

        repaint();
        //revalidate();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (activeScene != null) {
            paintActiveScene(g);
        }
        else {
            System.err.println("Engine3d.Rendering.SceneRenderer: No scene is loaded!");
        }
        repaint();
    }

    private void paintActiveScene(Graphics g)
    {
        timeMeasurer.pauseAndEndMeasurement("frameTime");
        timeMeasurer.addCycle("frameTime");
        long frameTime = timeMeasurer.getMeasurement("frameTime");
        timeMeasurer.startMeasurement("frameTime");

        activeScene.getCamera().drawScreenBuffer(g); //This is the only non-UI call :helenaPepe:

        g.setColor(Color.white);
        int screenWidth = (int) activeScene.camera.getScreenDimensions().x() - 20;
        String s = "Cam Pos: " + activeScene.camera.getPosition().toStringRounded();
        int sWidth = g.getFontMetrics().stringWidth(s);
        g.drawString(s,screenWidth-sWidth,20 );

        s = "Cam Rot: " + activeScene.camera.getRotation().toStringRounded();
        sWidth = g.getFontMetrics().stringWidth(s);
        g.drawString(s,screenWidth-sWidth,40 );

        g.drawString("FPS: " + timeMeasurer.getCyclesPerSecond("frameTime"), 20, 20);
        g.drawString(timeMeasurer.getMsPrintOut("frameTime", frameTime), 20, 40);

        g.drawString("Buffers/s: " + timeMeasurer.getCyclesPerSecond("buildScreenBuffer"), 20, 70);
        g.drawString(timeMeasurer.getMsPrintOut("buildScreenBuffer"), 20, 90);
        long buildScreenBuffer = timeMeasurer.getMeasurement("buildScreenBuffer");
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("Get Matrices", buildScreenBuffer), 30, 110);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("ObjWorldToScreen", buildScreenBuffer), 30, 130);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("TriangleClipping", buildScreenBuffer),30, 150);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("Texturizer", buildScreenBuffer),30, 170);

        g.drawString("Updates/s: " + timeMeasurer.getCyclesPerSecond("update"), 20, 200);
        long updateTime = timeMeasurer.getMeasurement("update");
        g.drawString(timeMeasurer.getMsPrintOut("updateTime", updateTime), 20, 220);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("applyGravity", updateTime), 30, 240);
        g.drawString(timeMeasurer.getPercentAndMsPrintOut("handleCollision", updateTime), 30, 260);


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

    private double deltaTime()
    {
        long currentTime = System.nanoTime();
        long deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        return nanoToSec(deltaTime);
    }

    private double nanoToSec(long ns)
    {
        return (double) ns / 1_000_000_000;
    }

    private void startBuildThread() {
        if (bufferThread != null && bufferThread.isAlive()) {
            bufferThread.interrupt(); // Stop the current thread
        }

        bufferThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (activeScene != null) {
                    timeMeasurer.startMeasurement("buildScreenBuffer");
                    activeScene.buildScreenBuffer();
                    timeMeasurer.pauseAndEndMeasurement("buildScreenBuffer");
                    timeMeasurer.addCycle("buildScreenBuffer");

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

        bufferThread.start();
    }

    private void startUpdateThread() {
        if (updateThread != null && updateThread.isAlive()) {
            updateThread.interrupt();
        }

        updateThread = new Thread(() -> {
            while (running) {
                if (activeScene != null) {
                    timeMeasurer.startMeasurement("update");
                    activeScene.update(deltaTime()); // Update game logic
                    timeMeasurer.pauseAndEndMeasurement("update");
                    timeMeasurer.addCycle("update");
                }

                try {
                    Thread.sleep(16); // ~60 FPS logic updates
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        updateThread.start();
    }

    public void stopThreads() {
        running = false;

        if (bufferThread != null) bufferThread.interrupt();
        if (updateThread != null) updateThread.interrupt();
    }
}
