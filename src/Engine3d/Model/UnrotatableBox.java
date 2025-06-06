package Engine3d.Model;

import Math.Box;
import Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Object3D;

public class UnrotatableBox extends BoxMesh {
    public UnrotatableBox(Object3D object3D, Vector3D size) {
        super(object3D, size);
    }

    public UnrotatableBox(Object3D object3D, Box box) {
        super(object3D, box);
    }

    @Override
    public Vector3D getRotation() {
        return new Vector3D();
    }

}
