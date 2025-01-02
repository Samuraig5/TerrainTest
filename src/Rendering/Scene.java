package Rendering;

import Time.GameTimer;
import Time.Updatable;
import WorldSpace.Object3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
    List<Object3D> objects = new ArrayList<>();
    Camera camera;
    GameTimer sceneTimer;
    Color backgroundColour = Color.BLACK;

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
        camera.drawer.fillBackground((Graphics2D) g,backgroundColour);
        for (Object3D o:objects)
        {
            o.drawObject(g, camera);
        }
    }

    public Camera getCamera() {return camera;}

    public void subscribeToTime (Updatable updatable) {sceneTimer.subscribe(updatable);}
}
