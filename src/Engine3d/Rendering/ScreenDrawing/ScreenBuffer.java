package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Math.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class ScreenBuffer
{
    private BufferedImage bufferedImage;
    private double[][] depthBuffer;

    public ScreenBuffer(Vector3D screenSize)
    {
        recompute(screenSize);
    }

    public void clear(int[] clearColour) {
        WritableRaster raster = bufferedImage.getRaster();
        int width = bufferedImage.getWidth(); int height = bufferedImage.getHeight();
        raster.setPixels(0, 0, width, height, getSample(width, height, clearColour));
        depthBuffer = new double[depthBuffer.length][depthBuffer[0].length];
    }
    public void clear() {
        int[] clearColor = new int[4];
        clear(clearColor);
    }
    public void clear(Color c) {
        clear(colorToIntArray(c));
    }
    public void recompute(Vector3D screenSize)
    {
        bufferedImage = new BufferedImage((int)screenSize.x(), (int)screenSize.y(),TYPE_INT_ARGB);
        depthBuffer = new double[(int) screenSize.x()][(int) screenSize.y()];
    }

    public boolean pixelOnTop(int x, int y, double depth) {
        return (depth > depthBuffer[x][y]);
    }
    public void updateDepth(int x, int y, double depth) {
        depthBuffer[x][y] = depth;
    }
    public boolean inBounds(int x, int y){
        return (x >= 0 && x < bufferedImage.getWidth()) && (y >= 0 && y < bufferedImage.getHeight());
    }
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int[] getSample(int width, int height, int[] colour)
    {
        int[] sample = new int[width * height * 4]; // 4 components per pixel (RGBA)

        int red = colour[0];
        int green = colour[1];
        int blue = colour[2];
        int alpha = colour[3];
        for (int i = 0; i < width * height; i++) {
            int baseIndex = i * 4; // Calculate the base index for each pixel (RGBA)
            sample[baseIndex] = red;     // Set red
            sample[baseIndex + 1] = green;   // Set green
            sample[baseIndex + 2] = blue;    // Set blue
            sample[baseIndex + 3] = alpha;   // Set alpha
        }
        return sample;
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
