package Engine3d.Rendering.ScreenDrawing;

import Math.Vector.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class ScreenBuffer
{
    private BufferedImage bufferedImage;
    private int[] pixels;  // Direct pixel array
    private int width;
    private int height;
    private double[][] depthBuffer;
    private Object[] columnLocks;

    public ScreenBuffer(Vector3D screenSize)
    {
        recompute(screenSize);
    }

    public void clear(int[] clearColour) {
        int clearARGB = (clearColour[3] << 24) | (clearColour[0] << 16) | (clearColour[1] << 8) | clearColour[2];
        Arrays.fill(pixels, clearARGB);
        for (double[] column : depthBuffer) {
            Arrays.fill(column, 0.0);
        }
    }
    public void clear() {
        int[] clearColor = new int[4];
        clear(clearColor);
    }
    public void clear(Color c) {
        clear(colorToIntArray(c));
    }
    public void recompute(Vector3D screenSize) {
        width = (int) screenSize.x();
        height = (int) screenSize.y();
        bufferedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData(); //'pixels' points to memory space of BufferedImage pixel array
        depthBuffer = new double[width][height];
        columnLocks = new Object[width];
        for (int i = 0; i < width; i++) {
            columnLocks[i] = new Object();
        }
    }

    public boolean inBounds(int x, int y){
        return (x >= 0 && x < width) && (y >= 0 && y < height);
    }

    public void writePixelIfOnTop(int x, int y, double depth, int colour) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        synchronized (columnLocks[x]) {
            if (depth > depthBuffer[x][y]) {
                depthBuffer[x][y] = depth;
                int flippedY = height - y - 1;
                pixels[flippedY * width + x] = colour;  // Direct array write
            }
        }
    }

    public void setPixel(int x, int y, int colour) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        int flippedY = height - y - 1;
        pixels[flippedY * width + x] = colour;  // Direct array write
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public static int colorToARGB(Color color){
        int alpha = color.getAlpha();
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public static int[] colorToIntArray(Color color) {
        return new int[] {
                color.getRed(),    // Extract red component (0-255)
                color.getGreen(),  // Extract green component (0-255)
                color.getBlue(),   // Extract blue component (0-255)
                color.getAlpha()   // Extract alpha component (0-255)
        };
    }
}
