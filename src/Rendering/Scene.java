package Rendering;

import WorldSpace.Object3D;
import WorldSpace.Vector3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
    List<Object3D> objects = new ArrayList<>();
    Camera camera;

    public Scene(Camera camera)
    {
        this.camera = camera;
    }

    public void addObject(Object3D object)
    {
        objects.add(object);
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
