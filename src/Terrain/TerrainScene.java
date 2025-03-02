package Terrain;

import Engine3d.Rendering.Camera;
import Engine3d.Scene;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.StaticAABBObject;

import java.util.HashMap;
import java.util.Map;
public class TerrainScene extends Scene {
    static public final double VOLUME_SIZE = 10;

    private final Map<Vector3D, StaticAABBObject> terrainGrid = new HashMap<>();
    public TerrainScene(Camera camera) {
        super(camera);
    }

    public StaticAABBObject createNewTerrainVolume(Vector3D location) {
        StaticAABBObject newObject = new StaticAABBObject(this);
        newObject.translate(location);
        TerrainVolume newVolume = new TerrainVolume(this, newObject, VOLUME_SIZE);
        newObject.setMesh(newVolume);

        terrainGrid.put(location, newObject);

        return newObject;
    }

    public StaticAABBObject getVolume(Vector3D location) {
        return terrainGrid.get(location);
    }

    public boolean isOccupied(Vector3D location) {
        return getVolume(location) != null;
    }
}
