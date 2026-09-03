package Engine3d;

import Engine3d.Rendering.SceneRenderer;

public class GameLoop {
    private final GameEngine engine;

    private Thread updateThread;
    private Thread bufferThread;
    private volatile boolean running = true;
    private long lastTime;

    public GameLoop(GameEngine engine) {
        this.engine = engine;
    }

    public void start() {
        startBuildThread();
        startUpdateThread();
    }

    public void stop() {
        running = false;
        if (bufferThread != null) bufferThread.interrupt();
        if (updateThread != null) updateThread.interrupt();
    }

    private void startBuildThread() {
        bufferThread = new Thread(() -> {
            final long targetPeriod = 16_666_667L; // ns for 60 FPS
            while (!Thread.currentThread().isInterrupted()) {
                long frameStart = System.nanoTime();
                engine.renderFrame();
                long remaining = targetPeriod - (System.nanoTime() - frameStart);
                if (remaining > 0) {
                    try { Thread.sleep(remaining / 1_000_000, (int) (remaining % 1_000_000)); }
                    catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                }
            }
        });
        bufferThread.start();
    }

    private void startUpdateThread() {
        updateThread = new Thread(() -> {
            while (running) {
                try { engine.tick(deltaTime()); }
                catch (Exception e) { System.err.println(e.getMessage()); }
                try { Thread.sleep(16); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            }
        });
        updateThread.start();
    }

    private double deltaTime() {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;
        return dt;
    }
}
