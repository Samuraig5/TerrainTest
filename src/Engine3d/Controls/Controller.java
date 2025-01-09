package Engine3d.Controls;

import Engine3d.Rendering.SceneRenderer;
import Engine3d.Rotatable;
import Engine3d.Translatable;
import Math.Vector.Vector3D;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller implements KeyListener, MouseListener, MouseMotionListener
{
    private final List<Translatable> attachedTranslatables = new ArrayList<>();
    private final List<Rotatable> attachedRotatables = new ArrayList<>();

    public Controller(SceneRenderer renderer)
    {
        renderer.addKeyListener(this);
        renderer.addMouseListener(this);
        renderer.addMouseMotionListener(this);
    }

    public void attachTranslatable(Translatable translatable){attachedTranslatables.add(translatable);}
    public void attachRotatable(Rotatable rotatable) {attachedRotatables.add(rotatable);}
    void updateTranslatables(Vector3D delta)
    {
        for (Translatable trans : attachedTranslatables) {
            trans.translate(delta);
        }
    }
    void updateRotatables(Vector3D rotation)
    {
        for (Rotatable rot : attachedRotatables) {
            rot.rotate(rotation);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

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

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
