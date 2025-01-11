package Engine3d.Lighting;

import Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Scene;

public class CameraLight extends LightSource {

    Vector3D offRot;
    Camera camera;
    public CameraLight(Camera camera, Scene scene, Vector3D offRot) {
        super(scene);
        this.camera = camera;
        this.offRot = offRot;
    }

    @Override
    public Vector3D getPosition() {
        return camera.getPosition();
    }
    @Override
    public Vector3D getRotation() {
        return camera.getRotation().translated(offRot);
        //return Matrix4x4.get3dRotationMatrix(finalRot).matrixVectorMultiplication(Vector3D.FORWARD());
    }
}
