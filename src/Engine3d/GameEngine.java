package Engine3d;

import Engine3d.Controls.Controller;
import Engine3d.Controls.EditorController;
import Engine3d.DevTools.Console;
import Engine3d.DevTools.Profiler;
import Engine3d.Objects.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.RenderItem;
import Engine3d.Rendering.RenderPipeline;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Time.Updatable;
import Levels.Persistence.LevelIO;
import Levels.Utils.LevelBuilder;
import Physics.PhysicsSystem;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

    private EditorController editorController;
    private volatile boolean editorMode = false;

    private Scene activeScene;

    private final java.util.concurrent.ConcurrentLinkedQueue<Runnable> editCommands =
            new java.util.concurrent.ConcurrentLinkedQueue<>();

    public void enqueueEdit(Runnable r) {
        editCommands.add(r);
    }

    public GameEngine(Camera camera) {
        this.camera = camera;
        this.renderer = new SceneRenderer();

        camera.getFrame().add(renderer);
        renderer.grabFocus();
        camera.getFrame().repaint();

        renderer.addKeyListener(console);
        setUpConsole();

        renderer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F3) {
                    camera.debugging = !camera.debugging;
                }
            }
        });

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

    public SceneRenderer getRenderer() {
        return renderer;
    }

    public void loadLevel(LevelBuilder builder) {
        Scene scene = builder.build(this);
        scene.setEngine(this);
        this.activeScene = scene;
        renderer.setActiveScene(scene);
    }

    public void renderFrame() {
        if (activeScene == null) return;
        List<RenderItem> frame;
        synchronized (stateLock) {
            frame = activeScene.snapshotObjects();
        }
        try (Profiler.Span s = Profiler.span("buildScreenBuffer")) {
            renderPipeline.build(camera, activeScene, frame, getSelected());
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

                if (editorMode) {
                    if (editorController != null) editorController.update(dt);
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
            for (Updatable u : activeScene.getUpdatables()) {
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

    public void setEditorController(EditorController e) {
        this.editorController = e;
    }
    public boolean isEditorMode() {
        return editorMode;
    }
    public void setEditorMode(boolean b) {
        this.editorMode = b;
    }
    public Object3D getSelected() {
        return editorController == null ? null : editorController.getSelected();
    }
}