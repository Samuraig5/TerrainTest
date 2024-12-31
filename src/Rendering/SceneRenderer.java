package Rendering;

import javax.swing.*;
import java.awt.*;

public class SceneRenderer extends JPanel
{
    private Scene activeScene;

    public SceneRenderer(Scene initialScene)
    {
        setFocusable(true);
        requestFocusInWindow();

        activeScene = initialScene;

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
            System.err.println("Rendering.SceneRenderer: No scene is loaded!");
        }
    }

    private void paintActiveScene(Graphics g)
    {
        //RenderVector offset = activeScene.getCameraOffset();
        //g.translate((int) (offset.x()*activeScene.getZoomLevel()), (int) (offset.y()*activeScene.getZoomLevel()));
        activeScene.drawScene(g);
    }
}
