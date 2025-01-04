package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Rendering.Camera;
import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector2D;
import Engine3d.Math.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class PixelDrawer
{
    Camera camera;
    double[][] depthBuffer;
    public PixelDrawer(Camera camera)
    {
        this.camera = camera;
    }

    public static void drawPixel(ScreenBuffer screenBuffer, Color c, int x, int y)
    {
        BufferedImage bufferedImage = screenBuffer.getBufferedImage();
        y = bufferedImage.getHeight() - y;
        if (!screenBuffer.inBounds(x,y)) {return;}
        WritableRaster raster = bufferedImage.getRaster();
        raster.setPixels(x, y, 1, 1, ScreenBuffer.colorToIntArray(c));
    }

    public void drawLine(ScreenBuffer screenBuffer, Color c, Vector3D v1, Vector3D v2)
    {
        int x1 = (int) v1.x();
        int y1 = (int) v1.y();
        int x2 = (int) v2.x();
        int y2 = (int) v2.y();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // Draw the pixel at the current position
            drawPixel(screenBuffer, c, x1, y1);

            // If we have reached the endpoint, break the loop
            if (x1 == x2 && y1 == y2) break;

            // Calculate error and adjust the x or y coordinate
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    public void fillTriangle(ScreenBuffer screenBuffer, MeshTriangle t)
    {
        BufferedImage colour = new BufferedImage(1, 1, TYPE_INT_ARGB);
        colour.setRGB(0,0,t.getMaterial().getShadedColour().getRGB());
        textureTriangle(screenBuffer, t, colour);
    }

    public void textureTriangle(ScreenBuffer screenBuffer, MeshTriangle tri, BufferedImage sprite)
    {
        Vector3D[] points = tri.getPoints();
        Vector2D[] texPoints = tri.getMaterial().getTextureCoords();

        int x1 = (int) points[0].x(); int y1 = (int) points[0].y();
        int x2 = (int) points[1].x(); int y2 = (int) points[1].y();
        int x3 = (int) points[2].x(); int y3 = (int) points[2].y();

        double u1 = texPoints[0].u(); double v1 = texPoints[0].v(); double w1 = texPoints[0].w();
        double u2 = texPoints[1].u(); double v2 = texPoints[1].v(); double w2 = texPoints[1].w();;
        double u3 = texPoints[2].u(); double v3 = texPoints[2].v(); double w3 = texPoints[2].w();

        Texturizer.textureTriangle(screenBuffer,
                x1,y1,u1,v1,w1,x2,y2,u2,v2,w2,x3,y3,u3,v3,w3,
                tri.getMaterial().getLuminance(),sprite);
    }
}
