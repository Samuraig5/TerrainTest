package Rendering;

import WorldSpace.Object3D;
import WorldSpace.Vector3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
    List<Object3D> objects = new ArrayList<>();
    Camera camera = new Camera(new Vector3D(0,0,0), new Vector3D(0,0,0));

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
