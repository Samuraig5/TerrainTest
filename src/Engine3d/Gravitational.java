package Engine3d;

import Engine3d.Time.Updatable;

public interface Gravitational extends Updatable
{
    void applyGravity(double g, double deltaTime);
    boolean isGrounded();
}
