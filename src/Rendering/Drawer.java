package Rendering;

import WorldSpace.Triangle;
import WorldSpace.Vector3D;

import java.awt.*;

public class Drawer
{
    public static void drawLine(Graphics2D g, Vector3D v1, Vector3D v2)
    {
        g.drawLine((int) v1.x(), (int) v1.y(), (int) v2.x(), (int) v2.y());
    }

    public static void drawTriangle(Graphics2D g, Triangle t)
    {
        Vector3D points[] = t.getPoints();
        drawLine(g,points[0],points[1]);
        drawLine(g,points[1],points[2]);
        drawLine(g,points[2],points[0]);

    }
}
