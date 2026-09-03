package Engine3d.Rendering;

import Engine3d.DevTools.Profiler;
import Engine3d.Scene;
import Math.Vector.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SceneRenderer extends JPanel {

    private Scene activeScene;

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

        repaint();
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

        if (activeScene.isEditorMode()) {
            g.setColor(Color.WHITE);
            int cx = getWidth()/2, cy = getHeight()/2;
            g.drawLine(cx-10, cy, cx+10, cy);
            g.drawLine(cx, cy-10, cx, cy+10);
        }

        if (activeScene.getEngine() != null) {
            activeScene.getEngine().getConsole().render(g, getWidth(), getHeight());
        }
    }
}
