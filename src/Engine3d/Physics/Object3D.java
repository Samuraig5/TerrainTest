package Engine3d.Physics;

import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Rotatable;
import Engine3d.Translatable;

public class Object3D implements Translatable, Rotatable
{
    private Mesh mesh;
    protected Vector3D rotation = new Vector3D();
    protected Vector3D position = new Vector3D();

    public Object3D() {}

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void rotate(Vector3D delta) {
        rotation.translate(rotation);
    }

    @Override
    public Vector3D getRotation() {
        return rotation;
    }

    @Override
    public Vector3D getDirection() {
        return Matrix4x4.get3dRotationMatrix(rotation).matrixVectorMultiplication(Vector3D.FORWARD());
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }

    @Override
    public Vector3D getPosition() {
        return position;
    }
}
