package Levels;

import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Model.BillboardScatterMesh;
import Math.Raycast.RayTriangle;
import Physics.Object3D;
import Physics.PlayerObject;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Physics.AABBCollisions.StaticAABBObject;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LastGiftLevel extends Scene
{
    public LastGiftLevel(Camera camera) {
        super(camera);

        backgroundColour = new Color(73, 0, 0);

        PlayerObject playerObject = new PlayerObject(this, (PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,20,0));
        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), playerObject);

        addUpdatable(cameraController);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(1);

        new HeadLight(camera, this);

        String crimson = "Resources/Textures/Crimson Nylium.png";
        String rose = "Resources/Textures/Rose.png";

        try {
            BufferedImage cimsonImg = ImageIO.read(new File(crimson));
            BufferedImage roseImg = ImageIO.read(new File(rose));

            double floorSize = 600;
            StaticAABBObject ground = spawnWall(cimsonImg, new Vector3D(floorSize,1,floorSize));
            ground.translate(Vector3D.DOWN().scaled(0.5));

            double roomSize = 50;
            BillboardScatterMesh flowers = new BillboardScatterMesh(roseImg);
            for (int x = (int)-roomSize; x < (int)roomSize; x++) {
                for (int z = (int)-roomSize; z < (int)roomSize; z++) {
                    flowers.add(new BillboardScatterMesh.Instance(
                            new Vector3D((x+Math.random()) /2,0.7, (z+Math.random()) /2), 0.5, 0.7));
                }
            }
            flowers.add(new BillboardScatterMesh.Wind(
                    new Vector3D(1,0,0.25).normalized(),
                    0.12,0.5,1
            ));

            Object3D flowerField = new Object3D(this);
            flowerField.setMesh(flowers);

        }
        catch (IOException e1) {
            getSceneRenderer().logError("Can't find file ");
        }
    }

    private StaticAABBObject spawnWall(BufferedImage sprite, Vector3D size) {
        StaticAABBObject wall = new StaticAABBObject(this);
        BoxMesh boxMesh = new BoxMesh(size);
        wall.setMesh(boxMesh);
        boxMesh.setTexture(sprite);
        boxMesh.setDiffuseColour(Color.white);
        boxMesh.getDrawInstructions().drawWireFrame = false;
        boxMesh.centreToMiddleBottom();
        return wall;
    }
}
