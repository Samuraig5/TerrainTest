package Engine3d.Rendering;

import Engine3d.Controls.PlayerObject;
import Engine3d.Math.Vector.Vector3D;
import javax.swing.*;

public class PlayerCamera extends Camera {
    PlayerObject playerObject;
    public PlayerCamera(JFrame window) {
        super(window);
    }

    public void setPlayerObject(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    @Override
    public void translate(Vector3D delta) {
        if (playerObject == null) { System.err.println("PlayerCamera: No PlayerObject is set!"); return;}
        playerObject.translate(delta);
        //System.out.println("Camera Pos: " + position.toString());
    }
    public Vector3D getPosition() {
        if (playerObject == null) { System.err.println("PlayerCamera: No PlayerObject is set!"); return new Vector3D();}
        return playerObject.getPosition().translated(playerObject.getCameraOffset());
    }
    @Override
    public void rotate(Vector3D delta) {
        if (playerObject == null) { System.err.println("PlayerCamera: No PlayerObject is set!"); return;}
        playerObject.rotate(delta);
        //System.out.println("Camera Rot: " + rotation.x() + ", " + rotation.y() + ", " + rotation.z());
    }
    @Override
    public Vector3D getRotation() {
        if (playerObject == null) { System.err.println("PlayerCamera: No PlayerObject is set!"); return new Vector3D();}
        return playerObject.getRotation();
    }
}
