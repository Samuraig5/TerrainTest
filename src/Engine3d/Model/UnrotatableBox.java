package Engine3d.Model;

import Engine3d.Math.Box;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Physics.Object3D;

import java.awt.*;

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
