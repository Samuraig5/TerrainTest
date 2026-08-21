package Engine3d.Rendering;

import Engine3d.DevTools.Profiler;
import Engine3d.Scene;
import Math.Vector.Vector3D;

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
    Vector3D errorMessagePos;
    Vector3D errorPosDelta = new Vector3D(0, 20, 0);
    List<String> errors = new ArrayList<>();

    public SceneRenderer()
    {
        setFocusable(true);
        requestFocusInWindow();

        // Optional: Hide the cursor
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor hiddenCursor = toolkit.createCustomCursor(
                toolkit.getImage(""), // An empty image
                new Point(0, 0),
                "hiddenCursor"
        );
        setCursor(hiddenCursor);
    }

    public void setActiveScene(Scene activeScene) {
        this.activeScene = activeScene;

        errorMessagePos = new Vector3D(20, activeScene.getCamera().getScreenDimensions().y()/2,0);

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
        activeScene.getCamera().drawScreenBuffer(g); //This is the only non-UI call :helenaPepe:

        if (activeScene.getCamera().debugging) {
            g.setColor(Color.white);
            g.drawString("Buffers/s: " + Math.round(Profiler.rate("buffers")), 20, 40);
            g.drawString(String.format("build: %.2f ms", Profiler.ms("buildScreenBuffer")), 20, 60);
            g.drawString(String.format("  geometry: %.2f ms", Profiler.ms("geometry")), 30, 78);
            g.drawString(String.format("  raster:   %.2f ms", Profiler.ms("raster")), 30, 96);
            g.drawString(String.format("  filters:  %.2f ms", Profiler.ms("filters")), 30, 114);

            g.drawString("Updates/s: " + Math.round(Profiler.rate("updates")), 20, 140);
            g.drawString(String.format("update: %.2f ms", Profiler.ms("update")), 20, 160);
            g.drawString(String.format("  applyGravity:    %.2f ms", Profiler.ms("applyGravity")), 30, 178);
            g.drawString(String.format("  handleCollision: %.2f ms", Profiler.ms("handleCollision")), 30, 196);
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
                    try (Profiler.Span s = Profiler.span("buildScreenBuffer")) {
                        activeScene.buildScreenBuffer();
                    }
                    Profiler.count("buffers");

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
                try {
                    if (activeScene != null) {
                        try (Profiler.Span s = Profiler.span("update")) {
                            activeScene.update(deltaTime()); // Update game logic
                        }
                        Profiler.count("updates");
                    }
                }
                catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                try {
                    Thread.sleep(16); // ~60 FPS logic updates
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
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
