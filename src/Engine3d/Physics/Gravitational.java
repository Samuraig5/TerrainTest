package Engine3d.Physics;

import Engine3d.Time.Updatable;

public interface Gravitational extends Updatable
{
    void applyGravity(double g, double deltaTime);
    boolean isGrounded();
}
