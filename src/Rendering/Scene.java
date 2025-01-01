package Rendering;

import Time.GameTimer;
import Time.Updatable;
import WorldSpace.Object3D;
import WorldSpace.Vector3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
    List<Object3D> objects = new ArrayList<>();
    Camera camera;
    GameTimer sceneTimer;

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
            sceneTimer.subscribe((Updatable) object);
        }
    }

    public void drawScene(Graphics g)
    {
        for (Object3D o:objects)
        {
            o.drawObject(g, camera);
        }
    }

    public Camera getCamera() {return camera;}
}
