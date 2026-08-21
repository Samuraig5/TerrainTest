package Engine3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import Engine3d.DevTools.Profiler;
import Engine3d.Model.SimpleMeshes.BoxMesh;
import Engine3d.Model.SimpleMeshes.CubeMesh;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.Filters.ScreenFilter;
import Engine3d.Rendering.SceneRenderer;
import Engine3d.Rendering.ScreenDrawing.TileRasterizer;
import Engine3d.Rendering.SkyBox;
import Math.Raycast.Ray;
import Math.Raycast.RayCollision;
import Math.Raycast.RayTriangle;
import Physics.AABBCollisions.AABB;
import Physics.AABBCollisions.AABBObject;
import Physics.AABBCollisions.DynamicAABBObject;
import Physics.AABBCollisions.StaticAABBObject;
import Physics.GJK_EPA.GJK;
import Physics.Gravitational;
import Engine3d.Lighting.LightSource;
import Math.Matrix4x4;
import Math.Vector.Vector3D;
import Math.MeshTriangle;
import Engine3d.Model.ObjParser;
import Physics.Object3D;
import Engine3d.Time.Updatable;
import Engine3d.Model.Mesh;
import Physics.Triggers.TriggerZone;
import Math.Box;

public class Scene implements Updatable
{
    private final ObjParser objParser = new ObjParser();
    Camera camera;
    final SceneRenderer sceneRenderer = new SceneRenderer();
    protected Color backgroundColour = Color.BLACK;
    protected List<Object3D> objects = new CopyOnWriteArrayList<>();
    List<LightSource> lightSources = new ArrayList<>();
    private double gravity = 1d;
    protected List<Updatable> updatables = new CopyOnWriteArrayList<>();
    protected List<Gravitational> gravitationals = new ArrayList<>();
    protected List<AABBObject> AABBObjects = new ArrayList<>();
    protected List<DynamicAABBObject> dynamicAABBObjects = new ArrayList<>();
    protected List<StaticAABBObject> staticAABBObjects = new ArrayList<>();
    private final List<TriggerZone> triggers = new ArrayList<>();

    //Makes sure we can't read game state while it is being written
    private final Object stateLock = new Object();
    private final TileRasterizer tileRasterizer = new TileRasterizer();

    private volatile SkyBox skyBox;
    private final List<ScreenFilter> filters = new CopyOnWriteArrayList<>();

    public Scene(Camera camera) {
        this.camera = camera;
        //if (camera instanceof PlayerCamera) {
        //    new PlayerObject(this, (PlayerCamera) camera);
        //}
        camera.getFrame().add(sceneRenderer);
        sceneRenderer.setActiveScene(this);
        sceneRenderer.grabFocus();

        camera.getFrame().repaint();

        getSceneRenderer().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F3)
                    camera.debugging = !camera.debugging;
            }
        });
    }
    public SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public void addObject(Object3D object)
    {
        objects.add(object);
        if (object instanceof Updatable)
        {
            addUpdatable((Updatable) object);
        }
        if (object instanceof Gravitational)
        {
            gravitationals.add((Gravitational) object);
        }
        if (object instanceof AABBObject)
        {
            AABBObjects.add((AABBObject) object);

            if (object instanceof DynamicAABBObject)
            {
                dynamicAABBObjects.add((DynamicAABBObject) object);
            }
            else if (object instanceof StaticAABBObject)
            {
                staticAABBObjects.add((StaticAABBObject) object);
            }
        }
    }

    public void addUpdatable(Updatable updatable) {
        updatables.add(updatable);
        if (updatable instanceof TriggerZone tz) {
            triggers.add(tz);
        }
    }

    record RenderItem(Mesh mesh, Vector3D position, Vector3D rotation) {}
    public void buildScreenBuffer() {
        camera.getScreenBuffer().clear(backgroundColour);

        List<RenderItem> frame;

        synchronized (stateLock) {
            frame = new ArrayList<>(objects.size());
            for (Object3D obj : objects) {
                frame.add(new RenderItem(
                        obj.getMesh(),
                        new Vector3D(obj.getPosition()),
                        new Vector3D(obj.getRotation())
                ));
            }
        }

        //These values are purely based off the camera.
        //If they change between two objects on the same frame then the objects can "jitter"
        //This is also slightly more efficient.
        Vector3D constCamPos = new Vector3D(camera.getPosition());
        Vector3D constCamDir = new Vector3D(camera.getDirection());
        Vector3D up = new Vector3D(0,1,0);
        Vector3D target = camera.getDirection().translated(constCamPos);
        Matrix4x4 cameraMatrix = Matrix4x4.getPointAtMatrix(constCamPos, target, up);
        Matrix4x4 viewMatrix = cameraMatrix.quickMatrixInverse();

        frame.sort((o1, o2) -> {
            // Calculate distances to the camera
            double distance1 = o1.position.distanceTo(constCamPos);
            double distance2 = o2.position.distanceTo(constCamPos);
            // Sort objects by distance (closer first)
            return Double.compare(distance1, distance2);
        });

        //Compute Geometry
        List<Mesh.ProjectedTriangles> geometry;
        try (Profiler.Span s = Profiler.span("geometry")) {
            geometry = new ArrayList<>(frame.parallelStream()
                    .map(o -> o.mesh.computeGeometry(o.position, o.rotation, camera, constCamPos, viewMatrix, lightSources))
                    .toList());   // ← wrap in ArrayList so we can add to it
        }

        if (camera.debugging) {
            for (AABBObject o : AABBObjects)
                geometry.add(debugBox(o.getAABBCollider().getAABB(), Color.WHITE, camera, constCamPos, viewMatrix));
            for (TriggerZone t : triggers)
                geometry.add(debugBox(t.getRegion(), Color.ORANGE, camera, constCamPos, viewMatrix));
        }

        try (Profiler.Span s = Profiler.span("raster")) {
            tileRasterizer.render(camera, geometry, backgroundColour, skyBox);
        }

        //Rasterize
        try (Profiler.Span s = Profiler.span("raster")) {
            tileRasterizer.render(camera, geometry, backgroundColour,skyBox);
        }

        /*
        TODO: Add debug showing debugging stuff (eg. Wireframes)
        if (camera.debugging) {

        }
         */

        try (Profiler.Span s = Profiler.span("filters")) {
            for (ScreenFilter f : filters) f.apply(camera.getScreenBuffer(), constCamPos, constCamDir);
            filters.removeIf(ScreenFilter::isDone);
        }
    }

    public Camera getCamera() {return camera;}

    public void addLight(LightSource lightSource) {
        lightSources.add(lightSource);
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }
    public void addFilter(ScreenFilter f)    { filters.add(f); }
    public void removeFilter(ScreenFilter f) { filters.remove(f); }
    public void setGravity(double grav) {
        gravity = grav;
    }
    public Object3D loadFromFile(String folderPath, String filePath) {
        Object3D object3D = new Object3D(this);
        Mesh loaded = objParser.loadFromObjFile(object3D, folderPath, filePath);
        if (loaded == null) {
            getSceneRenderer().logError("ObjParser couldn't find file: " + folderPath + "/" + filePath);
            object3D.setMesh(new Mesh());
        }
        return object3D;
    }

    @Override
    public void update(double deltaTime) {
        synchronized (stateLock) {
            for (Updatable updatable : updatables) {
                updatable.update(deltaTime);
            }

            try (Profiler.Span s = Profiler.span("applyGravity")) {
                for (Gravitational grav : gravitationals) {
                    grav.applyGravity(gravity, deltaTime);
                }
            }

            try (Profiler.Span s = Profiler.span("handleCollision")) {
                for (int i = 0; i < dynamicAABBObjects.size(); i++) {
                    for (int j = 0; j < staticAABBObjects.size(); j++) {
                        dynamicAABBObjects.get(i).
                                getAABBCollider().handleCollision(
                                        staticAABBObjects.get(j).getAABBCollider());
                    }
                    for (int j = i+1; j < dynamicAABBObjects.size(); j++) {
                        dynamicAABBObjects.get(i).
                                getAABBCollider().handleCollision(
                                        dynamicAABBObjects.get(j).getAABBCollider());
                    }
                }
            }
        }
    }

    public RayCollision checkAndGetCollision(int numSteps, Ray ray) {
        for (int i = 0; i < numSteps; i++) {
            for (int j = 0; j < AABBObjects.size(); j++) {
                try {
                    AABBObject obj = AABBObjects.get(j);
                    if (obj == ray.getSource()) { continue; }
                    if (!obj.getAABBCollider().getAABB().collision(ray).isEmpty()) {
                        if (GJK.boolSolveGJK(obj, ray)) {
                            return new RayCollision(ray.getOrigin(), obj);
                        }
                    }
                }
                catch (NullPointerException e) {
                    System.err.println(e.getMessage());
                }
            }
            ray.advance();
        }
        return null;
    }
    public boolean checkForCollision(int numSteps, Ray ray) {
        if (checkAndGetCollision(numSteps, ray) != null) {
            return true;
        }
        return false;
    }

    public void createCollisionMarker(Vector3D pos) {
        Object3D marker = new Object3D(this);
        marker.translate(pos);
        Mesh mesh = new CubeMesh(marker, 0.25);
        marker.setMesh(mesh);

        mesh.setDrawInstructions(new DrawInstructions(true,false,false,false));

        objects.add(marker);
    }

    public boolean boxOnGround(AABB probe) {
        for (StaticAABBObject s : staticAABBObjects) {
            if (s.getAABBCollider().getAABB().overlaps(probe)) return true;
        }
        return false;
    }

    public double groundDistanceBelow(Vector3D origin) {
        Vector3D dir = Vector3D.DOWN();     // unit, so the result is a real distance
        double best = Double.POSITIVE_INFINITY;
        for (StaticAABBObject s : staticAABBObjects) {
            for (MeshTriangle tri : s.getMesh().getFacesInWorld(s.getPosition(), s.getRotation())) {
                Vector3D[] p = tri.getPoints();
                double t = RayTriangle.intersect(origin, dir, p[0], p[1], p[2]);
                if (!Double.isNaN(t) && t < best) best = t;
            }
        }
        return best;
    }

    private Mesh.ProjectedTriangles debugBox(AABB box, Color color, Camera camera,
                                             Vector3D camPos, Matrix4x4 viewMatrix) {
        BoxMesh mesh = new BoxMesh(new Box(box.min(), box.max()));   // built at world min/max
        DrawInstructions di = new DrawInstructions(true, false, false, false); // wireframe only
        di.wireFrameColour = color;
        di.ignorePixelDepth = true;                                  // draw on top, see through walls
        mesh.setDrawInstructions(di);
        return mesh.computeGeometry(new Vector3D(0,0,0), new Vector3D(0,0,0),
                camera, camPos, viewMatrix, lightSources);
    }
}
