package Terrain;

import Math.Vector.Vector3D;

public class TerrainVertex extends Vector3D
{
    private SharedPoint sharedPoint;
    public TerrainVertex(double x, double y, double z) {
        super(x,y,z);
    }

    public SharedPoint getSharedPoint() {
        return sharedPoint;
    }

    public void assignToSharedPoint(SharedPoint sharedPoint) {
        this.sharedPoint = sharedPoint;
    }
}
