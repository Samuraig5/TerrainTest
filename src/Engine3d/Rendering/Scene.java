package Engine3d.Rendering;

import Engine3d.Math.Ray;
import Engine3d.Model.DrawInstructions;
import Engine3d.Model.UnrotatableBox;
import Physics.AABBCollisions.AABBObject;
import Physics.AABBCollisions.DynamicAABBObject;
import Physics.AABBCollisions.StaticAABBObject;
import Physics.Gravitational;
import Engine3d.Lighting.LightSource;
import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.ObjParser;
import Physics.Object3D;
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
    private final ObjParser objParser = new ObjParser();
    Camera camera;
    final SceneRenderer sceneRenderer = new SceneRenderer();
    protected Color backgroundColour = Color.BLACK;
    private TimeMeasurer timeMeasurer;
    List<Object3D> objects = new CopyOnWriteArrayList<>();
    List<LightSource> lightSources = new ArrayList<>();
    private double gravity = 1d;
    protected List<Updatable> updatables = new CopyOnWriteArrayList<>();
    protected List<Gravitational> gravitationals = new ArrayList<>();
    protected List<AABBObject> AABBObjects = new ArrayList<>();
    protected List<DynamicAABBObject> dynamicAABBObjects = new ArrayList<>();
    protected List<StaticAABBObject> staticAABBObjects = new ArrayList<>();

    public Scene(Camera camera) {
        this.camera = camera;
        //if (camera instanceof PlayerCamera) {
        //    new PlayerObject(this, (PlayerCamera) camera);
        //}

        camera.getFrame().add(sceneRenderer);
        sceneRenderer.setActiveScene(this);
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
    }


    public void buildScreenBuffer()
    {
        camera.getScreenBuffer().clear(backgroundColour);
        objects.sort((o1, o2) -> {
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


        /*
        for (Object3D o:objects)
        {
            o.getMesh().drawObject(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
            if (camera.debugging) {
                o.getSource().drawObject(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
            }
        }

         */

        objects.parallelStream().forEach(o -> {
            o.getMesh().drawMesh(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
            if (camera.debugging) {

                o.getSource().drawMesh(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
                if (o instanceof AABBObject && !(o instanceof PlayerObject)) {
                    UnrotatableBox collision = ((AABBObject) o).getAABBCollider().getAABBMesh();
                    collision.scale(new Vector3D(1.01f,1.01f,1.01f));
                    collision.centreToMiddleBottom();

                    DrawInstructions di = new DrawInstructions(true,false,false,false);
                    di.wireFrameColour = Color.ORANGE;
                    collision.setDrawInstructions(di);
                    collision.drawMesh(camera,constCamPos,viewMatrix,lightSources,timeMeasurer);
                }
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
        timeMeasurer.pauseAndEndMeasurement("handleCollision");
    }

    public boolean checkForCollision(int numSteps, Ray ray) {
        for (int i = 0; i < numSteps; i++) {
            for (int j = 0; j < AABBObjects.size(); j++) {
                try {
                    AABBObject obj = AABBObjects.get(j);
                    if (obj == ray.getSource()) { continue; }
                    if (!obj.getAABBCollider().getAABB().collision(ray).isEmpty()) {
                        return true;
                    }
                }
                catch (NullPointerException e) {

                }
            }
            ray.advance();
        }
        return false;
    }
}
