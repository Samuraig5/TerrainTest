package Engine3d.Model.SimpleMeshes;

import Engine3d.Physics.Object3D;
import Engine3d.Math.Vector.Vector3D;

public class CubeMesh extends BoxMesh
{
    public CubeMesh(Object3D object3D, double size) {
        super(object3D, new Vector3D(size,size,size));
    }
}
