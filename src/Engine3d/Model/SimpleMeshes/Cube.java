package Engine3d.Model.SimpleMeshes;

import Engine3d.Physics.Object3D;
import Engine3d.Rendering.Material;
import Engine3d.Model.Mesh;
import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector.Vector2D;
import Engine3d.Math.Vector.Vector3D;

public class Cube extends Box
{
    public Cube(Object3D object3D, double size) {
        super(object3D, new Vector3D(size,size,size));
    }
}
