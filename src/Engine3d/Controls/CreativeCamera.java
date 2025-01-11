package Engine3d.Controls;

import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Physics.PlayerObject;

public class CreativeCamera extends PlayerObject
{

    public CreativeCamera(Scene scene, PlayerCamera camera) {
        super(scene, camera);
    }

    @Override
    public void applyGravity(double g, double deltaTime) {
        //Do nothing
    }
    @Override
    public boolean isGrounded() {
        return true;
    }
}
