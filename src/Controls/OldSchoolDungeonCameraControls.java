package Controls;

import Rendering.SceneRenderer;
import Time.Updatable;
import WorldSpace.Vector3D;

import java.awt.event.KeyEvent;

public class OldSchoolDungeonCameraControls extends Controller implements Updatable
{
    private float stepSize = 0.25f;
    private float turnStep = 0.05f;

    private boolean wDown = false;
    private boolean aDown = false;
    private boolean sDown = false;
    private boolean dDown = false;
    private boolean spaceDown = false;
    private boolean shiftDown = false;

    public OldSchoolDungeonCameraControls(SceneRenderer renderer) {
        super(renderer);
    }

    @Override
    public void update(double deltaTime) {
        Vector3D transDelta = new Vector3D();
        Vector3D rotDelta = new Vector3D();

        if (wDown) {transDelta.translate(new Vector3D(0, 0, stepSize));}
        if (sDown) {transDelta.translate(new Vector3D(0, 0, -stepSize));}
        if (spaceDown) {transDelta.translate(new Vector3D(0, stepSize, 0));}
        if (shiftDown) {transDelta.translate(new Vector3D(0, -stepSize, 0));}
        if (aDown) {rotDelta.translate(new Vector3D(0, turnStep, 0));}
        if (dDown) {rotDelta.translate(new Vector3D(0, -turnStep, 0));}

        updateTranslatables(transDelta);
        updateRotatables(rotDelta);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wDown = false;
            case KeyEvent.VK_A -> aDown = false;
            case KeyEvent.VK_S -> sDown = false;
            case KeyEvent.VK_D -> dDown = false;
            case KeyEvent.VK_SPACE -> spaceDown = false;
            case KeyEvent.VK_SHIFT -> shiftDown = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wDown = true;
            case KeyEvent.VK_A -> aDown = true;
            case KeyEvent.VK_S -> sDown = true;
            case KeyEvent.VK_D -> dDown = true;
            case KeyEvent.VK_SPACE -> spaceDown = true;
            case KeyEvent.VK_SHIFT -> shiftDown = true;
        }
    }
}
