package Engine3d.Model;

import Math.Box;
import Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Physics.Object3D;

public class UnrotatableBox extends BoxMesh {
    public UnrotatableBox(Vector3D size) {
        super(size);
    }

    public UnrotatableBox(Box box) {
        super(box);
    }

    @Override
    public Vector3D getRotation() {
        return new Vector3D();
    }

}
