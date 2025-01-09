package Engine3d;

import Math.Vector.Vector3D;

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

    /**
     * Returns the world direction based on the local space direction vector.
     * Eg is base = Vector3D.FORWARD() the function returns the direction in which the payerObject is facing.
     * @param base Local space direction.
     * @return World space direction.
     */
    Vector3D getDirection(Vector3D base);
}
