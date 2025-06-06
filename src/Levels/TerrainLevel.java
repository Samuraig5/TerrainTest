package Levels;

import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Physics.PlayerObject;
import Terrain.TerrainScene;

import java.awt.*;

public class TerrainLevel extends TerrainScene {
    public TerrainLevel(Camera camera) {
        super(camera);

        backgroundColour = new Color(51, 153, 255);

        PlayerObject playerObject = new PlayerObject(this, (PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,25,0));
        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), playerObject);

        //CreativeCamera playerObject = new CreativeCamera(this, (PlayerCamera) camera);
        //playerObject.translate(new Vector3D(0,5,0));
        //OldSchoolFlyingControls cameraController = new OldSchoolFlyingControls(getSceneRenderer(), playerObject);
        addUpdatable(cameraController);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(0.25);

        new HeadLight(camera, this);

        /**
        int maxX = 3;
        int maxZ = 3;

        for (int z = -maxZ; z < maxZ; z++) {
            for (int x = -maxX; x < maxX; x++) {
                Vector3D position = new Vector3D(x*VOLUME_SIZE,0,z*VOLUME_SIZE);
                createNewTerrainVolume(position);
            }
        }
         **/
    }
}
