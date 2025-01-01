package Rendering;

import WorldSpace.*;

import javax.swing.*;

public class Camera implements Translatable, Rotatable
{
    JFrame window;
    public Drawer drawer;
    Matrix4x4 projectionMatrix;
    double fov = 90;
    double zNear = 0.1d;
    double zFar = 1000;

    Vector3D position = new Vector3D();
    Vector3D rotation = new Vector3D();
    final Vector3D BASE_LOOK_DIRECTION = new Vector3D(0,0,1);

    public Camera(JFrame window)
    {
        this.window = window;
        this.drawer = new Drawer(this);
        double aspectRatio = getScreenDimensions().y() / getScreenDimensions().x();
        this.projectionMatrix = Matrix4x4.getProjectionMatrix(fov, aspectRatio, zNear, zFar);
    }

    /**
     * Projects a given vector in world space into a vector in screen space.
     *
     * @param in Vector in world space.
     * @return The projection of 'in' onto the screen space.
     */
    public Vector3D projectVector(Vector3D in) {
        return projectionMatrix.multiplyAndNormalize(in);
    }
    public Triangle projectTriangle(Triangle in) {
        return projectionMatrix.multiplyWithTriangle(in);
    }

    public Vector3D getScreenDimensions() {
        return new Vector3D(window.getWidth(), window.getHeight(), 0);
    }

    public Vector3D getNearPlane() {return new Vector3D(0,0,zNear);}
    public Vector3D getFarPlane() {return new Vector3D(0,0,zFar);}


    @Override
    public void translate(Vector3D delta) {
        Vector3D forwardMovement = getLookDirection().scaled(delta.z());
        Vector3D verticalMovement = new Vector3D(0,delta.y(),0);

        Vector3D movement = forwardMovement.translation(verticalMovement);

        position.translate(movement);
        //System.out.println("Camera Pos: " + position.x() + ", " + position.y() + ", " + position.z());
    }

    public Vector3D getPosition(){return new Vector3D(position);}
    public Vector3D getLookDirection()
    {
        Matrix4x4 cameraRot = Matrix4x4.getRotationMatrixY(rotation.y());
        return cameraRot.matrixVectorMultiplication(BASE_LOOK_DIRECTION);
    }

    @Override
    public void rotate(Vector3D delta) {
        rotation.translate(delta);
        //System.out.println("Camera Rot: " + rotation.x() + ", " + rotation.y() + ", " + rotation.z());
    }
}
