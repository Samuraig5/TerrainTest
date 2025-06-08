package Terrain;

import Engine3d.Rendering.Camera;
import Engine3d.Scene;
import Math.Vector.Vector3D;
import Physics.CollidableObject;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TerrainScene extends Scene {
    static public final double VOLUME_SIZE = 10;
    static public final int RENDER_SIZE = 3;

    private final Map<Vector3D, CollidableObject> terrainGrid = new ConcurrentHashMap<>();
    public TerrainScene(Camera camera) {
        super(camera);
    }

    public void removeVolume(Vector3D location, CollidableObject object) {
        terrainGrid.remove(location, object);
        removeObject(object);
    }

    public CollidableObject createNewTerrainVolume(Vector3D location, TerrainType type) {
        CollidableObject newObject = new CollidableObject(this);
        newObject.translate(location);
        TerrainVolume newVolume = new TerrainVolume(this, newObject, VOLUME_SIZE, type);
        newObject.setMesh(newVolume);

        terrainGrid.put(location, newObject);

        return newObject;
    }

    public CollidableObject getVolume(Vector3D location) {
        return terrainGrid.get(location);
    }

    public boolean isOccupied(Vector3D location) {
        return getVolume(location) != null;
    }

    @Override
    public void buildScreenBuffer() {
        if (terrainGrid == null) { return; }
        Vector3D playerPosReal = getCamera().getPosition();
        Vector3D playerPosGrid = new Vector3D();
        for (int i = 0; i < 3; i++) {
            int pos = (int) (Math.round(playerPosReal.getValue(i) / VOLUME_SIZE) * VOLUME_SIZE);
            playerPosGrid.setComponent(i, pos);
        }

        for (int x = (int) (playerPosGrid.x()-(RENDER_SIZE)*VOLUME_SIZE); x <= playerPosGrid.x()+(RENDER_SIZE)*VOLUME_SIZE ; x+=VOLUME_SIZE) {
            for (int y = (int) (playerPosGrid.y()-(RENDER_SIZE)*VOLUME_SIZE); y <= playerPosGrid.y()+(RENDER_SIZE)*VOLUME_SIZE ; y+=VOLUME_SIZE) {
                for (int z = (int) (playerPosGrid.z()-(RENDER_SIZE)*VOLUME_SIZE); z <= playerPosGrid.z()+(RENDER_SIZE)*VOLUME_SIZE ; z+=VOLUME_SIZE) {
                    Vector3D key = new Vector3D(x,y,z);
                    if (terrainGrid.containsKey(key)) {
                        setObjectState(terrainGrid.get(key), true);
                    }
                    else { //If object hasn't been generated yet, generate it
                        TerrainType type = TerrainType.AIR;
                        if (key.y() <= 0) {
                            type = TerrainType.ROCK;
                        }
                        createNewTerrainVolume(key, type);
                    }
                }
            }
        }

        for (Vector3D key : terrainGrid.keySet()) {
            setObjectState(terrainGrid.get(key), false);
            if (playerPosGrid.distanceTo(key) < RENDER_SIZE*VOLUME_SIZE) {
                setObjectState(terrainGrid.get(key), true);
            }
        }
        super.buildScreenBuffer();
    }
}
