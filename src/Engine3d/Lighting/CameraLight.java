package Engine3d.Lighting;

import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Rendering.Scene;

public class CameraLight extends LightSource {

    Vector3D offRot;
    Camera camera;
    public CameraLight(PlayerCamera camera, Scene scene, Vector3D offRot) {
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
