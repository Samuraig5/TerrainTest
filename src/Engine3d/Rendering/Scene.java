package Engine3d.Rendering;

import Engine3d.Time.GameTimer;
import Engine3d.Time.TimeMeasurer;
import Engine3d.Time.Updatable;
import Engine3d.Model.Object3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scene
{
    List<Object3D> objects = new ArrayList<>();
    Camera camera;
    GameTimer sceneTimer;
    Color backgroundColour = Color.BLACK;
    private TimeMeasurer timeMeasurer;

    public Scene(Camera camera)
    {
        this.camera = camera;
        this.sceneTimer = new GameTimer();
    }

    public void addObject(Object3D object)
    {
        objects.add(object);
        if (object instanceof Updatable)
        {
            subscribeToTime((Updatable) object);
        }
    }

    public void drawScene()
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
        for (Object3D o:objects)
        {
            o.drawObject(camera, timeMeasurer);
        }
    }

    public Camera getCamera() {return camera;}

    public void subscribeToTime (Updatable updatable) {sceneTimer.subscribe(updatable);}

    public void addTimeMeasurer(TimeMeasurer tm) {
        this.timeMeasurer = tm;
    }
}
