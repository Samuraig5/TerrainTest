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
    public static void drawLine(Graphics2D g, Color c, Vector3D v1, Vector3D v2) {
        g.setColor(c); drawLine(g, v1, v2);
    }

    public static void drawTriangle(Graphics2D g, Triangle t)
    {
        Vector3D points[] = t.getPoints();
        drawLine(g,points[0],points[1]);
        drawLine(g,points[1],points[2]);
        drawLine(g,points[2],points[0]);

    }
    public static void drawTriangle(Graphics2D g, Color c, Triangle t) {
        g.setColor(c); drawTriangle(g, t);
    }


    public static void fillTriangle(Graphics2D g, Triangle t)
    {
        g.setColor(t.getShadedColour());

        Vector3D points[] = t.getPoints();

        Polygon triangle = new Polygon();
        triangle.addPoint((int) points[0].x(), (int) points[0].y());
        triangle.addPoint((int) points[1].x(), (int) points[1].y());
        triangle.addPoint((int) points[2].x(), (int) points[2].y());

        g.fillPolygon(triangle);
    }

    public static Color getColourShade(Color baseColor, double luminance) {
        luminance = Math.max(0, Math.min(1, luminance)); //Clamp value for safety

        int red = (int) (baseColor.getRed() * luminance);
        int green = (int) (baseColor.getGreen() * luminance);
        int blue = (int) (baseColor.getBlue() * luminance);

        return new Color(red, green, blue);
    }
}
