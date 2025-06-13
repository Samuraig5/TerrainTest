package Terrain;

import Engine3d.Rendering.Camera;
import Engine3d.Scene;
import Math.OpenSimplex2S;
import Math.Vector.Vector3D;
import Menus.Settings;
import Physics.CollidableObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static Terrain.TerrainVolumePoints.*;

public class TerrainScene extends Scene {
    public double VOLUME_SIZE;
    public int RENDER_SIZE;
    public long SEED;

    private final Map<Vector3D, CollidableObject> terrainGrid = new ConcurrentHashMap<>();
    public TerrainScene(Camera camera, Settings settings) {
        super(camera, settings);
        VOLUME_SIZE = settings.VOLUME_SIZE();
        RENDER_SIZE = settings.RENDER_SIZE();
        SEED = settings.SEED();
    }

    public void removeVolume(Vector3D location, CollidableObject object) {
        terrainGrid.remove(location, object);
        removeObject(object);
    }

    public CollidableObject createNewTerrainVolume(Vector3D location, TerrainType type, double[] heightOffset) {
        CollidableObject newObject = new CollidableObject(this);
        newObject.translate(location);
        TerrainVolume newVolume = new TerrainVolume(this, newObject, VOLUME_SIZE, type, heightOffset);
        newObject.setMesh(newVolume);

        terrainGrid.put(location, newObject);

        return newObject;
    }

    public CollidableObject createNewTerrainVolume(Vector3D location, TerrainType type) {
        return createNewTerrainVolume(location, type, new double[10]);
    }

    public CollidableObject getVolume(Vector3D location) {
        return terrainGrid.get(location);
    }

    public boolean isOccupied(Vector3D location) {
        CollidableObject colObj = getVolume(location);
        if (colObj == null) { return false; }
        TerrainVolume tv = (TerrainVolume) colObj.getMesh();
        if (tv == null) { return false; }
        TerrainType tt = tv.getTerrainType();
        if (tt == null) { return false; }
        return TerrainType.getOccupationLogic(tt);
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
            for (int y = (int) (playerPosGrid.y()+(RENDER_SIZE)*VOLUME_SIZE); y > playerPosGrid.y()-(RENDER_SIZE)*VOLUME_SIZE ; y-=VOLUME_SIZE) { //Go from top to bottom
                for (int z = (int) (playerPosGrid.z()-(RENDER_SIZE)*VOLUME_SIZE); z <= playerPosGrid.z()+(RENDER_SIZE)*VOLUME_SIZE ; z+=VOLUME_SIZE) {
                    Vector3D key = new Vector3D(x,y,z);
                    if (key.y() < 0) { continue; }
                        if (terrainGrid.containsKey(key)) {
                        setObjectState(terrainGrid.get(key), true);
                    }
                    else { //If object hasn't been generated yet, generate it
                        TerrainType type = TerrainType.AIR;
                        double[] heightOffset = new double[10];
                        if (key.y() <= 0) {
                            type = TerrainType.ROCK;
                            addHeightOffset(0.01f, (float) VOLUME_SIZE*5,new Vector3D(x, y, z), heightOffset);
                            addHeightOffset(0.1f,1f,new Vector3D(x, y, z), heightOffset);

                        }
                        createNewTerrainVolume(key, type, heightOffset);
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

    /**
     * Adds to the heightOffset with random height offsets based on the OpenSimplex algorithm.
     * @param frequency frequency of the offset. A higher frequency means a point's offset depends less on the neighbours.
     * @param volume volume of the offset. A higher volume means a greater offset.
     * @param volumeCoords coordinates of the volume this height offset is applied to.
     * @param heightOffset the array the offset should be added to.
     */
    private void addHeightOffset(float frequency, float volume, Vector3D volumeCoords, double[] heightOffset) {

        Vector3D miniPos;

        miniPos = TOP_FRONT_LEFT.getVector().scaled(VOLUME_SIZE).translated(volumeCoords).scaled(frequency);
        heightOffset[0] += volume * OpenSimplex2S.noise2(SEED, miniPos.x(), miniPos.z());

        miniPos = TOP_FRONT_RIGHT.getVector().scaled(VOLUME_SIZE).translated(volumeCoords).scaled(frequency);
        heightOffset[1] += volume * OpenSimplex2S.noise2(SEED, miniPos.x(), miniPos.z());

        miniPos = TOP_BACK_LEFT.getVector().scaled(VOLUME_SIZE).translated(volumeCoords).scaled(frequency);
        heightOffset[2] += volume * OpenSimplex2S.noise2(SEED, miniPos.x(), miniPos.z());

        miniPos = TOP_BACK_RIGHT.getVector().scaled(VOLUME_SIZE).translated(volumeCoords).scaled(frequency);
        heightOffset[3] += volume * OpenSimplex2S.noise2(SEED, miniPos.x(), miniPos.z());

        miniPos = TOP_CENTRE.getVector().scaled(VOLUME_SIZE).translated(volumeCoords).scaled(frequency);
        heightOffset[4] += volume * OpenSimplex2S.noise2(SEED, miniPos.x(), miniPos.z());
    }
}
