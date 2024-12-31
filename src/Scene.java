import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene
{
    List<Object3D> objects = new ArrayList<>();

    public void addObject(Object3D object)
    {
        objects.add(object);
    }

    public void drawScene(Graphics g)
    {
        for (Object3D o:objects)
        {
            o.drawObject(g);
        }
    }
}
