package Levels;

import Engine3d.Controls.CreativeCamera;
import Engine3d.Controls.OldSchoolFlyingControls;
import Engine3d.Lighting.HeadLight;
import Engine3d.Lighting.LightSource;
import Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Rendering.Scene;
import Math.Vector.Vector5D;
import Physics.Object3D;
import Terrain.TerrainTileMesh;

import java.awt.*;

import static Terrain.TerrainTileMesh.GRID_SIZE;

public class TerrainLevel extends Scene {
    public TerrainLevel(Camera camera) {
        super(camera);

        backgroundColour = new Color(51, 153, 255);

        CreativeCamera playerObject = new CreativeCamera(this, (PlayerCamera) camera);
        playerObject.translate(new Vector3D(0,5,0));
        OldSchoolFlyingControls cameraController = new OldSchoolFlyingControls(getSceneRenderer(), playerObject);
        addUpdatable(cameraController);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(90),0,0));
        sun.setLightIntensity(0.25);

        new HeadLight(camera, this);

        int maxX = 20;
        int maxZ = 20;

        TerrainTileMesh[][] meshes = new TerrainTileMesh[maxX][maxZ];

        for (int z = 0; z < maxZ; z++) {
            for (int x = 0; x < maxX; x++) {
                Vector3D coords = new Vector3D(x,0,z);
                Vector5D height = new Vector5D(
                        Math.round( Math.random()),
                        Math.round( Math.random()),
                        Math.round( Math.random()),
                        Math.round( Math.random()),
                        Math.round( Math.random())
                );

                Object3D object3D = new Object3D(this);
                object3D.translate(coords.scaled(GRID_SIZE));
                TerrainTileMesh tm = new TerrainTileMesh(object3D, coords, height, meshes);
                object3D.setMesh(tm);
                meshes[x][z] = tm;
            }
        }
    }
}
