package Engine3d.Lighting;

import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.Scene;

public class CameraLight extends LightSource{

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
        Vector3D finalRot = offRot.translation(camera.getRotation());

        Matrix4x4 yRot = Matrix4x4.getRotationMatrixY(finalRot.y());
        Matrix4x4 xRot = Matrix4x4.getRotationMatrixX(finalRot.x());
        Matrix4x4 rotMat = Matrix4x4.matrixMatrixMultiplication(xRot, yRot);

        return rotMat.matrixVectorMultiplication(Camera.BASE_LOOK_DIRECTION);
    }
}
