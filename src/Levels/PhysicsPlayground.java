package Levels;

import Engine3d.Controls.CreativeCamera;
import Engine3d.Controls.EditorController;
import Engine3d.Controls.OldSchoolDungeonCameraControls;
import Engine3d.Lighting.LightSource;
import Engine3d.Model.Mesh;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Objects.Object3D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Scene;
import Math.Geometries.Box;
import Math.Geometries.MeshTriangle;
import Math.Vector.Vector3D;
import Physics.AABBCollisions.DynamicAABBObject;
import Physics.AABBCollisions.StaticAABBObject;
import Physics.PlayerObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test level for collision work. Stations are laid out along +X, {@link #SPACING} apart, and each one
 * isolates one physics behaviour. The player spawns in front of a station, facing +Z.
 * <p>
 * Launch with the program argument {@code level=playground}.
 * <p>
 * Controls: WASD move, Ctrl sprint, Space jump, F1 editor, F3 collider debug boxes, ` console.<br>
 * Console: {@code tp} lists the stations, {@code tp <number|name>} teleports to one,
 * {@code drop <height>} lifts you straight up, {@code reset} puts the crates back.
 */
public class PhysicsPlayground extends Scene
{
    private static final double SPACING = 24;
    private static final Vector3D SPAWN = new Vector3D(0, 0.5, -6);   // relative to a station's origin
    private static final double KILL_Y = -20;

    private static final Color GREY   = new Color(150, 150, 150);
    private static final Color BLUE   = new Color(70, 120, 220);
    private static final Color TEAL   = new Color(40, 170, 160);
    private static final Color GREEN  = new Color(80, 180, 80);
    private static final Color RED    = new Color(210, 70, 60);
    private static final Color ORANGE = new Color(230, 140, 40);
    private static final Color YELLOW = new Color(230, 210, 60);
    private static final Color PURPLE = new Color(150, 90, 200);
    private static final Color PINK   = new Color(220, 110, 170);

    private record Station(String name, Vector3D origin) { }

    private final PlayerObject player;
    private final List<Station> stations = new ArrayList<>();
    private final List<DynamicAABBObject> crates = new ArrayList<>();
    private final List<Vector3D> crateHomes = new ArrayList<>();

    public PhysicsPlayground(Camera camera) {
        super(camera);
        backgroundColour = new Color(96, 128, 160);

        player = new PlayerObject(this, (PlayerCamera) camera);
        setUpControls((PlayerCamera) camera);

        LightSource sun = new LightSource(this);
        sun.setRotation(new Vector3D(Math.toRadians(160), Math.toRadians(35), 0));
        sun.setLightIntensity(5);

        buildScaleStation(addStation("scale"));
        buildOffsetStation(addStation("offset"));
        buildWallStation(addStation("walls"));
        buildRampStation(addStation("ramps"));
        buildStepStation(addStation("steps"));
        buildJumpStation(addStation("jump"));
        buildCeilingStation(addStation("ceiling"));
        buildCornerStation(addStation("corners"));
        buildCrateStation(addStation("crates"));
        buildThinStation(addStation("thin"));
        buildConcaveStation(addStation("concave"));
        buildGateStation(addStation("gate"));
        buildFloor();

        registerCommands();
        addUpdatable(dt -> {
            if (player.getPosition().y() < KILL_Y) {
                teleport(nearestStation());
            }
        });

        teleport(0);
        getConsole().println("Physics playground. tp lists the stations, tp <n> teleports, drop <h>, reset.");
    }

    // === SETUP ===

    private void setUpControls(PlayerCamera camera) {
        OldSchoolDungeonCameraControls playerCtrl =
                new OldSchoolDungeonCameraControls(getSceneRenderer(), player);
        playerCtrl.isEnabled(true);
        addUpdatable(playerCtrl);

        // CreativeCamera is itself a PlayerObject, so it's a dynamic collider in the scene.
        // Park it up and out of the way so the player can't bump into it; the editor view starts here.
        CreativeCamera editorCam = new CreativeCamera(this, camera);
        editorCam.translate(new Vector3D(SPACING * 2, 12, -20));
        camera.setPlayerObject(player);   // editorCam's constructor took the camera; give it back

        EditorController editorCtrl = new EditorController(getSceneRenderer(), editorCam, this, camera);
        editorCtrl.isEnabled(false);
        addUpdatable(editorCtrl);
        setEditorUpdatable(editorCtrl);

        getSceneRenderer().addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_F1 -> {
                        boolean toEditor = !editorCtrl.isEnabled();
                        editorCtrl.isEnabled(toEditor);
                        playerCtrl.isEnabled(!toEditor);
                        setEditorMode(toEditor);
                        camera.setPlayerObject(toEditor ? editorCam : player);
                        if (!toEditor) {
                            setSelected(null);
                        }
                    }
                    // The shared controller has jumping switched off (LastGift doesn't jump), so add it here.
                    case KeyEvent.VK_SPACE -> {
                        if (!isEditorMode() && !getConsole().isOpen()) {
                            player.requestJump();
                        }
                    }
                }
            }
        });
    }

    private Vector3D addStation(String name) {
        Vector3D origin = new Vector3D(stations.size() * SPACING, 0, 0);
        stations.add(new Station(name, origin));
        return origin;
    }

    private void buildFloor() {
        double minX = -SPACING / 2 - 10;
        double maxX = (stations.size() - 1) * SPACING + SPACING / 2 + 10;
        BoxMesh mesh = new BoxMesh(new Box(new Vector3D(minX, -1, -20), new Vector3D(maxX, 0, 30)));
        try {
            BufferedImage tex = ImageIO.read(new File("Resources/Textures/Crimson Nylium.png"));
            mesh.setTexture(tex);
            mesh.setDiffuseColour(Color.white);
        }
        catch (IOException e) {
            paint(mesh, GREY);   // no texture, no motion cues, but still a floor
        }
        StaticAABBObject floor = new StaticAABBObject(this);
        floor.setMesh(mesh);
    }

    // === STATIONS ===

    /** Object scale. The white debug box (F3) and the yellow selection box (F1, click) should both hug each block. */
    private void buildScaleStation(Vector3D o) {
        double[] scales = {1, 2, 3};
        double[] xs = {-7, -2.5, 3.5};
        for (int i = 0; i < scales.length; i++) {
            double s = scales[i];
            block(footprint(1, 1, 1), BLUE, at(o, xs[i], 0, 4)).setScale(new Vector3D(s, s, s));
        }
        block(footprint(1, 1, 1), TEAL, at(o, 9, 0, 4)).setScale(new Vector3D(3, 0.5, 4));   // non-uniform
    }

    /** Mesh offset. Red reaches its spot through centreToMiddleBottom's mesh offset, green through its own points. */
    private void buildOffsetStation(Vector3D o) {
        BoxMesh offsetMesh = paint(new BoxMesh(new Vector3D(2, 2, 2)), RED);
        offsetMesh.centreToMiddleBottom();
        StaticAABBObject red = new StaticAABBObject(this);
        red.setMesh(offsetMesh);
        red.translate(at(o, -5, 0, 4));

        block(footprint(2, 2, 2), GREEN, at(o, 0, 0, 4));

        // Same as red, turned 45°. The offset is applied after rotation, so it turns around a corner,
        // not its centre. It should still collide exactly where it's drawn.
        BoxMesh turnedMesh = paint(new BoxMesh(new Vector3D(2, 2, 2)), RED);
        turnedMesh.centreToMiddleBottom();
        StaticAABBObject turned = new StaticAABBObject(this);
        turned.setMesh(turnedMesh);
        turned.rotate(deg(0, 45, 0));
        turned.translate(at(o, 5, 0, 4));
    }

    /** Walls turned 0°, 30°, 45° and 60° around Y. Walk into them at an angle; you should slide, not snag. */
    private void buildWallStation(Vector3D o) {
        double[] angles = {0, 30, 45, 60};
        double[] xs = {-8, -3, 2, 7};
        for (int i = 0; i < angles.length; i++) {
            block(footprint(4, 3, 0.4), ORANGE, at(o, xs[i], 0, 5), deg(0, angles[i], 0));
        }
    }

    /** Ramps at 10°, 20°, 30°, 45° and 60°, pivoting on their low edge, which sits flush with the floor. */
    private void buildRampStation(Vector3D o) {
        double[] angles = {10, 20, 30, 45, 60};
        Color[] colours = {GREEN, TEAL, BLUE, PURPLE, RED};
        for (int i = 0; i < angles.length; i++) {
            Box slab = new Box(new Vector3D(-1.5, -0.4, 0), new Vector3D(1.5, 0, 7));
            block(slab, colours[i], at(o, -8 + 4 * i, 0, 1), deg(-angles[i], 0, 0));
        }
    }

    /** Single steps 0.1, 0.25, 0.5, 0.75 and 1.0 high. Try walking onto each without jumping. */
    private void buildStepStation(Vector3D o) {
        double[] heights = {0.1, 0.25, 0.5, 0.75, 1.0};
        for (int i = 0; i < heights.length; i++) {
            block(footprint(3, heights[i], 3), YELLOW, at(o, -8 + 4 * i, 0, 3));
        }
    }

    /** Platforms 1, 1.5, 2, 2.5 and 3 high: how high can you jump, and can you land on the edges? */
    private void buildJumpStation(Vector3D o) {
        double[] heights = {1, 1.5, 2, 2.5, 3};
        for (int i = 0; i < heights.length; i++) {
            block(footprint(2.5, heights[i], 2.5), TEAL, at(o, -8 + 4 * i, 0, 3));
        }
    }

    /** Ceilings with their underside at 2.0, 2.6 and 3.4 (the player is 1.8 tall). Jump into them. */
    private void buildCeilingStation(Vector3D o) {
        double[] bottoms = {2.0, 2.6, 3.4};
        double[] xs = {-7, 0, 7};
        for (int i = 0; i < bottoms.length; i++) {
            block(footprint(5, 0.4, 5), PURPLE, at(o, xs[i], bottoms[i], 3));
        }
    }

    /** A 90° inside corner, a 30° wedge, and corridors 1.2 and 1.05 wide (the player is 1.0 wide). */
    private void buildCornerStation(Vector3D o) {
        // L-shaped inside corner
        block(footprint(6, 2.5, 0.3), PURPLE, at(o, -7, 0, 7));
        block(footprint(0.3, 2.5, 6), PURPLE, at(o, -10, 0, 4.15));

        // Wedge: two walls meeting at a point at z = 9, opening towards the spawn
        Box wedgeWall = new Box(new Vector3D(-0.15, 0, -6), new Vector3D(0.15, 2.5, 0));
        block(wedgeWall, PINK, at(o, 0, 0, 9), deg(0, 15, 0));
        block(wedgeWall, PINK, at(o, 0, 0, 9), deg(0, -15, 0));

        // Corridors. The tight one is wider than the player's box, but not once you turn
        // (a 1x1 footprint turned 45° is 1.41 across).
        block(footprint(0.3, 2.5, 6), ORANGE, at(o, 5.25, 0, 5));
        block(footprint(0.3, 2.5, 6), ORANGE, at(o, 6.75, 0, 5));
        block(footprint(0.3, 2.5, 6), RED, at(o, 9.325, 0, 5));
        block(footprint(0.3, 2.5, 6), RED, at(o, 10.675, 0, 5));
    }

    /** Pushable crates weighing 0.25, 1 and 9 (the player weighs 1), plus a row of three to push into each other. */
    private void buildCrateStation(Vector3D o) {
        crate(YELLOW, 0.25, at(o, -6, 0, 4));
        crate(ORANGE, 1, at(o, -2, 0, 4));
        crate(RED, 9, at(o, 2, 0, 4));

        crate(BLUE, 1, at(o, 7, 0, 3));
        crate(BLUE, 1, at(o, 7, 0, 4.1));
        crate(BLUE, 1, at(o, 7, 0, 5.2));
    }

    /**
     * Walls 0.05, 0.2 and 0.5 thick to sprint into, and floating slabs of the same thicknesses at height 3
     * behind them. Stand under a slab and type {@code drop 40} to test falling through thin floors.
     */
    private void buildThinStation(Vector3D o) {
        double[] thickness = {0.05, 0.2, 0.5};
        double[] xs = {-7, 0, 7};
        for (int i = 0; i < thickness.length; i++) {
            block(footprint(4, 3, thickness[i]), GREY, at(o, xs[i], 0, 5));
            block(footprint(4, thickness[i], 4), BLUE, at(o, xs[i], 3, 12));
        }
    }

    /**
     * Non-convex shapes, each a single collider: a U, an L and an arch. You should be able to walk into
     * the U and under the arch, but each collides as its convex hull, so you can't (yet).
     */
    private void buildConcaveStation(Vector3D o) {
        BlockMesh u = new BlockMesh(GREEN,
                new Box(new Vector3D(-2.5, 0, -1.5), new Vector3D(-1.5, 2, 2.5)),
                new Box(new Vector3D(1.5, 0, -1.5), new Vector3D(2.5, 2, 2.5)),
                new Box(new Vector3D(-1.5, 0, 1.5), new Vector3D(1.5, 2, 2.5)));
        staticMesh(u, at(o, -7, 0, 4));

        BlockMesh l = new BlockMesh(ORANGE,
                new Box(new Vector3D(-2, 0, 1), new Vector3D(2, 1.5, 2)),
                new Box(new Vector3D(-2, 0, -2), new Vector3D(-1, 1.5, 1)));
        staticMesh(l, at(o, 0, 0, 4));

        BlockMesh arch = new BlockMesh(PURPLE,
                new Box(new Vector3D(-2, 0, -0.5), new Vector3D(-1.4, 2.2, 0.5)),
                new Box(new Vector3D(1.4, 0, -0.5), new Vector3D(2, 2.2, 0.5)),
                new Box(new Vector3D(-2, 2.2, -0.5), new Vector3D(2, 2.6, 0.5)));
        staticMesh(arch, at(o, 7, 0, 4));
    }

    /** The LastGift gate as one collider, with no hand-placed boxes. Its hull fills in the opening. */
    private void buildGateStation(Vector3D o) {
        staticModel("Resources/Models/Gate", "gate.obj", at(o, 0, 0, 6), deg(0, 180, 0), 280);
    }

    // === HELPERS ===

    private static DrawInstructions flat() {
        return new DrawInstructions(false, true, false, true);
    }

    private static BoxMesh paint(BoxMesh mesh, Color colour) {
        mesh.setDiffuseColour(colour);
        mesh.setDrawInstructions(flat());
        return mesh;
    }

    /** A box whose local origin is the centre of its bottom face, so rotation and scale pivot there. */
    private static Box footprint(double sx, double sy, double sz) {
        return new Box(new Vector3D(-sx / 2, 0, -sz / 2), new Vector3D(sx / 2, sy, sz / 2));
    }

    private static Vector3D at(Vector3D origin, double x, double y, double z) {
        return origin.translated(new Vector3D(x, y, z));
    }

    private static Vector3D deg(double x, double y, double z) {
        return new Vector3D(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    private StaticAABBObject block(Box local, Color colour, Vector3D pos) {
        return block(local, colour, pos, new Vector3D(0, 0, 0));
    }

    private StaticAABBObject block(Box local, Color colour, Vector3D pos, Vector3D rot) {
        StaticAABBObject obj = new StaticAABBObject(this);
        obj.setMesh(paint(new BoxMesh(local), colour));
        obj.rotate(rot);
        obj.translate(pos);
        return obj;
    }

    private StaticAABBObject staticMesh(Mesh mesh, Vector3D pos) {
        StaticAABBObject obj = new StaticAABBObject(this);
        obj.setMesh(mesh);
        obj.translate(pos);
        return obj;
    }

    private void crate(Color colour, double weight, Vector3D pos) {
        DynamicAABBObject obj = new DynamicAABBObject(this);
        obj.setMesh(paint(new BoxMesh(footprint(1, 1, 1)), colour));
        obj.getAABBCollider().setWeight(weight);
        obj.translate(pos);
        crates.add(obj);
        crateHomes.add(pos);
    }

    /**
     * Loads an OBJ model as a static collider. loadFromFile always creates a plain Object3D, and ObjParser
     * hands it an empty mesh before filling it in, which a collider can't be built from. So this loads into
     * a throwaway object and moves the finished mesh onto a StaticAABBObject.
     */
    private void staticModel(String folder, String file, Vector3D pos, Vector3D rot, double scale) {
        Object3D temp = loadFromFile(folder, file);
        removeObject(temp);
        Mesh mesh = temp.getMesh();
        if (mesh == null || mesh.getPoints().isEmpty()) {
            getConsole().println("Playground: couldn't load " + folder + "/" + file);
            return;
        }
        StaticAABBObject obj = new StaticAABBObject(this);
        obj.setMesh(mesh);
        obj.setScale(new Vector3D(scale, scale, scale));
        obj.rotate(rot);
        obj.translate(pos);
    }

    // === TELEPORTING & COMMANDS ===

    private void teleport(int index) {
        Vector3D target = stations.get(index).origin().translated(SPAWN);
        player.translate(target.translated(player.getPosition().inverted()));
        player.rotate(new Vector3D(0, -player.getRotation().y(), 0));   // face +Z
        player.resetMomentum();
    }

    private int nearestStation() {
        int i = (int) Math.round(player.getPosition().x() / SPACING);
        return Math.max(0, Math.min(stations.size() - 1, i));
    }

    private int findStation(String key) {
        try {
            int i = Integer.parseInt(key);
            return (i >= 0 && i < stations.size()) ? i : -1;
        }
        catch (NumberFormatException e) {
            for (int i = 0; i < stations.size(); i++) {
                if (stations.get(i).name().equalsIgnoreCase(key)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private void resetCrates() {
        for (int i = 0; i < crates.size(); i++) {
            DynamicAABBObject c = crates.get(i);
            c.translate(crateHomes.get(i).translated(c.getPosition().inverted()));
        }
    }

    private void registerCommands() {
        getConsole().registerCommand("tp", args -> {
            if (args.length < 2) {
                for (int i = 0; i < stations.size(); i++) {
                    getConsole().println("  " + i + "  " + stations.get(i).name());
                }
                return;
            }
            int index = findStation(args[1]);
            if (index < 0) {
                getConsole().println("No station '" + args[1] + "'. Type tp to list them.");
                return;
            }
            enqueueEdit(() -> teleport(index));
        });

        getConsole().registerCommand("drop", args -> {
            double height = args.length < 2 ? 20 : Double.parseDouble(args[1]);
            enqueueEdit(() -> {
                player.translate(Vector3D.UP().scaled(height));
                player.resetMomentum();
            });
        });

        getConsole().registerCommand("reset", args -> enqueueEdit(this::resetCrates));
    }

    /** One mesh built from several boxes. As a single collider it collides like its convex hull. */
    private static class BlockMesh extends Mesh
    {
        // Same corner order and triangle winding as BoxMesh, so back-face culling agrees with it.
        private static final int[][] TRIANGLES = {
                {0, 1, 2}, {0, 2, 3}, {7, 6, 5}, {7, 5, 4}, {3, 2, 6}, {3, 6, 7},
                {4, 5, 1}, {4, 1, 0}, {1, 5, 6}, {1, 6, 2}, {3, 7, 4}, {3, 4, 0}
        };

        BlockMesh(Color colour, Box... boxes) {
            setDrawInstructions(flat());
            for (Box b : boxes) {
                addBox(b, colour);
            }
            recomputeLocalBounds();
        }

        private void addBox(Box b, Color colour) {
            Vector3D lo = b.min();
            Vector3D hi = b.max();
            Vector3D[] p = {
                    new Vector3D(lo.x(), lo.y(), lo.z()),
                    new Vector3D(lo.x(), hi.y(), lo.z()),
                    new Vector3D(hi.x(), hi.y(), lo.z()),
                    new Vector3D(hi.x(), lo.y(), lo.z()),
                    new Vector3D(lo.x(), lo.y(), hi.z()),
                    new Vector3D(lo.x(), hi.y(), hi.z()),
                    new Vector3D(hi.x(), hi.y(), hi.z()),
                    new Vector3D(hi.x(), lo.y(), hi.z()),
            };
            for (Vector3D v : p) {
                points.add(v);
            }
            for (int[] t : TRIANGLES) {
                MeshTriangle tri = new MeshTriangle(p[t[0]], p[t[1]], p[t[2]]);
                tri.getMaterial().setBaseColour(colour);
                faces.add(tri);
            }
        }
    }
}
