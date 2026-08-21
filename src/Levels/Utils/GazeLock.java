package Levels.Utils;

import Engine3d.Time.Updatable;
import Math.Vector.Vector3D;
import Physics.PlayerObject;

public class GazeLock implements Updatable {
    private final PlayerObject player;
    private final double targetYaw, targetPitch;
    private final double maxDev;     // final allowed deviation (radians)
    private final double spring;     // pull-to-center rate (per second)
    private boolean active = false;

    private double capYaw, capPitch;                 // current wall, closes toward maxDev
    private double closeSpeed = Math.toRadians(45);  // how fast the cone shrinks (rad/s)

    private Runnable onWallHit;
    private boolean wasClamped = false;
    private double lastTrigger = -1e9;
    private final double cooldown = 0.5;

    public GazeLock(PlayerObject player, Vector3D lookAt, double maxDeviationDeg, double spring) {
        this.player = player;
        Vector3D m = lookAt.normalized();
        this.targetYaw   = Math.atan2(m.x(), Math.sqrt(m.y()*m.y() + m.z()*m.z()));
        this.targetPitch = Math.atan2(m.y(), m.z());
        this.maxDev = Math.toRadians(maxDeviationDeg);
        this.spring = spring;
    }

    public void setActive(boolean a) {
        if (a && !active) {                          // capture where they're looking now
            Vector3D look = player.getLookRotation();
            capYaw   = Math.max(maxDev, Math.abs(wrap(targetYaw - look.y())));
            capPitch = Math.max(maxDev, Math.abs(targetPitch - look.x()));
        }
        active = a;
    }
    public void setOnWallHit(Runnable r) { this.onWallHit = r; }
    public void setCloseSpeed(double degPerSec) { this.closeSpeed = Math.toRadians(degPerSec); }

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        // the wall eases inward to maxDev
        capYaw   = Math.max(maxDev, capYaw   - closeSpeed * deltaTime);
        capPitch = Math.max(maxDev, capPitch - closeSpeed * deltaTime);

        Vector3D look = player.getLookRotation();
        double dYaw   = wrap(targetYaw   - look.y());
        double dPitch =      targetPitch - look.x();

        double k = Math.min(1, spring * deltaTime);
        double yawCorr   = dYaw   * k;
        double pitchCorr = dPitch * k;

        boolean clamped = false;
        if (Math.abs(dYaw   - yawCorr)   > capYaw)   { yawCorr   = dYaw   - Math.signum(dYaw)   * capYaw;   clamped = true; }
        if (Math.abs(dPitch - pitchCorr) > capPitch) { pitchCorr = dPitch - Math.signum(dPitch) * capPitch; clamped = true; }

        player.rotate(new Vector3D(pitchCorr, yawCorr, 0));

        // only count it as "hitting the wall" once the cone has fully closed to maxDev
        boolean atFinalWall = capYaw <= maxDev + 1e-6 && capPitch <= maxDev + 1e-6;
        if (clamped && atFinalWall && !wasClamped) {
            double now = System.nanoTime() * 1e-9;
            if (now - lastTrigger > cooldown && onWallHit != null) { onWallHit.run(); lastTrigger = now; }
        }
        wasClamped = clamped && atFinalWall;
    }

    private static double wrap(double a) {
        while (a >  Math.PI) a -= 2*Math.PI;
        while (a < -Math.PI) a += 2*Math.PI;
        return a;
    }
}