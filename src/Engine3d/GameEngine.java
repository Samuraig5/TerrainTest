package Engine3d;

import Engine3d.Controls.Controller;
import Engine3d.DevTools.Console;
import Engine3d.DevTools.Profiler;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.RenderItem;
import Engine3d.Rendering.RenderPipeline;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Time.Updatable;
import Levels.Persistence.LevelIO;
import Physics.PhysicsSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Camera camera;
    private final SceneRenderer renderer;
    private final GameLoop loop;
    private final RenderPipeline renderPipeline = new RenderPipeline();
    private final PhysicsSystem  physicsSystem  = new PhysicsSystem();
    private final Object stateLock = new Object();

    private final Console console = new Console(this);
    private final List<Controller> consoleSuspended = new ArrayList<>();

    private Scene activeScene;

    private final java.util.concurrent.ConcurrentLinkedQueue<Runnable> editCommands =
            new java.util.concurrent.ConcurrentLinkedQueue<>();

    public void enqueueEdit(Runnable r) {
        editCommands.add(r);
    }

    public GameEngine(Scene scene) {
        this.activeScene = scene;
        this.camera = scene.getCamera();
        this.renderer = scene.getSceneRenderer();

        renderer.addKeyListener(console);
        setUpConsole();

        scene.setEngine(this);
        this.loop = new GameLoop(this);
        loop.start();
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public Camera getCamera() {
        return camera;
    }

    public GameLoop getLoop() {
        return loop;
    }

    public PhysicsSystem getPhysics() {
        return physicsSystem;
    }

    public void renderFrame() {
        if (activeScene == null) return;
        List<RenderItem> frame;
        synchronized (stateLock) {
            frame = activeScene.snapshotObjects();
        }
        try (Profiler.Span s = Profiler.span("buildScreenBuffer")) {
            renderPipeline.build(camera, activeScene, frame);
        }
        Profiler.count("buffers");
        synchronized (camera) {
            camera.swapBuffers();
        }
        renderer.repaint();
    }

    public void tick(double dt) {
        if (activeScene == null) return;
        try (Profiler.Span s = Profiler.span("update")) {
            synchronized (stateLock) {
                Runnable cmd;
                while ((cmd = editCommands.poll()) != null) cmd.run();

                if (activeScene.isEditorMode()) {
                    Updatable eu = activeScene.getEditorUpdatable();
                    if (eu != null) eu.update(dt);
                } else {
                    activeScene.tickScripts(dt);
                    physicsSystem.step(activeScene, dt);
                }
            }
        }
        Profiler.count("updates");
    }

    public void setConsoleOpen(boolean open) {
        if (open) {
            consoleSuspended.clear();
            for (Updatable u : activeScene.updatables) {
                if (u instanceof Controller c && c.isEnabled()) {
                    c.isEnabled(false);
                    consoleSuspended.add(c);
                }
            }
        }
        else {
            for (Controller c : consoleSuspended) {
                c.isEnabled(true);
            }
            consoleSuspended.clear();
        }
    }

    private void setUpConsole() {
        console.registerCommand("save",
                args -> {
                    if (args.length < 2) {
                        console.println("usage: save <fileName>");
                        return;
                    }
                    String path = "Levels/saved/" + args[1] + ".txt";
                    enqueueEdit(() -> {
                        try {
                            LevelIO.save(LevelIO.snapshot(activeScene), path);
                            console.println("Saved " + path);
                        }
                        catch (IOException e) {
                            console.println("Save failed: " + e.getMessage());
                        }
                    });
                });

        console.registerCommand("load",
                args -> {
                    if (args.length < 2) {
                        console.println("usage: load <fileName>");
                        return;
                    }
                    String path = "Levels/saved/" + args[1] + ".txt";
                    enqueueEdit(() -> {
                        try {
                            LevelIO.restore(LevelIO.load(path), activeScene);
                            console.println("Loaded " + path);
                        }
                        catch (IOException e) {
                            console.println("Load failed: " + e.getMessage());
                        }
                    });
                });
    }

    public Console getConsole() {
        return console;
    }
}