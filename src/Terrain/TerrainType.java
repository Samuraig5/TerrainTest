package Terrain;

import Engine3d.Rendering.DrawInstructions;

public enum TerrainType {
    AIR,
    DIRT,
    ROCK;

    public static DrawInstructions getDrawInstructions(TerrainType type){
        DrawInstructions dw;
        switch (type.ordinal()) {
            case 0:
                return new DrawInstructions(false,false,false,false);
            case 1, 2:
                return new DrawInstructions(false,true,false,true);
            default:
                return new DrawInstructions(true,false,false,false);
        }
    }

    public static boolean getCollisionLogic(TerrainType type){
        DrawInstructions dw;
        switch (type.ordinal()) {
            case 0:
                return false;
            default:
                return true;
        }
    }
}


