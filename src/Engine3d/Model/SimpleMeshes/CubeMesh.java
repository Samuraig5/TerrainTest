package Engine3d.Model.SimpleMeshes;

import Engine3d.Objects.Object3D;
import Math.Vector.Vector3D;

public class CubeMesh extends BoxMesh
{
    public CubeMesh(Object3D object3D, double size) {
        super(new Vector3D(size,size,size));
    }
}
