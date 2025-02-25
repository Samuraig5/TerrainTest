package Engine3d.Controls;

import Engine3d.Rendering.SceneRenderer;
import Engine3d.Time.Updatable;
import Math.Raycast.RayCollision;
import Math.Vector.Vector3D;
import Physics.PlayerObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class OldSchoolDungeonCameraControls extends Controller implements Updatable
{
    private float stepSize = 5f;
    private float turnStep = 4f;
    private float jumpStrength = 0.35f;
    private float mouseSensitivity = 0.5f; // Adjust for desired sensitivity

    private PlayerObject playerObject;

    private boolean wDown = false;
    private boolean aDown = false;
    private boolean sDown = false;
    private boolean dDown = false;
    private boolean spaceDown = false;
    private boolean shiftDown = false;
    private boolean ctrlDown = false;

    private int lastMouseX = -1;
    private int lastMouseY = -1;

    private SceneRenderer renderer;
    private Robot robot;
    private boolean centerCursor = true;
    private boolean recentering = false;

    public OldSchoolDungeonCameraControls(SceneRenderer renderer, PlayerObject playerObject) {
        super(renderer);

        this.playerObject = playerObject;
        attachTranslatable(playerObject);
        attachRotatable(playerObject);


        this.renderer = renderer;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        renderer.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
        });
    }

    @Override
    public void update(double deltaTime) {
        Vector3D transDelta = new Vector3D();
        Vector3D rotDelta = new Vector3D();

        double adjStepSize = stepSize * deltaTime;
        double adjTurnStep = turnStep * deltaTime;

        if (ctrlDown) {adjStepSize *= 2;}

        if (wDown) {transDelta.translate(new Vector3D(0, 0, adjStepSize));}
        if (sDown) {transDelta.translate(new Vector3D(0, 0, -adjStepSize));}
        //if (spaceDown) {transDelta.translate(new Vector3D(0, adjStepSize, 0));}
        if (shiftDown) {transDelta.translate(new Vector3D(0, -adjStepSize, 0));}
        if (aDown) {transDelta.translate(new Vector3D(adjStepSize, 0, 0));}
        if (dDown) {transDelta.translate(new Vector3D(-adjStepSize, 0, 0));}

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
        Vector3D adjJump = Vector3D.UP().scaled(jumpStrength);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wDown = true;
            case KeyEvent.VK_A -> aDown = true;
            case KeyEvent.VK_S -> sDown = true;
            case KeyEvent.VK_D -> dDown = true;
            case KeyEvent.VK_SPACE -> {
                spaceDown = true;
                if (playerObject.isGrounded()) {
                    playerObject.addMomentum(adjJump);
                }
            }
            case KeyEvent.VK_SHIFT -> shiftDown = true;
            case KeyEvent.VK_CONTROL -> ctrlDown = true;
            case KeyEvent.VK_ESCAPE -> {
                Point center = new Point(renderer.getWidth() / 2, renderer.getHeight() / 2);
                SwingUtilities.convertPointToScreen(center, renderer);
                if(!centerCursor) { centerCursor(center); }
                centerCursor = !centerCursor;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    private void handleMouseMoved(MouseEvent e) {
        if (recentering) {
            return; // Ignore events caused by the robot repositioning the cursor
        }

        // Calculate the center of the JFrame
        Point center = new Point(renderer.getWidth() / 2, renderer.getHeight() / 2);
        SwingUtilities.convertPointToScreen(center, renderer);

        // Calculate mouse movement deltas (optional, for game mechanics)
        int deltaX = e.getXOnScreen() - center.x;
        int deltaY = e.getYOnScreen() - center.y;

        if (centerCursor) {
            Vector3D rotDelta = new Vector3D(Math.toRadians(deltaY) * mouseSensitivity, Math.toRadians(-deltaX) * mouseSensitivity, 0);
            playerObject.rotate(rotDelta);
        }

        lastMouseX = e.getX();
        lastMouseY = e.getY();

        if (centerCursor) {
            centerCursor(center);
        }
    }

    private void centerCursor(Point center) {
        // Recenter the mouse cursor
        recentering = true;
        robot.mouseMove(center.x, center.y);
        recentering = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        RayCollision res = playerObject.cursorRayCast(25, 0.5f);
        Vector3D target = playerObject.findClosestPointToCollision(res);
        if (e.getButton() == 1) {
            target.translate(Vector3D.UP().scaled(1));
        }
        else {
            target.translate(Vector3D.DOWN().scaled(1));
        }
    }
}
