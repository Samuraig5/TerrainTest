package Rendering;

import WorldSpace.*;

import javax.swing.*;

public class Camera implements Translatable
{
    JFrame window;
    Matrix4x4 projectionMatrix;
    double focalDistance;
    double aspectRatio;
    double fov = 90;
    double fovRad;
    double zNear = 0.1d;
    double zFar = 1000;
    double zNormal = zFar/(zFar-zNear);

    Vector3D position;
    Vector3D rotation; // Rendering.Camera orientation (rotation in radians)

    public Camera(JFrame window, Vector3D position, Vector3D rotation, double focalDistance)
    {
        this.window = window;
        this.position = position;
        this.rotation = rotation;
        this.focalDistance = focalDistance;
        this.aspectRatio = (double) window.getHeight() / (double) window.getWidth();
        this.fovRad = 1.0f / Math.tan(fov * 0.5f / 180.0f * Math.PI);

        projectionMatrix = new Matrix4x4();
        projectionMatrix.mat[0][0] = aspectRatio * fovRad;
        projectionMatrix.mat[1][1] = fovRad;
        projectionMatrix.mat[2][2] = zFar / (zFar - zNear);
        projectionMatrix.mat[3][2] = (-zFar * zNear) / (zFar - zNear);
        projectionMatrix.mat[2][3] = 1.0f;
        projectionMatrix.mat[3][3] = 0.0f;
    }

    /**
     * Multiplies a given input vector with the projection matrix.
     * The input vector has an implied fourth element set to '1'.
     * So given a vector in, the implied vector in~ would be in~=(in.x, in.y, in.z, 1.0f).
     *
     * @param in The vector that should be projected.
     * @return The projection of 'in'.
     */
    public Vector3D projectVector(Vector3D in)
    {
        double x = in.x() * projectionMatrix.mat[0][0]
                + in.y() * projectionMatrix.mat[1][0]
                + in.z() * projectionMatrix.mat[2][0]
                + projectionMatrix.mat[3][0];
        double y = in.x() * projectionMatrix.mat[0][1]
                + in.y() * projectionMatrix.mat[1][1]
                + in.z() * projectionMatrix.mat[2][1]
                + projectionMatrix.mat[3][1];
        double z = in.x() * projectionMatrix.mat[0][2]
                + in.y() * projectionMatrix.mat[1][2]
                + in.z() * projectionMatrix.mat[2][2]
                + projectionMatrix.mat[3][2];
        double w = in.x() * projectionMatrix.mat[0][3]
                + in.y() * projectionMatrix.mat[1][3]
                + in.z() * projectionMatrix.mat[2][3]
                + projectionMatrix.mat[3][3];
        if (w != 0) //Normalisation of the output vector to 'z'
        {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Vector3D(x, y, z);
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }

    public Vector3D getScreenDimensions()
    {
        return new Vector3D(window.getWidth(), window.getHeight(), 0);
    }
}
