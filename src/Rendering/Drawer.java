package Rendering;

import Rendering.ResourceManager.SpriteManager;
import WorldSpace.Triangle;
import WorldSpace.Vector2D;
import WorldSpace.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Drawer
{
    static double SHADING_HARSHNESS = 0.2d; //The lower this is the darker the shadows will be (0-1)
    Camera camera;
    SpriteManager spriteManager = new SpriteManager();

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

    public void textureTriangle(Graphics2D g, Triangle tri)
    {
        BufferedImage sprite = spriteManager.getResource(tri.getMaterial().getTexturePath());
        if (sprite == null) {return;}

        Vector3D[] points = tri.getPoints();
        Vector2D[] texPoints = tri.getMaterial().getTextureCoords();

        int x1 = (int) points[0].x(); int y1 = (int) points[0].y();
        int x2 = (int) points[1].x(); int y2 = (int) points[1].y();
        int x3 = (int) points[2].x(); int y3 = (int) points[2].y();

        int u1 = (int) texPoints[0].u(); int v1 = (int) texPoints[0].v(); int w1 = 0;
        int u2 = (int) texPoints[1].u(); int v2 = (int) texPoints[1].v(); int w2 = 0;
        int u3 = (int) texPoints[2].u(); int v3 = (int) texPoints[2].v(); int w3 = 0;

        Texturizer.textureTriangle(g,(int)camera.getScreenDimensions().y(),x1,y1,u1,v1,w1,x2,y2,u2,v2,w2,x3,y3,u3,v3,w3, sprite);
    }

    public void fillBackground(Graphics2D g, Color c)
    {
        g.setColor(c);
        g.fillRect(0, 0, (int) camera.getScreenDimensions().x(), (int) camera.getScreenDimensions().y());
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

    private void sortByY(Vector3D[] p, Vector2D[] t)
    {
        Vector3D[] pointsSorted = new Vector3D[3];
        Vector2D[] texPointsSorted = new Vector2D[3];

        Vector3D temp;
        Vector2D tempTex;
        for (int i = 0; i < 3; i++)
        {
            Vector3D pi = p[i];
            Vector2D ti = t[i];
            for (int j = 0; j < i; j++) {
                if (pi.y() < pointsSorted[j].y())
                {
                    temp = pointsSorted[j];
                    tempTex = texPointsSorted[j];

                    pointsSorted[j] = pi;
                    texPointsSorted[j] = ti;

                    pi = temp;
                    ti = tempTex;
                }
            }
            pointsSorted[i] = pi;
            texPointsSorted[i] = ti;
        }

        p = pointsSorted;
        t = texPointsSorted;
    }
}
