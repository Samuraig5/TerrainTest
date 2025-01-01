package Controls;

import Rendering.SceneRenderer;
import WorldSpace.Rotatable;
import WorldSpace.Translatable;
import WorldSpace.Vector3D;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class OldSchoolDungeonCameraControls extends Controller
{
    private float stepSize = 0.25f;
    private float turnStep = 0.1f;

    public OldSchoolDungeonCameraControls(SceneRenderer renderer) {
        super(renderer);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Vector3D transDelta = new Vector3D();
        Vector3D rotDelta = new Vector3D();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> transDelta.translate(new Vector3D(0, 0, stepSize)); // Move forward
            case KeyEvent.VK_S -> transDelta.translate(new Vector3D(0, 0, -stepSize)); // Move backward
            //case KeyEvent.VK_A -> transDelta.translate(new Vector3D(-stepSize, 0, 0)); // Move left
            //case KeyEvent.VK_D -> transDelta.translate(new Vector3D(stepSize, 0, 0)); // Move right
            case KeyEvent.VK_SPACE -> transDelta.translate(new Vector3D(0, stepSize, 0)); // Move up
            case KeyEvent.VK_SHIFT -> transDelta.translate(new Vector3D(0, -stepSize, 0)); // Move down

            case KeyEvent.VK_A -> rotDelta.translate(new Vector3D(0, turnStep, 0)); // Turn left
            case KeyEvent.VK_D -> rotDelta.translate(new Vector3D(0, -turnStep, 0)); // Turn Right

        }
        updateTranslatables(transDelta);
        updateRotatables(rotDelta);
    }
}
