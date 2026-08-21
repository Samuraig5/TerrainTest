package Levels.Utils;

import Engine3d.Time.Updatable;
import java.util.ArrayList;
import java.util.List;

public class Timeline implements Updatable {
    private record Step(double time, Runnable action) {}
    private final List<Step> steps = new ArrayList<>();
    private double clock = 0; private int next = 0; private boolean running = false;

    public Timeline at(double time, Runnable action) { steps.add(new Step(time, action)); return this; }
    public void start() { clock = 0; next = 0; running = true; }
    @Override public void update(double dt) {
        if (!running) return;
        clock += dt;
        while (next < steps.size() && clock >= steps.get(next).time()) {
            steps.get(next).action().run();
            next++;
        }
        if (next >= steps.size()) running = false;
    }
}
