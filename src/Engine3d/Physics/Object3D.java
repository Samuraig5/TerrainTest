package Engine3d.Physics;

import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.Mesh;
import Engine3d.Model.SimpleMeshes.CubeMesh;
import Engine3d.Rendering.Scene;
import Engine3d.Rotatable;
import Engine3d.Translatable;

public class Object3D implements Translatable, Rotatable
{
    private Scene scene;
    private Mesh mesh;
    protected Vector3D rotation = new Vector3D();
    protected Vector3D position = new Vector3D();

    public Object3D(Scene scene) {
        this.scene = scene;
        scene.addObject(this);
        setUpDebugging();
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public void rotate(Vector3D delta) {
        rotation.translate(delta);
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

    public Scene getScene() {return scene;}

    // === DEBUGGING ===

    double sourceBoxSize = 0.25d;
    Mesh source = new CubeMesh(this, sourceBoxSize);
    private void setUpDebugging() {
        //Centers the box on the source of the object
        source.translate(new Vector3D(-sourceBoxSize/2,-sourceBoxSize/2,-sourceBoxSize/2));
        source.showWireFrame(true);
    }
    public Mesh getSource() {
        return source;
    }
}
