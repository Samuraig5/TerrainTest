package Engine3d.Model;

import Math.Geometries.Box;
import Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;

public class UnrotatableBox extends BoxMesh {
    public UnrotatableBox(Vector3D size) {
        super(size);
    }

    public UnrotatableBox(Box box) {
        super(box);
    }

    @Override
    public Vector3D getRotation() {
        return new Vector3D(0,0,0);
    }

}
