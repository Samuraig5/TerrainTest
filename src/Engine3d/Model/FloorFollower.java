package Engine3d.Model;

import Engine3d.Time.Updatable;
import Engine3d.Objects.Translatable;
import Math.Vector.Vector3D;
import Engine3d.Objects.Object3D;

public class FloorFollower implements Updatable {
    private final Object3D floor;
    private final Translatable player;
    private final double period;   // snap increment = texture tiling period (world units)

    public FloorFollower(Object3D floor, Translatable player, double period) {
        this.floor = floor;
        this.player = player;
        this.period = period;
    }

    @Override
    public void update(double deltaTime) {
        Vector3D pp = player.getPosition();
        double snapX = Math.floor(pp.x() / period) * period;
        double snapZ = Math.floor(pp.z() / period) * period;

        Vector3D fp = floor.getPosition();
        double dx = snapX - fp.x();
        double dz = snapZ - fp.z();
        if (dx != 0 || dz != 0) {
            floor.translate(new Vector3D(dx, 0, dz));   // X/Z only — Y (the -0.5) stays put
        }
    }
}