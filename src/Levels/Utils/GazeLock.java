package Levels.Utils;

import Engine3d.Time.Updatable;
import Math.Vector.Vector3D;
import Physics.PlayerObject;

public class GazeLock implements Updatable {
    private final PlayerObject player;
    private final double targetYaw, targetPitch;
    private final double maxDev;     // radians of allowed deviation
    private final double spring;     // pull rate (per second)
    private boolean active = false;
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

    public void setActive(boolean a) { active = a; }
    public void setOnWallHit(Runnable r) { this.onWallHit = r; }

    @Override
    public void update(double deltaTime) {
        if (!active) return;
        Vector3D look = player.getLookRotation();
        double dYaw   = wrap(targetYaw   - look.y());
        double dPitch =      targetPitch - look.x();

        double k = Math.min(1, spring * deltaTime);
        double yawCorr   = dYaw   * k;
        double pitchCorr = dPitch * k;

        boolean clamped = false;
        if (Math.abs(dYaw   - yawCorr)   > maxDev) { yawCorr   = dYaw   - Math.signum(dYaw)   * maxDev; clamped = true; }
        if (Math.abs(dPitch - pitchCorr) > maxDev) { pitchCorr = dPitch - Math.signum(dPitch) * maxDev; clamped = true; }

        player.rotate(new Vector3D(pitchCorr, yawCorr, 0));

        if (clamped && !wasClamped) {                       // just hit the wall this frame
            double now = System.nanoTime() * 1e-9;
            if (now - lastTrigger > cooldown && onWallHit != null) {
                onWallHit.run();
                lastTrigger = now;
            }
        }
        wasClamped = clamped;
    }

    private static double wrap(double a) {
        while (a >  Math.PI) a -= 2*Math.PI;
        while (a < -Math.PI) a += 2*Math.PI;
        return a;
    }
}