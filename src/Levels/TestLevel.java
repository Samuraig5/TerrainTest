package Levels;

import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Controls.PlayerObject;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Physics.AABBCollider;
import Engine3d.Physics.AABBObject;
import Engine3d.Physics.CollidableObject;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Rendering.Scene;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestLevel extends Scene
{
    public TestLevel(Camera camera) {
        super(camera);

        backgroundColour = new Color(0, 128, 134);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(1);

        new HeadLight(camera, this);


        PlayerObject playerObject = new PlayerObject((PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,25,0));
        addObject(playerObject);

        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), playerObject);
        subscribeToTime(cameraController);

        String filepath = "Resources/Textures/Rock.png";
        try {
            BufferedImage rock = ImageIO.read(new File(filepath));

            AABBObject ground = new AABBObject();
            BoxMesh boxMesh = new BoxMesh(ground, new Vector3D(25,5,25));
            boxMesh.translate(new Vector3D(0,-5,0));
            boxMesh.setTexture(rock);
            boxMesh.setDiffuseColour(Color.white);
            boxMesh.showWireFrame(false);
            boxMesh.centreOn(new Vector3D(0,0,0));
            addObject(ground);
            ground.getAABBCollider().setWeight(-1);
        }
        catch (IOException e1) {
            getSceneRenderer().logError("Can't find file " + filepath);
        }
    }
}
