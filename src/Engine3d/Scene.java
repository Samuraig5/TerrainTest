package Engine3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Engine3d.DevTools.Log;
import Engine3d.Objects.Object3D;
import Engine3d.Objects.ObjectSource;
import Engine3d.Rendering.*;
import Engine3d.Rendering.Filters.ScreenFilter;
import Engine3d.Rendering.Skyboxes.SkyBox;
import Physics.AABBCollisions.AABBObject;
import Physics.AABBCollisions.DynamicAABBObject;
import Physics.AABBCollisions.StaticAABBObject;
import Physics.Gravitational;
import Engine3d.Lighting.LightSource;
import Math.Vector.Vector3D;
import Engine3d.Model.ObjParser;
import Engine3d.Time.Updatable;
import Engine3d.Model.Mesh;
import Physics.Triggers.TriggerZone;

public class Scene {
    private GameEngine gameEngine;

    private final ObjParser objParser = new ObjParser();
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

    private volatile SkyBox skyBox;
    private final List<ScreenFilter> filters = new CopyOnWriteArrayList<>();

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

    public void removeObject(Object3D object) {
        objects.remove(object);
        updatables.remove(object);
        gravitationals.remove(object);
        AABBObjects.remove(object);
        dynamicAABBObjects.remove(object);
        staticAABBObjects.remove(object);
    }

    public java.util.List<Object3D> getObjects() { return objects; }

    public void addUpdatable(Updatable updatable) {
        updatables.add(updatable);
        if (updatable instanceof TriggerZone tz) {
            triggers.add(tz);
        }
    }
    public List<Updatable> getUpdatables() {
        return updatables;
    }

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
            Log.println("ObjParser couldn't find file: " + folderPath + "/" + filePath);
            object3D.setMesh(new Mesh());
        }
        object3D.setObjectSource(new ObjectSource.ModelSource(folderPath, filePath));
        return object3D;
    }

    public Color getBackgroundColour() {
        return backgroundColour;
    }
    public void setBackgroundColour(Color c) { this.backgroundColour = c; }

    public List<RenderItem> snapshotObjects() {
        List<RenderItem> frame = new ArrayList<>(objects.size());
        for (Object3D obj : objects) {
            frame.add(new RenderItem(
                    obj.getMesh(),
                    new Vector3D(obj.getScale()),
                    new Vector3D(obj.getPosition()),
                    new Vector3D(obj.getRotation())
            ));
        }
        return frame;
    }

    public List<LightSource> getLightSources() {
        return lightSources;
    }

    public List<AABBObject> getAABBObjects() {
        return AABBObjects;
    }

    public List<TriggerZone> getTriggers() {
        return triggers;
    }

    public SkyBox getSkybox() {
        return skyBox;
    }

    public List<ScreenFilter> getFilters() {
        return filters;
    }

    public List<Gravitational> getGravitationals() {
        return gravitationals;
    }

    public double getGravity() {
        return gravity;
    }

    public List<DynamicAABBObject> getDynamicAABBObjects() {
        return dynamicAABBObjects;
    }

    public List<StaticAABBObject> getStaticAABBObjects() {
        return staticAABBObjects;
    }

    public double groundDistanceBelow(Vector3D origin) {
        return gameEngine.getPhysics().groundDistanceBelow(this, origin);
    }

    public void setEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    public GameEngine getEngine() {
        return gameEngine;
    }

    public void tickScripts(double dt) {
        for (Updatable updatable : updatables) {
            updatable.update(dt);
        }
    }
}
