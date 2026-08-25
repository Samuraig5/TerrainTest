package Engine3d.Rendering.Skyboxes;

import Engine3d.Time.Updatable;

public class EyeAwakening implements Updatable {
    private final NightSkyBox sky; private final double duration;
    private double t = 0; private boolean running = false;
    public EyeAwakening(NightSkyBox sky, double duration) { this.sky = sky; this.duration = duration; }
    public void start() { running = true; }
    @Override public void update(double dt) {
        if (!running) return;
        t = Math.min(1, t + dt / duration);
        sky.setGaze(t*t*(3 - 2*t));      // smoothstep for a slow, dreadful turn
    }
}
