package Rendering;

import WorldSpace.*;

import javax.swing.*;

public class Camera implements Translatable
{
    JFrame window;
    Matrix4x4 projectionMatrix;
    double fov = 90;
    double zNear = 0.1d;
    double zFar = 1000;

    Vector3D position = new Vector3D();
    Vector3D rotation = new Vector3D(); // Rendering.Camera orientation (rotation in radians)

    public Camera(JFrame window)
    {
        this.window = window;
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
        return projectionMatrix.multiplyWithVect3D(in);
    }
    public Triangle projectTriangle(Triangle in) {
        return projectionMatrix.multiplyWithTriangle(in);
    }

    public Vector3D getScreenDimensions() {
        return new Vector3D(window.getWidth(), window.getHeight(), 0);
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }
    public Vector3D getPosition(){return new Vector3D(position);}
}
