package Rendering;

import WorldSpace.Triangle;
import WorldSpace.Vector2D;
import WorldSpace.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PixelDrawer
{
    Camera camera;
    double[][] depthBuffer;
    public PixelDrawer(Camera camera)
    {
        this.camera = camera;
        recomputeDepthBuffer();
    }

    public void drawLine(Graphics2D g, Color c, Vector3D v1, Vector3D v2)
    {
        g.setColor(c);
        v1 = adjustVector(v1);
        v2 = adjustVector(v2);
        g.drawLine((int) v1.x(), (int) v1.y(), (int) v2.x(), (int) v2.y());
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

    public void fillRectangle(Graphics2D g, Color c, Vector3D rect)
    {
        g.setColor(c);
        g.fillRect(0, 0, (int) (rect.x() / camera.resolution), (int) (rect.y() / camera.resolution));
    }

    private Vector3D adjustVector(Vector3D vec)
    {
        double x = (vec.x() * (1/camera.resolution));
        double y = camera.getScreenDimensions().y() - (vec.y() * (1/camera.resolution));
        return new Vector3D(x,y,0);
    }

    private Triangle adjustTriangle(Triangle tri)
    {
        Vector3D[] points = tri.getPoints();
        Vector2D[] texPoints = tri.getMaterial().getTextureCoords();

        Vector3D p1 = adjustVector(points[0]);
        Vector3D p2 = adjustVector(points[1]);
        Vector3D p3 = adjustVector(points[2]);
        Vector2D t1 = texPoints[0].scaled((1/camera.resolution));
        Vector2D t2 = texPoints[1].scaled((1/camera.resolution));
        Vector2D t3 = texPoints[2].scaled((1/camera.resolution));

        Triangle res = new Triangle(p1,p2,p3);
        Material mat = new Material(tri.getMaterial());
        mat.setTextureCoords(t1,t2,t3);
        res.setMaterial(mat);
        return res;
    }

    public void textureTriangle(Graphics2D g, Triangle tri, BufferedImage sprite)
    {
        Vector3D[] points = tri.getPoints();
        Vector2D[] texPoints = tri.getMaterial().getTextureCoords();

        int x1 = (int) points[0].x(); int y1 = (int) points[0].y();
        int x2 = (int) points[1].x(); int y2 = (int) points[1].y();
        int x3 = (int) points[2].x(); int y3 = (int) points[2].y();

        double u1 = texPoints[0].u(); double v1 = texPoints[0].v(); double w1 = texPoints[0].w();
        double u2 = texPoints[1].u(); double v2 = texPoints[1].v(); double w2 = texPoints[1].w();;
        double u3 = texPoints[2].u(); double v3 = texPoints[2].v(); double w3 = texPoints[2].w();

        Texturizer.textureTriangle(g,(int)camera.getResolution().y(),camera.resolution,depthBuffer,
                x1,y1,u1,v1,w1,x2,y2,u2,v2,w2,x3,y3,u3,v3,w3,
                tri.getMaterial().getLuminance(),sprite);
    }

    public void recomputeDepthBuffer()
    {
        Vector3D res = camera.getResolution();
        depthBuffer = new double[(int) res.x()][(int) res.y()];
    }
}
