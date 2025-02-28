package Terrain;

import Math.Vector.Vector3D;

public enum TerrainVolumePoints
{
    TOP_FRONT_LEFT(new Vector3D(-0.5,1,-0.5)),
    TOP_FRONT_RIGHT(new Vector3D(0.5,1,-0.5)),
    TOP_BACK_RIGHT(new Vector3D(-0.5,1,0.5)),
    TOP_BACK_LEFT(new Vector3D(0.5,1,0.5)),
    TOP_CENTRE(new Vector3D(0,1,0)),
    BOTTOM_FRONT_LEFT(new Vector3D(-0.5,0,-0.5)),
    BOTTOM_FRONT_RIGHT(new Vector3D(0.5,0,-0.5)),
    BOTTOM_BACK_RIGHT(new Vector3D(-0.5,0,0.5)),
    BOTTOM_BACK_LEFT(new Vector3D(0.5,0,0.5)),
    BOTTOM_CENTRE(new Vector3D(0,0,0)),
    ;

    // Field to store the vector
    private final Vector3D vector;

    // Constructor to initialize the vector for each enum constant
    TerrainVolumePoints(Vector3D vector) {
        this.vector = vector;
    }

    // Getter method to access the vector
    public Vector3D getVector() {
        return vector;
    }
}
