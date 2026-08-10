package Engine3d.Controls;

import Math.Vector.Vector3D;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Time.Updatable;

import java.awt.event.KeyEvent;

public class OldSchoolFlyingControls extends Controller implements Updatable
{
    private float stepSize = 5f;
    private float turnStep = 4f;

    private CreativeCamera playerObject;

    private volatile boolean wDown = false;
    private volatile boolean aDown = false;
    private volatile boolean sDown = false;
    private volatile boolean dDown = false;
    private volatile boolean spaceDown = false;
    private volatile boolean shiftDown = false;
    private volatile boolean ctrlDown = false;


    public OldSchoolFlyingControls(SceneRenderer renderer, CreativeCamera playerObject) {
        super(renderer);

        this.playerObject = playerObject;
        attachTranslatable(playerObject);
        attachRotatable(playerObject);
    }

    @Override
    public void update(double deltaTime) {
        Vector3D transDelta = new Vector3D(0,0,0);
        Vector3D rotDelta = new Vector3D(0,0,0);

        double adjStepSize = stepSize * deltaTime;
        double adjTurnStep = turnStep * deltaTime;

        if (ctrlDown) {adjStepSize *= 5;}

        if (wDown) {transDelta = transDelta.translated(new Vector3D(0, 0, adjStepSize));}
        if (sDown) {transDelta = transDelta.translated(new Vector3D(0, 0, -adjStepSize));}
        if (spaceDown) {transDelta = transDelta.translated(new Vector3D(0, adjStepSize, 0));}
        if (shiftDown) {transDelta = transDelta.translated(new Vector3D(0, -adjStepSize, 0));}
        if (aDown) {rotDelta = rotDelta.translated(new Vector3D(0, adjTurnStep, 0));}
        if (dDown) {rotDelta = rotDelta.translated(new Vector3D(0, -adjTurnStep, 0));}

        playerObject.localTranslate(transDelta);
        playerObject.rotate(rotDelta);
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
            case KeyEvent.VK_CONTROL -> ctrlDown = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wDown = true;
            case KeyEvent.VK_A -> aDown = true;
            case KeyEvent.VK_S -> sDown = true;
            case KeyEvent.VK_D -> dDown = true;
            case KeyEvent.VK_SPACE -> spaceDown = true;
            case KeyEvent.VK_SHIFT -> shiftDown = true;
            case KeyEvent.VK_CONTROL -> ctrlDown = true;
        }
    }
}
