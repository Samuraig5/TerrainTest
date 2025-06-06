package Physics;

import Engine3d.Time.Updatable;
import Engine3d.Translatable;

public interface Collidable extends Translatable {
    Collider getCollider();
}
