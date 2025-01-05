package Engine3d;

import Engine3d.Math.Vector.Vector3D;

public interface Rotatable
{
    /**
     * Changes the rotation by translating it by the provided delta.
     * @param delta change in rotation in radiants.
     */
    void rotate(Vector3D delta);

    /**
     * Get the current rotation in radiants.
     * @return current rotation in radiants.
     */
    Vector3D getRotation();

    /**
     * Get the current direction of the rotatable.
     * @return current view direction.
     */
    Vector3D getDirection();
}
