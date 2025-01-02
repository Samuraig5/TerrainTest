package Rendering;

import WorldSpace.Triangle;
import WorldSpace.Vector3D;

import java.awt.*;

public class Drawer
{
    static double SHADING_HARSHNESS = 0.2d; //The lower this is the darker the shadows will be (0-1)
    Camera camera;

    public Drawer(Camera camera)
    {
        this.camera = camera;
    }

    public void drawLine(Graphics2D g, Vector3D v1, Vector3D v2)
    {
        v1 = adjustVector(v1);
        v2 = adjustVector(v2);
        g.drawLine((int) v1.x(), (int) v1.y(), (int) v2.x(), (int) v2.y());
    }
    public void drawLine(Graphics2D g, Color c, Vector3D v1, Vector3D v2) {
        g.setColor(c); drawLine(g, v1, v2);
    }

    public void drawTriangle(Graphics2D g, Triangle t)
    {
        Vector3D points[] = t.getPoints();
        drawLine(g,points[0],points[1]);
        drawLine(g,points[1],points[2]);
        drawLine(g,points[2],points[0]);

    }
    public void drawTriangle(Graphics2D g, Color c, Triangle t) {
        g.setColor(c); drawTriangle(g, t);
    }


    public void fillTriangle(Graphics2D g, Triangle t)
    {
        t = adjustTriangle(t);

        g.setColor(t.getMaterial().getShadedColour());

        Vector3D points[] = t.getPoints();

        Polygon triangle = new Polygon();
        triangle.addPoint((int) points[0].x(), (int) points[0].y());
        triangle.addPoint((int) points[1].x(), (int) points[1].y());
        triangle.addPoint((int) points[2].x(), (int) points[2].y());

        g.fillPolygon(triangle);
    }

    public static Color getColourShade(Color baseColor, double luminance) {
        luminance = Math.max(SHADING_HARSHNESS, Math.min(1, luminance)); //Clamp value for safety

        int red = (int) (baseColor.getRed() * luminance);
        int green = (int) (baseColor.getGreen() * luminance);
        int blue = (int) (baseColor.getBlue() * luminance);

        return new Color(red, green, blue);
    }

    private Vector3D adjustVector(Vector3D vec)
    {
        double x = vec.x();
        double y = camera.getScreenDimensions().y() - vec.y();
        return new Vector3D(x,y,0);
    }

    private Triangle adjustTriangle(Triangle tri)
    {
        Vector3D[] points = tri.getPoints();
        Vector3D p1 = adjustVector(points[0]);
        Vector3D p2 = adjustVector(points[1]);
        Vector3D p3 = adjustVector(points[2]);

        Triangle res = new Triangle(p1,p2,p3);
        res.setMaterial(new Material(tri.getMaterial()));
        return res;
    }
}
