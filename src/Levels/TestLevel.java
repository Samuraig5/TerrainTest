package Levels;

import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Menus.Settings;
import Physics.CollidableObject;
import Physics.PlayerObject;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestLevel extends Scene
{
    public TestLevel(Camera camera, Settings settings) {
        super(camera, settings);

        backgroundColour = new Color(0, 128, 134);

        PlayerObject playerObject = new PlayerObject(this, (PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,20,0));
        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), playerObject);

        //CreativeCamera playerObject = new CreativeCamera(this, (PlayerCamera) camera);
        //playerObject.translate(new Vector3D(0,5,0));
        //OldSchoolFlyingControls cameraController = new OldSchoolFlyingControls(getSceneRenderer(), playerObject);
        addUpdatable(cameraController);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(1);

        new HeadLight(camera, this);

        /*
        RotatingObject bug = new RotatingObject(loadFromFile("Resources/Models/Ladybug", "ladybug.obj"));
        bug.translate(new Vector3D(5,2,5));
        bug.setRotationSpeed(new Vector3D(0,2,0));

        RotatingObject newBug;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                newBug = new RotatingObject(bug);
                newBug.translate(new Vector3D(i*2,0,j*2));
                newBug.rotate(new Vector3D(3,i+1*j,0));
            }
        }
        bug.translate(new Vector3D(0,-2,0));

         */

        String stone = "Resources/Textures/SmoothStoneWall.png";
        String lightMoss = "Resources/Textures/StoneBrickWallLightlyMossy.png";
        String heavyMoss = "Resources/Textures/StoneBrickWallHeavilyMossy.png";
        String grime = "Resources/Textures/Grime Top.png";

        try {
            BufferedImage stoneImg = ImageIO.read(new File(stone));
            BufferedImage lightMossImg = ImageIO.read(new File(lightMoss));
            BufferedImage heavyMossImg = ImageIO.read(new File(heavyMoss));
            BufferedImage grimeImg = ImageIO.read(new File(grime));

            double roomSize = 50;
            double wallHeight = 4;

            CollidableObject ground = spawnWall(stoneImg, new Vector3D(roomSize*2,1,roomSize*2));
            ground.translate(Vector3D.DOWN().scaled(0.5));


            CollidableObject wall1 = spawnWall(lightMossImg, new Vector3D(roomSize,wallHeight,1));
            wall1.translate(Vector3D.FORWARD().scaled(roomSize/2));
            CollidableObject wall2 = spawnWall(lightMossImg, new Vector3D(roomSize,wallHeight,1));
            wall2.translate(Vector3D.BACK().scaled(roomSize/2));
            CollidableObject wall3 = spawnWall(lightMossImg, new Vector3D(1,wallHeight,roomSize));
            wall3.translate(Vector3D.RIGHT().scaled(roomSize/2));
            CollidableObject wall4 = spawnWall(lightMossImg, new Vector3D(1,wallHeight,roomSize));
            wall4.translate(Vector3D.LEFT().scaled(roomSize/2));

            CollidableObject box = spawnWall(grimeImg, new Vector3D(5,5,5));
            box.translate(Vector3D.UP());

            CollidableObject box2 = spawnWall(heavyMossImg, new Vector3D(7,2.5,7));
            box2.translate(Vector3D.UP());

            CollidableObject wa = spawnWall(grimeImg, new Vector3D(25,2,25));
            wa.translate(new Vector3D(10,0,10));
            wa.rotate(new Vector3D(Math.toRadians(-25),0,0));

            /*
            StaticAABBObject box = spawnWall(grimeImg, new Vector3D(5,5,5));
            box.translate(new Vector3D(5,0,0));

            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    StaticAABBObject wa = spawnWall(grimeImg, new Vector3D(1,1,1));
                    wa.translate(new Vector3D(i*2,1,j*2));
                }
            }
             */
        }
        catch (IOException e1) {
            getSceneRenderer().logError("Can't find file ");
        }
    }

    private CollidableObject spawnWall(BufferedImage sprite, Vector3D size) {
        CollidableObject wall = new CollidableObject(this);
        BoxMesh boxMesh = new BoxMesh(wall, size);
        wall.setMesh(boxMesh);
        boxMesh.setTexture(sprite);
        boxMesh.setDiffuseColour(Color.white);
        boxMesh.getDrawInstructions().drawWireFrame = false;
        boxMesh.centreToMiddleBottom();
        return wall;
    }
}
