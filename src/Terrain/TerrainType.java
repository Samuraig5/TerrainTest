package Terrain;

import Engine3d.Model.MTL;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.Material;

import java.awt.*;

public enum TerrainType {
    AIR,
    DIRT,
    ROCK;

    public static DrawInstructions getDrawInstructions(TerrainType type){
        switch (type.ordinal()) {
            case 0:
                return new DrawInstructions(false,false,false,false);
            case 1, 2:
                return new DrawInstructions(false,true,false,true);
            default:
                return new DrawInstructions(true,false,false,false);
        }
    }

    /**
     * @param type the terran type
     * @return true if that terrain type has collisions, false otherwise
     */
    public static boolean getCollisionLogic(TerrainType type){
        switch (type.ordinal()) {
            case 0:
                return false;
            default:
                return true;
        }
    }

    /**
     * @param type the terran type
     * @return true if that terrain type has is "solid" meaning other terrain types can not be extended into this terrain type.
     *          If false, a neighbouring terrain volume will not be blocked from being extended into this volume.
     */
    public static boolean getOccupationLogic(TerrainType type){
        switch (type.ordinal()) {
            case 0:
                return false;
            default:
                return true;
        }
    }

    public static Material getMaterial(TerrainType type) {
        Material material = new Material();

        switch (type.ordinal()) {
            case 0:
                material.setBaseColour(new Color(0));
                return material;
            case 1:
                material.setBaseColour(new Color(89, 57, 0));
                return material;
            case 2:
                material.setBaseColour(new Color(117, 117, 117));
                return material;
            default:
                return new Material();
        }
    }
}


