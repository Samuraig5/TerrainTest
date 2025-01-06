package Levels;

import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Controls.PlayerObject;
import Engine3d.Lighting.CameraLight;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Rendering.ResourceManager.SpriteManager;
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

        camera.translate(new Vector3D(0,7,0));

        backgroundColour = new Color(0, 128, 134);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(1);

        new HeadLight(camera, this);

        String filepath = "Resources/Textures/Rock.png";
        try {
            BufferedImage rock = ImageIO.read(new File(filepath));

            Object3D ground = new Object3D();
            BoxMesh boxMesh = new BoxMesh(ground, new Vector3D(25,1,25));
            boxMesh.setTexture(rock);
            boxMesh.setDiffuseColour(Color.white);
            boxMesh.showWireFrame(false);
            addObject(ground);
        }
        catch (IOException e1) {
            getSceneRenderer().logError("Can't find file " + filepath);
        }

        PlayerObject playerObject = new PlayerObject((PlayerCamera) camera);
        addObject(playerObject);

        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), playerObject);
        subscribeToTime(cameraController);
    }
}
