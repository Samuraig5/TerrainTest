package Engine3d.Rendering;

import Engine3d.Gravitational;
import Engine3d.Lighting.LightSource;
import Engine3d.Math.Matrix4x4;
import Engine3d.Math.Vector.Vector3D;
import Engine3d.Model.ObjParser;
import Engine3d.Time.GameTimer;
import Engine3d.Time.TimeMeasurer;
import Engine3d.Time.Updatable;
import Engine3d.Model.Object3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene implements Updatable
{
    private ObjParser objParser;
    Camera camera;
    final SceneRenderer sceneRenderer = new SceneRenderer();
    Color backgroundColour = Color.BLACK;
    final GameTimer sceneTimer = new GameTimer();
    private TimeMeasurer timeMeasurer;
    List<Object3D> objects = new CopyOnWriteArrayList<>();
    List<LightSource> lightSources = new ArrayList<>();
    private double gravity = 0.5d;
    protected List<Gravitational> gravitationals = new ArrayList<>();

    public Scene(Camera camera) {
        this.camera = camera;

        subscribeToTime(this);

        objParser = new ObjParser();

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
            subscribeToTime((Updatable) object);
        }
        if (object instanceof Gravitational)
        {
            gravitationals.add((Gravitational) object);
        }
    }


    public void buildScreenBuffer()
    {
        camera.getScreenBuffer().clear(backgroundColour);
        objects.sort(new Comparator<Object3D>() {
            @Override
            public int compare(Object3D o1, Object3D o2) {
                // Calculate distances to the camera
                double distance1 = o1.getPosition().distanceTo(camera.getPosition());
                double distance2 = o2.getPosition().distanceTo(camera.getPosition());
                // Sort objects by distance (closer first)
                return Double.compare(distance1, distance2);
            }
        });

        //These values are purely based off the camera.
        //If they change between two objects on the same frame then the objects can "jitter"
        //This is also slightly more efficient.
        Vector3D constCamPos = new Vector3D(camera.position);
        Vector3D up = new Vector3D(0,1,0);
        Vector3D target = camera.getDirection().translated(constCamPos);
        Matrix4x4 cameraMatrix = Matrix4x4.getPointAtMatrix(constCamPos, target, up);
        Matrix4x4 viewMatrix = cameraMatrix.quickMatrixInverse();

        for (Object3D o:objects)
        {
            o.drawObject(camera, constCamPos, viewMatrix, lightSources, timeMeasurer);
        }
    }

    public Camera getCamera() {return camera;}

    public void subscribeToTime (Updatable updatable) {sceneTimer.subscribe(updatable);}

    public void addTimeMeasurer(TimeMeasurer tm) {
        this.timeMeasurer = tm;
    }
    public void addLight(LightSource lightSource) {
        lightSources.add(lightSource);
    }
    public Object3D loadFromFile(String folderPath, String filePath) {
        Object3D loaded = objParser.loadFromObjFile(folderPath, filePath);
        if (loaded == null) {
            getSceneRenderer().logError("ObjParser coulding find file: " + folderPath + "/" + filePath);
            loaded = new Object3D();
        }
        return loaded;
    }

    @Override
    public void update(double deltaTime) {
        for (Gravitational grav : gravitationals) {
            grav.applyGravity(gravity, deltaTime);
        }
    }
}
