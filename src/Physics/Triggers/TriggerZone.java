package Physics.Triggers;

import Engine3d.Time.Updatable;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.AABB;
import Physics.AABBCollisions.AABBObject;

public class TriggerZone implements Updatable {
    private final AABB region;
    private final AABBObject watched;     // usually the player
    private Runnable onEnter, onExit;
    private boolean once = true;          // fire onEnter only the first time by default
    private boolean fired = false;
    private boolean wasInside = false;

    public TriggerZone(AABB region, AABBObject watched) {
        this.region = region;
        this.watched = watched;
    }

    // fluent config
    public TriggerZone onEnter(Runnable r) { this.onEnter = r; return this; }
    public TriggerZone onExit(Runnable r)  { this.onExit  = r; return this; }
    public TriggerZone repeatable()        { this.once = false; return this; }

    @Override
    public void update(double deltaTime) {
        boolean inside = region.overlaps(watched.getAABBCollider().getAABB());

        if (inside && !wasInside) {                 // entered this frame
            if (onEnter != null && (!once || !fired)) { onEnter.run(); fired = true; }
        } else if (!inside && wasInside) {          // left this frame
            if (onExit != null) onExit.run();
        }
        wasInside = inside;
    }

    /** Build a zone from a center point and a size (full extents). */
    public static AABB box(Vector3D center, Vector3D size) {
        Vector3D h = size.scaled(0.5);
        return new AABB(center.translated(h.inverted()), center.translated(h));
    }
}