package Levels;

import Engine3d.Controls.CreativeCamera;
import Engine3d.Controls.OldSchoolFlyingControls;
import Engine3d.Lighting.LightSource;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Math.Vector.Vector3D;
import Physics.GJK_EPA.EPA;
import Physics.GJK_EPA.GJK;
import Physics.Object3D;

import java.awt.*;

public class GJKTest extends Scene{

    private Object3D object1;
    private Object3D object2;
    public GJKTest(Camera camera) {
        super(camera);

        backgroundColour = new Color(51, 153, 255);

        CreativeCamera playerObject = new CreativeCamera(this, (PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,0,0));
        OldSchoolFlyingControls cameraController = new OldSchoolFlyingControls(getSceneRenderer(), playerObject);
        addUpdatable(cameraController);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(0.25);

        object1 = new Object3D(this);
        BoxMesh mesh1 = new BoxMesh(object1, new Vector3D(1,1,1));
        mesh1.getDrawInstructions().drawWireFrame = true;
        object1.setMesh(mesh1);
        addObject(object1);


        object2 = playerObject;

    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        // Test for collision
        try {
            //boolean result = GJK.boolSolveGJK(object1, object2);
            //System.out.println("Collision Detected: " + result);
            Vector3D separator = EPA.solveEPA(object1, object2);
            if (!separator.isEmpty()) {
                System.out.println(separator);
            }
            object2.translate(separator);

        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

    }
}
