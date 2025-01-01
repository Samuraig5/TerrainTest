package Controls;

import Rendering.Camera;
import Rendering.Scene;
import Rendering.SceneRenderer;
import WorldSpace.Rotatable;
import WorldSpace.Translatable;
import WorldSpace.Vector3D;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CameraController implements KeyListener, MouseListener, MouseMotionListener
{
    private final List<Translatable> attachedTranslatables = new ArrayList<>();
    private final List<Rotatable> attachedRotatables = new ArrayList<>();

    private boolean rightMousePressed = false;
    private int lastMouseX, lastMouseY;

    public CameraController(SceneRenderer renderer)
    {
        renderer.addKeyListener(this);
        renderer.addMouseListener(this);
        renderer.addMouseMotionListener(this);
    }

    public void attachTranslatable(Translatable translatable){attachedTranslatables.add(translatable);}
    public void setAttachedRotatables(Rotatable rotatable) {attachedRotatables.add(rotatable);}
    private void updateTranslatables(Vector3D delta)
    {
        for (Translatable trans : attachedTranslatables) {
            trans.translate(delta);
        }
    }
    public void updateRotatables(Vector3D rotation)
    {
        for (Rotatable rot : attachedRotatables) {
            rot.rotate(rotation);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        double step = 1; // Camera movement speed
        Vector3D delta = new Vector3D(0,0,0);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> delta.translate(new Vector3D(0, 0, step)); // Move forward
            case KeyEvent.VK_S -> delta.translate(new Vector3D(0, 0, -step)); // Move backward
            case KeyEvent.VK_A -> delta.translate(new Vector3D(-step, 0, 0)); // Move left
            case KeyEvent.VK_D -> delta.translate(new Vector3D(step, 0, 0)); // Move right
            case KeyEvent.VK_SPACE -> delta.translate(new Vector3D(0, step, 0)); // Move up
            case KeyEvent.VK_SHIFT -> delta.translate(new Vector3D(0, -step, 0)); // Move down
        }

        updateTranslatables(delta);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed for key releases in this example
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed for key typing in this example
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right mouse button
            rightMousePressed = true;
            lastMouseX = e.getX();
            lastMouseY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right mouse button
            rightMousePressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (rightMousePressed) {
            int deltaX = e.getX() - lastMouseX;
            int deltaY = e.getY() - lastMouseY;

            updateRotatables(new Vector3D(deltaY * 0.01, deltaX * 0.01, 0));

            // Update the last mouse position
            lastMouseX = e.getX();
            lastMouseY = e.getY();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
