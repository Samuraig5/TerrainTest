package Levels;

import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Controls.PlayerObject;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Physics.AABBCollisions.StaticAABBObject;
import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Rendering.Scene;
import Engine3d.Testing.RotatingObject;

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

        PlayerObject playerObject = new PlayerObject(this, (PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,10,0));

        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), playerObject);
        addUpdatable(cameraController);

        String filepath = "Resources/Textures/Rock.png";
        try {
            BufferedImage rock = ImageIO.read(new File(filepath));

            double roomSize = 50;
            double wallHeight = 10;

            StaticAABBObject ground = spawnWall(rock, new Vector3D(roomSize*2,1,roomSize*2));

            StaticAABBObject wall1 = spawnWall(rock, new Vector3D(roomSize,wallHeight,1));
            wall1.translate(Vector3D.FORWARD().scaled(roomSize/2));
            StaticAABBObject wall2 = spawnWall(rock, new Vector3D(roomSize,wallHeight,1));
            wall2.translate(Vector3D.BACK().scaled(roomSize/2));
            StaticAABBObject wall3 = spawnWall(rock, new Vector3D(1,wallHeight,roomSize));
            wall3.translate(Vector3D.RIGHT().scaled(roomSize/2));
            StaticAABBObject wall4 = spawnWall(rock, new Vector3D(1,wallHeight,roomSize));
            wall4.translate(Vector3D.LEFT().scaled(roomSize/2));

            StaticAABBObject box = spawnWall(rock, new Vector3D(5,5,5));
            box.translate(Vector3D.UP());
        }
        catch (IOException e1) {
            getSceneRenderer().logError("Can't find file " + filepath);
        }
    }

    private StaticAABBObject spawnWall(BufferedImage sprite, Vector3D size) {
        StaticAABBObject wall = new StaticAABBObject(this);
        BoxMesh boxMesh = new BoxMesh(wall, size);
        boxMesh.setTexture(sprite);
        boxMesh.setDiffuseColour(Color.white);
        boxMesh.showWireFrame(false);
        boxMesh.centreToMiddleBottom();
        return wall;
    }
}