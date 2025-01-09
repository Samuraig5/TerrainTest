package Terrain;

import java.util.ArrayList;
import java.util.List;

public class SharedPoint
{
    private final List<TerrainVertex> terrainVertices = new ArrayList<>();

    public TerrainVertex[] getTerrainVertices() {
        return terrainVertices.toArray(new TerrainVertex[0]);
    }
}
