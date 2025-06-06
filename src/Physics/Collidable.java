package Physics;

import Engine3d.Translatable;

public interface Collidable extends Translatable {
    Collider getCollider();
}
