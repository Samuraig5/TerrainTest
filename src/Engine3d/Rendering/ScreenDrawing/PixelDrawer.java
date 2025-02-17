package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Rendering.Camera;
import Math.MeshTriangle;
import Math.Vector.Vector2D;
import Math.Vector.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.abs;

public class PixelDrawer
{
    Camera camera;
    double[][] depthBuffer;
    public PixelDrawer(Camera camera)
    {
        this.camera = camera;
    }

    public static void drawPixel(ScreenBuffer screenBuffer, Color c, int x, int y) {
        screenBuffer.setPixel(x,y,c);
    }

    public static void checkAndDrawPixel(ScreenBuffer screenBuffer, Color c, int x, int y, double depth)
    {
        if (screenBuffer.pixelOnTop(x,y,depth))
        {
            PixelDrawer.drawPixel(screenBuffer, c, x, y);
            screenBuffer.updateDepth(x,y,depth);
        }
    }

    public void drawLine(ScreenBuffer screenBuffer, Color c, Vector3D v1, Vector3D v2, boolean ignorePixelDepth) {
        // Extract coordinates and depths from the input vectors
        int x1 = (int) v1.x();
        int y1 = (int) v1.y();
        double w1 = v1.w();

        int x2 = (int) v2.x();
        int y2 = (int) v2.y();
        double w2 = v2.w();

        // Calculate the differences and the direction of the line
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        double dw = Math.abs(w2 - w1);

        int stepX = x1 < x2 ? 1 : -1; // Step direction in x
        int stepY = y1 < y2 ? 1 : -1; // Step direction in y
        double stepW = w1 < w2 ? 1 : -1; // Step direction in w


        // Bresenham's algorithm variables
        int err = dx - dy;

        while (true) {
            // Interpolate the depth based on the line's progress
            double t = 0;
            if (dx + dy != 0) {
                t = Math.sqrt(
                        (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) /
                        (float) Math.sqrt((dx * dx) + (dy * dy)
                );
            }
            double depth = w2 * (1 - t) + w1 * t;;

            // Draw the current pixel
            if (ignorePixelDepth) {
                drawPixel(screenBuffer, c, x1, y1);
            }
            else {
                checkAndDrawPixel(screenBuffer, c, x1, y1, depth);
            }

            // Break when the line is complete
            if (x1 == x2 && y1 == y2) break;

            // Calculate the error and adjust coordinates
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += stepX;
            }
            if (e2 < dx) {
                err += dx;
                y1 += stepY;
            }
        }
    }

    public void fillTriangle(ScreenBuffer screenBuffer, MeshTriangle t)
    {
        BufferedImage colour = new BufferedImage(1, 1, TYPE_INT_ARGB);
        colour.setRGB(0,0,t.getMaterial().getShadedColour().getRGB());
        textureTriangle(screenBuffer, t, colour);
    }

    public synchronized void textureTriangle(ScreenBuffer screenBuffer, MeshTriangle tri, BufferedImage sprite)
    {
        Vector3D[] points = tri.getPoints();
        Vector2D[] texPoints = tri.getMaterial().getTextureCoords();

        int x1 = (int) points[0].x(); int y1 = (int) points[0].y();
        int x2 = (int) points[1].x(); int y2 = (int) points[1].y();
        int x3 = (int) points[2].x(); int y3 = (int) points[2].y();

        double u1 = texPoints[0].u(); double v1 = texPoints[0].v(); double w1 = texPoints[0].w();
        double u2 = texPoints[1].u(); double v2 = texPoints[1].v(); double w2 = texPoints[1].w();
        double u3 = texPoints[2].u(); double v3 = texPoints[2].v(); double w3 = texPoints[2].w();

        Texturizer.textureTriangle(screenBuffer,tri.getMaterial().getMTL(),
                x1,y1,u1,v1,w1,x2,y2,u2,v2,w2,x3,y3,u3,v3,w3,
                tri.getMaterial().getLuminance(),sprite);
    }
}
