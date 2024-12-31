package Rendering;

import WorldSpace.Point3D;
import WorldSpace.Rotatable;
import WorldSpace.Translatable;
import WorldSpace.Vector3D;

import javax.swing.*;

public class Camera implements Translatable, Rotatable
{
    JFrame window;
    Vector3D position;
    Vector3D rotation; // Rendering.Camera orientation (rotation in radians)
    double focalDistance;
    double nearPlane = 0;
    double farPlane = 10000;

    public Camera(JFrame window, Vector3D position, Vector3D rotation, double focalDistance)
    {
        this.window = window;
        this.position = position;
        this.rotation = rotation;
        this.focalDistance = focalDistance;
    }

    // Transform a vector from world space to camera space
    public Vector3D worldToRenderSpace(Vector3D worldPoint) {
        double screenWidth = window.getWidth();
        double screenHeight = window.getHeight();

        // Translate by camera position
        double px = worldPoint.x() - position.x();
        double py = worldPoint.y() - position.y();
        double pz = worldPoint.z() - position.z();

        // Rotate around X-axis
        double cosX = Math.cos(-rotation.x());
        double sinX = Math.sin(-rotation.x());
        double ty = cosX * py - sinX * pz;
        double tz = sinX * py + cosX * pz;
        py = ty;
        pz = tz;

        // Rotate around Y-axis
        double cosY = Math.cos(-rotation.y());
        double sinY = Math.sin(-rotation.y());
        double tx = cosY * px + sinY * pz;
        tz = -sinY * px + cosY * pz;
        px = tx;

        // Rotate around Z-axis
        double cosZ = Math.cos(-rotation.z());
        double sinZ = Math.sin(-rotation.z());
        double nx = cosZ * px - sinZ * py;
        double ny = sinZ * px + cosZ * py;

        // Apply frustum clipping of near and far planes
        if (tz < nearPlane || tz > farPlane) {
            return null;
        }

        // Perspective projection: Apply the focal length and adjust by the distance to the camera
        double screenX = (nx / tz) * focalDistance;
        double screenY = (ny / tz) * focalDistance;

        // Normalize the coordinates to center the screen (assuming screenWidth and screenHeight)
        double centerX = screenWidth / 2;
        double centerY = screenHeight / 2;

        // Return the 2D screen position after applying the perspective transformation
        return new Point3D(screenX + centerX, screenY + centerY, tz);
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }
    @Override
    public void rotate(Vector3D rotation) {
        rotation.set(rotation.x(), rotation.y(), 0);
        this.rotation.translate(rotation);
    }
    public double getFocalDistance() {return focalDistance;}


}
