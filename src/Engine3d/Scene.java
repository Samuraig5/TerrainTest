package Engine3d;

import Engine3d.Model.SimpleMeshes.CubeMesh;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.DrawInstructions;
import Engine3d.Rendering.SceneRenderer;
import Math.Raycast.Ray;
import Engine3d.Model.UnrotatableBox;
import Math.Raycast.RayCollision;
import Menus.Settings;
import Physics.CollidableObject;
import Physics.CollisionHandler;
import Physics.GJK_EPA.GJK;
import Physics.Gravitational;
import Engine3d.Lighting.LightSource;
import Math.Matrix4x4;
import Math.Vector.Vector3D;
import Engine3d.Model.ObjParser;
import Engine3d.Time.TimeMeasurer;
import Engine3d.Time.Updatable;
import Engine3d.Model.Mesh;
import Physics.PlayerObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene implements Updatable
{
    private Settings settings;
    private final ObjParser objParser = new ObjParser();
    Camera camera;
    final SceneRenderer sceneRenderer = new SceneRenderer();
    protected Color backgroundColour = Color.BLACK;
    private TimeMeasurer timeMeasurer;
    private double gravity;

    protected List<Object3D> objects = new CopyOnWriteArrayList<>();
    protected List<Object3D> activeObjects = new CopyOnWriteArrayList<>();
    List<LightSource> lightSources = new ArrayList<>();
    protected List<Updatable> updatables = new CopyOnWriteArrayList<>();
    protected List<Gravitational> gravitationals = new ArrayList<>();
    protected List<CollidableObject> collidables = new ArrayList<>();

    public Scene(Camera camera, Settings settings) {
        this.settings = settings;
        gravity = settings.DEFAULT_GRAVITY();
        this.camera = camera;
        //if (camera instanceof PlayerCamera) {
        //    new PlayerObject(this, (PlayerCamera) camera);
        //}
        camera.getFrame().add(sceneRenderer);
        sceneRenderer.setActiveScene(this);
        sceneRenderer.grabFocus();

        camera.getFrame().repaint();
    }
    public SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public void addObject(Object3D object)
    {
        objects.add(object);
        setObjectState(object, true);
    }

    public void removeObject(Object3D object) {
        objects.remove(object);
        setObjectState(object, false);
    }

    /**
     * Function to change an object's state.
     * Influences things like rendering and collision detection.
     * @param obj Object who's state should be changed
     * @param state state true = active, false = inactive.
     */
    public void setObjectState(Object3D obj, boolean state) {
        if (state) {
            if (!activeObjects.contains(obj)) {
                activeObjects.add(obj);
                if (obj instanceof Updatable) {
                    addUpdatable((Updatable) obj);
                }
                if (obj instanceof Gravitational) {
                    gravitationals.add((Gravitational) obj);
                }
                if (obj instanceof CollidableObject) {
                    collidables.add((CollidableObject) obj);
                }
            }

        }
        else {
            activeObjects.remove(obj);
            if (obj instanceof Updatable) {
                updatables.remove(obj);
            }
            if (obj instanceof Gravitational) {
                gravitationals.remove((Gravitational) obj);
            }
            if (obj instanceof CollidableObject) {
                collidables.remove((CollidableObject) obj);
            }
        }
    }

    public void addUpdatable(Updatable updatable) {
        updatables.add(updatable);
    }


    public void buildScreenBuffer()
    {
        camera.getScreenBuffer().clear(backgroundColour);
        activeObjects.sort((o1, o2) -> {
            // Calculate distances to the camera
            double distance1 = o1.getPosition().distanceTo(camera.getPosition());
            double distance2 = o2.getPosition().distanceTo(camera.getPosition());
            // Sort objects by distance (closer first)
            return Double.compare(distance1, distance2);
        });

        //These values are purely based off the camera.
        //If they change between two objects on the same frame then the objects can "jitter"
        //This is also slightly more efficient.
        Vector3D constCamPos = new Vector3D(camera.getPosition());
        Vector3D up = new Vector3D(0,1,0);
        Vector3D target = camera.getDirection().translated(constCamPos);
        Matrix4x4 cameraMatrix = Matrix4x4.getPointAtMatrix(constCamPos, target, up);
        Matrix4x4 viewMatrix = cameraMatrix.quickMatrixInverse();


        activeObjects.parallelStream().forEach(o -> {
            o.getMesh().drawMesh(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
            if (camera.debugging) {

                o.getSource().drawMesh(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
            }
        });
    }

    public Camera getCamera() {return camera;}

    public void addTimeMeasurer(TimeMeasurer tm) {
        this.timeMeasurer = tm;
    }
    public void addLight(LightSource lightSource) {
        lightSources.add(lightSource);
    }

    public void setGravity(double grav) {
        gravity = grav;
    }
    public Object3D loadFromFile(String folderPath, String filePath) {
        Object3D object3D = new Object3D(this);
        Mesh loaded = objParser.loadFromObjFile(object3D, folderPath, filePath);
        if (loaded == null) {
            getSceneRenderer().logError("ObjParser couldn't find file: " + folderPath + "/" + filePath);
            object3D.setMesh(new Mesh(object3D));
        }
        return object3D;
    }

    @Override
    public void update(double deltaTime) {
        for (Updatable updatable : updatables) {
            updatable.update(deltaTime);
        }

        timeMeasurer.startMeasurement("applyGravity");
        for (Gravitational grav : gravitationals) {
            grav.applyGravity(gravity, deltaTime);
        }
        timeMeasurer.pauseAndEndMeasurement("applyGravity");

        timeMeasurer.startMeasurement("handleCollision");
        for (int i = 0; i < collidables.size(); i++) {
            CollidableObject o1 = collidables.get(i);
            for (int j = i+1; j < collidables.size(); j++) {
                CollidableObject o2 = collidables.get(j);
                CollisionHandler.handleCollision(o1, o2);
            }
        }
        timeMeasurer.pauseAndEndMeasurement("handleCollision");
    }

    public RayCollision checkAndGetCollision(int numSteps, Ray ray) {
        for (int i = 0; i < numSteps; i++) {
            for (int j = 0; j < collidables.size(); j++) {
                try {
                    CollidableObject obj = collidables.get(j);
                    if (obj == ray.getSource()) { continue; }
                    if (obj.getCollider().contains(ray.getOrigin())) {
                        return new RayCollision(ray.getOrigin(), obj);
                    }
                }
                catch (NullPointerException e) {
                    //System.err.println("RayCollision ran into NullPointerException: " + e.getMessage());
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
}
