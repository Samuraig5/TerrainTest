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
        this.projectionMatrix = getProjectionMatrix();
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

    private Matrix4x4 getProjectionMatrix() {
        double aspectRatio = getScreenDimensions().y() / getScreenDimensions().x();
        double fovRad = 1.0f / Math.tan(fov * 0.5f / 180.0f * Math.PI);

        Matrix4x4 projectionMatrix = new Matrix4x4();
        projectionMatrix.mat[0][0] = aspectRatio * fovRad;
        projectionMatrix.mat[1][1] = fovRad;
        projectionMatrix.mat[2][2] = zFar / (zFar - zNear);
        projectionMatrix.mat[3][2] = (-zFar * zNear) / (zFar - zNear);
        projectionMatrix.mat[2][3] = 1.0f;
        projectionMatrix.mat[3][3] = 0.0f;
        return projectionMatrix;
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }
    public Vector3D getPosition(){return new Vector3D(position);}
}
