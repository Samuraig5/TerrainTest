package Engine3d.Rendering;

import Engine3d.Time.GameTimer;
import Engine3d.Time.TimeMeasurer;
import Engine3d.Time.Updatable;
import Engine3d.Model.Object3D;

import java.awt.*;
import java.util.ArrayList;
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

    public void drawScene(Graphics g)
    {
        camera.drawer.clearScreen((Graphics2D) g,backgroundColour);
        for (Object3D o:objects)
        {
            o.drawObject(g, camera, timeMeasurer);
        }
    }

    public Camera getCamera() {return camera;}

    public void subscribeToTime (Updatable updatable) {sceneTimer.subscribe(updatable);}

    public void addTimeMeasurer(TimeMeasurer tm) {
        this.timeMeasurer = tm;
    }
}
