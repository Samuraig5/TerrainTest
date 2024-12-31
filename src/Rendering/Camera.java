package Rendering;

import WorldSpace.Point3D;
import WorldSpace.Translatable;
import WorldSpace.Vector3D;

public class Camera implements Translatable
{
    Vector3D position;
    Vector3D rotation; // Rendering.Camera orientation (rotation in radians)

    public Camera(Vector3D position, Vector3D rotation)
    {
        this.position = position;
        this.rotation = rotation;
    }

    // Transform a vector from world space to camera space
    public Vector3D worldToRenderSpace(Vector3D worldPoint) {
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

        return new Point3D(nx, ny, tz);
    }

    @Override
    public void translate(Vector3D delta) {
        position.translate(delta);
    }
}
