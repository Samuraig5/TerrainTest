package Levels;

import Engine3d.Audio.Sound;
import Engine3d.Controls.CreativeCamera;
import Engine3d.Controls.EditorController;
import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Model.BillboardScatterMesh;
import Engine3d.Model.ChunkPopulator;
import Engine3d.Model.FloorFollower;
import Engine3d.Model.ScatterChunkManager;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.Filters.*;
import Engine3d.Rendering.Skyboxes.EyeAwakening;
import Engine3d.Rendering.Skyboxes.NightSkyBox;
import Levels.Utils.GazeLock;
import Levels.Utils.Timeline;
import Engine3d.Objects.Object3D;
import Physics.PlayerObject;
import Engine3d.Lighting.LightSource;
import Math.Vector.Vector3D;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Physics.AABBCollisions.StaticAABBObject;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Physics.Triggers.TriggerZone;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LastGiftLevel extends Scene
{
    private PlayerObject player;
    private GlitchFilter glitch;

    private EyeAwakening awakening;
    private GazeLock gaze;

    public LastGiftLevel(Camera camera) {
        super(camera);

        backgroundColour = new Color(73, 0, 0);

        player = new PlayerObject(this, (PlayerCamera) camera);
        player.translate(new Vector3D(0,2,0));

        // --- PLAYER mode: existing gameplay controls ---
        OldSchoolDungeonCameraControls playerCtrl =
                new OldSchoolDungeonCameraControls(getSceneRenderer(), player);
        playerCtrl.isEnabled(true);
        addUpdatable(playerCtrl);

        // --- EDITOR mode: fly-cam mover + editing overlay ---
        CreativeCamera editorCam = new CreativeCamera(this, (PlayerCamera) camera);
        ((PlayerCamera) camera).setPlayerObject(player);   // editorCam's ctor stole the camera; give it back

        EditorController editorCtrl =
                new EditorController(getSceneRenderer(), editorCam, this, camera); // mover + rendering cam
        editorCtrl.isEnabled(false);
        addUpdatable(editorCtrl);
        setEditorUpdatable(editorCtrl);

        // --- toggle ---
        getSceneRenderer().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() != java.awt.event.KeyEvent.VK_F1) {
                    return;
                }
                boolean toEditor = !editorCtrl.isEnabled();
                editorCtrl.isEnabled(toEditor);
                playerCtrl.isEnabled(!toEditor);
                setEditorMode(toEditor);
                ((PlayerCamera) camera).setPlayerObject(toEditor ? editorCam : player);
                if (!toEditor) {
                    setSelected(null);
                }
            }
        });

        OldSchoolDungeonCameraControls cameraController = new OldSchoolDungeonCameraControls(getSceneRenderer(), player);
        cameraController.isEnabled(false);

        addUpdatable(cameraController);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(160),0,0));
        sun.setLightIntensity(5);

        //new HeadLight(camera, this);

        try {
            BufferedImage cimsonImg = ImageIO.read(new File("Resources/Textures/Crimson Nylium.png"));
            BufferedImage roseImg = ImageIO.read(new File( "Resources/Textures/Rose.png"));
            BufferedImage signImg = ImageIO.read(new File("Resources/Textures/The White Sign.png"));

            NightSkyBox sky = new NightSkyBox(new Vector3D(0,0.28,1),
                    12, 500, 1234L, signImg, 75);
            setSkyBox(sky);
            double floorSize = 50;
            StaticAABBObject ground = spawnWall(cimsonImg, new Vector3D(floorSize, 1, floorSize));
            ground.translate(Vector3D.DOWN().scaled(0.5));

            FloorFollower floorFollower = new FloorFollower(ground, player, 1.0);
            addUpdatable(floorFollower);

            ChunkPopulator flowers = (cx, cz, size, out) -> {
                long seed = ((long) cx * 73856093L) ^ ((long) cz * 19349663L);
                Random rng = new Random(seed);
                for (int i = 0; i < 40; i++) {
                    double fx = cx * size + rng.nextDouble() * size;
                    double fz = cz * size + rng.nextDouble() * size;
                    double w  = 0.4 + rng.nextDouble() * 0.2;
                    double h  = 0.6 + rng.nextDouble() * 0.3;
                    out.add(new BillboardScatterMesh.Instance(new Vector3D(fx, 0.7, fz), w, h));
                }
            };

            ScatterChunkManager flowerChunks =
                    new ScatterChunkManager(this, player, roseImg, 3, 25, flowers,
                            new BillboardScatterMesh.Wind(
                                    new Vector3D(1,0,0.25).normalized(),
                                    0.12,0.5,1
                            )
                    );
            addUpdatable(flowerChunks);

            glitch = new GlitchFilter();
            addFilter(glitch);

            WakeUpFilter wake = new WakeUpFilter(
                    ()->{
                        cameraController.isEnabled(true);
                        getSceneRenderer().addKeyListener(glitch);
                        Clip music = Sound.playLoop("Resources/Audio/Hymn of the Cherubim.wav");
                    }
            );
            addFilter(wake);

            awakening = new EyeAwakening(sky, 10.0);   // 10s turn
            addUpdatable(awakening);

            gaze = new GazeLock(player, new Vector3D(0, -0.2, 1),
                    18, 0.5);
            gaze.setOnWallHit(()->glitch.trigger(1,0.45));
            addUpdatable(gaze);

            spawnGate(new Vector3D(0,0,200));
            spawnObelisks(new Vector3D(0,0,10));

            getSceneRenderer().addKeyListener(wake);   // renderer already holds keyboard focus
        }
        catch (IOException e1) {
            getConsole().println("Can't instantiate level: " + e1.getMessage());
        }
    }

    private void spawnGate(Vector3D gateLocation) {
        List<Object3D> gateParts = new ArrayList<>();
        Object3D gate = loadFromFile("Resources/Models/Gate", "gate.obj");
        gate.rotate(new Vector3D(0,Math.toRadians(180),0));
        gate.setScale(new Vector3D(280, 280, 280));
        gate.translate(gateLocation);

        gateParts.add(gate);
        gateParts.add(spawnCollider(new Vector3D(1.0, 5, 1.0), gateLocation.translated(new Vector3D(3,0,0.3))));
        gateParts.add(spawnCollider(new Vector3D(0.5, 5, 0.5), gateLocation.translated(new Vector3D(5.35,0,0.3))));
        gateParts.add(spawnCollider(new Vector3D(1, 2.5, 1), gateLocation.translated(new Vector3D(5.35,0,-2.3))));

        gateParts.add(spawnCollider(new Vector3D(1.0, 5, 1.0), gateLocation.translated(new Vector3D(-3,0,0.3))));
        gateParts.add(spawnCollider(new Vector3D(0.5, 5, 0.5), gateLocation.translated(new Vector3D(-5.35,0,0.3))));
        gateParts.add(spawnCollider(new Vector3D(1, 2.5, 1), gateLocation.translated(new Vector3D(-5.35,0,-2.3))));

        GlitchFilter textGlitch = new GlitchFilter();
        BlackScreenText blackText = new BlackScreenText().effect(textGlitch);
        addFilter(blackText);

        Timeline gateSeq = new Timeline()
                .at(0.0, () -> {
                    glitch.trigger(3.0, 1.4);
                    Sound.play("Resources/Audio/Digital Error.wav", 0.1);
                    })
                .at(0.7, () -> {
                    Sound.play("Resources/Audio/Error Buzz.wav", 0.1);
                    blackText.show(BlackScreenText.builder().add("WAKE UP", new Color(255, 0, 0)).build());
                    textGlitch.trigger(2.5,3.8);
                })
                .at(1.4, () -> {                                   // world swap — hidden behind full black
                    for (Object3D p : gateParts) removeObject(p);
                    Vector3D spawn = new Vector3D(0, 1, 0);
                    player.translate(spawn.translated(player.getPosition().inverted()));
                    player.rotate(player.getRotation().inverted().translated(new Vector3D(0,Math.toRadians(180),0)));
                    spawnTemple(new Vector3D(0,0,-200));
                })
                .at(4.5, blackText::hide);                          // reveal the temple world
        addUpdatable(gateSeq);

        TriggerZone progressionZone = new TriggerZone(
                TriggerZone.box(gateLocation.translated(new Vector3D(0,2,0.25)),
                        new Vector3D(5,4,1)),
                player).onEnter(()-> gateSeq.start());

        addUpdatable(progressionZone);
    }

    private void spawnObelisks(Vector3D obeliskLocation) {
        spawnObelisk(obeliskLocation, new Vector3D(0,0,0), new Vector3D(1,1,1));

        spawnObelisk(obeliskLocation.translated(
                new Vector3D(100,-10,10)),
                new Vector3D(Math.toRadians(10),Math.toRadians(45),Math.toRadians(15)),
                new Vector3D(10,10,10));
        spawnObelisk(obeliskLocation.translated(
                new Vector3D(80,-15,-40)),
                new Vector3D(Math.toRadians(20),Math.toRadians(20),Math.toRadians(5)),
                new Vector3D(8,8,8));
        spawnObelisk(obeliskLocation.translated(
                new Vector3D(-25,-10,50)),
                new Vector3D(Math.toRadians(15),Math.toRadians(10),Math.toRadians(15)),
                new Vector3D(5,5,5));
        spawnObelisk(obeliskLocation.translated(
                new Vector3D(-120,-10,70)),
                new Vector3D(Math.toRadians(-20),Math.toRadians(40),Math.toRadians(-15)),
                new Vector3D(10,10,10));
    }
    private void spawnObelisk(Vector3D location, Vector3D rotation, Vector3D scale) {
        Object3D obelisk = loadFromFile("Resources/Models/Obelisk", "Obelisk1_0001.obj");
        obelisk.rotate(rotation);
        obelisk.setScale(scale);
        obelisk.translate(location);
    }

    private void spawnTemple(Vector3D templeLocation) {
        Object3D temple = loadFromFile("Resources/Models/Temple", "BloodTemple.obj");
        temple.translate(templeLocation);

        spawnCollider(new Vector3D(1.5, 5, 1.5), templeLocation); //Statue
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(5.1,0,2.25)));
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(5.1,0,-2.25)));
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(-5.1,0,2.25)));
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(-5.1,0,-2.25)));

        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(2.25,0,5.1)));
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(2.25,0,-5.1)));
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(-2.25,0,5.1)));
        spawnCollider(new Vector3D(0.5, 10, 0.5), templeLocation.translated(new Vector3D(-2.25,0,-5.1)));

        addFilter(new CensorFilter(getCamera(), templeLocation.translated(new Vector3D(-0.1, 4.25, 0.5)), 0.3, 0.3));

        PopupFilter errors = new PopupFilter();
        addFilter(errors);

        Timeline templeSeq = new Timeline()
                .at(0.0, () -> {
                    glitch.trigger(1.0, 0.5);
                })
                .at(1, () -> {
                    glitch.trigger(3.0, 1.5);
                })
                .at(5, () -> {
                    glitch.trigger(10.0, 2);
                })
                .at(8, () -> {
                    gaze.setActive(true);
                })
                .at(10, () -> {
                    awakening.start();
                    glitch.trigger(10.0, 1);
                })
                .at(23, () -> {
                    glitch.trigger(10.0, 30.0);
                    Sound.play("Resources/Audio/Error Buzz.wav", 0.1);
                })
                .at(23.3, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(23.6, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(23.9, () -> {
                    errors.add("HELP", new Color(255, 0, 0));
                })
                .at(24.2, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(24.4, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(24.6, () -> {
                    errors.add("PLEASE", new Color(255, 0, 0));
                })
                .at(24.7, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(24.8, () -> {
                    errors.add("WHY", new Color(255, 0, 0));
                })
                .at(24.9, () -> {
                    errors.add("WHY", new Color(255, 0, 0));
                })
                .at(25, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(25.1, () -> {
                    errors.add("HELL", new Color(255, 0, 0));
                    errors.add("GOD", new Color(255, 0, 0));
                    errors.add("WHY", new Color(255, 0, 0));
                })
                .at(25.2, () -> {
                    errors.add("HOPELESS", new Color(255, 0, 0));
                    errors.add("MOTHER", new Color(255, 0, 0));
                    errors.add("HELP", new Color(255, 0, 0));
                    errors.add("WHY", new Color(255, 0, 0));
                    errors.add("HELL", new Color(255, 0, 0));
                })
                .at(25.3, () -> {
                    errors.add("DIE", new Color(255, 0, 0));
                    errors.add("HELP", new Color(255, 0, 0));
                    errors.add("WHY", new Color(255, 0, 0));
                    errors.add("HELL", new Color(255, 0, 0));
                    errors.add("HELL", new Color(255, 0, 0));
                    errors.add("HELL", new Color(255, 0, 0));
                    errors.add("NO", new Color(255, 255, 255));
                })
                .at(25.4, () -> {
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                })
                .at(25.5, () -> {
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                    errors.add("NO", new Color(255, 255, 255));
                })
                .at(26, () -> {
                    Sound.play("Resources/Audio/Error Buzz.wav", 0.1);
                })
                .at(29, () -> {
                    Sound.play("Resources/Audio/Error Buzz.wav", 0.1);
                })
                .at(32, () -> {
                    Sound.play("Resources/Audio/Error Buzz.wav", 0.1);
                })
                .at(35, () -> {
                    getSceneRenderer().stopThreads();
                    getCamera().getFrame().dispose();
                    System.exit(0);
                });

        for (double i = 25.6; i < 35; i += 0.1) {
            templeSeq.at(i, () -> {
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
                errors.add("NO", new Color(255, 255, 255));
            });
        }

        addUpdatable(templeSeq);

        TriggerZone endZone = new TriggerZone(
                TriggerZone.box(templeLocation,
                        new Vector3D(6,4,6)),
                player).onEnter(templeSeq::start);

        addUpdatable(endZone);
    }


    private StaticAABBObject spawnWall(BufferedImage sprite, Vector3D size) {
        StaticAABBObject wall = new StaticAABBObject(this);
        BoxMesh boxMesh = new BoxMesh(size);
        wall.setMesh(boxMesh);
        boxMesh.setTexture(sprite);
        boxMesh.setDiffuseColour(Color.white);
        boxMesh.getDrawInstructions().drawWireFrame = false;
        boxMesh.centreToMiddleBottom();
        return wall;
    }

    private StaticAABBObject spawnCollider(Vector3D size, Vector3D pos) {
        StaticAABBObject c = new StaticAABBObject(this);
        BoxMesh box = new BoxMesh(size);
        c.setMesh(box);                                                  // builds the collider
        box.setDrawInstructions(new DrawInstructions(false, false, false, false)); // invisible
        box.centreToMiddleBottom();
        c.translate(pos);
        return c;
    }
}
