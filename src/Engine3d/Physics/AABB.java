package Engine3d.Physics;

import Engine3d.Math.Box;
import Engine3d.Math.Vector.Vector3D;

public class AABB extends Box {

    public AABB(Vector3D min, Vector3D max) {
        super(min, max);
    }
}
