package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Rendering.Camera;
import Engine3d.Rendering.ResourceManager.SpriteManager;
import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Drawer
{
    static double SHADING_HARSHNESS = 0.2d; //The lower this is the darker the shadows will be (0-1)
    Camera camera;
    PixelDrawer p;
    SpriteManager spriteManager = new SpriteManager();

    public Drawer(Camera camera)
    {
        this.camera = camera;
        p = new PixelDrawer(camera);
    }

    public void drawBuffer(Graphics g)
    {
        BufferedImage buffer = camera.getScreenBuffer().getBufferedImage();
        int screenWidth = (int) (buffer.getWidth() / camera.getResolutionFactor());
        int screenHeight = (int) (buffer.getHeight() / camera.getResolutionFactor());

        g.drawImage(buffer, 0, 0, screenWidth, screenHeight,
                                0, 0, buffer.getWidth(), buffer.getHeight(),
                                null);


    }

    public void drawLine(Color c, Vector3D v1, Vector3D v2) {
        p.drawLine(camera.getScreenBuffer(), c, v1, v2);
    }

    public void drawTriangle(Color c, MeshTriangle t)
    {
        Vector3D points[] = t.getPoints();
        drawLine(c,points[0],points[1]);
        drawLine(c,points[1],points[2]);
        drawLine(c,points[2],points[0]);
    }

    public void fillTriangle(MeshTriangle t)
    {
        p.fillTriangle(camera.getScreenBuffer(), t);
    }

    public void textureTriangle(MeshTriangle tri)
    {
        BufferedImage sprite = spriteManager.getResource(tri.getMaterial().getTexturePath());
        if (sprite == null) {System.err.println("Drawer: Triangle doesn't have a texture!"); return;}

        p.textureTriangle(camera.getScreenBuffer(),tri,sprite);
    }

    public static Color getColourShade(Color baseColor, double luminance) {
        luminance = Math.max(SHADING_HARSHNESS, Math.min(1, luminance)); //Clamp value for safety

        int red = (int) (baseColor.getRed() * luminance);
        int green = (int) (baseColor.getGreen() * luminance);
        int blue = (int) (baseColor.getBlue() * luminance);

        return new Color(red, green, blue);
    }
    public static Color multiplyColors(Color color1, Color color2) {
        // Normalize RGB components to the range [0, 1]
        float red1 = color1.getRed() / 255.0f;
        float green1 = color1.getGreen() / 255.0f;
        float blue1 = color1.getBlue() / 255.0f;

        float red2 = color2.getRed() / 255.0f;
        float green2 = color2.getGreen() / 255.0f;
        float blue2 = color2.getBlue() / 255.0f;

        // Multiply corresponding components
        float redResult = red1 * red2;
        float greenResult = green1 * green2;
        float blueResult = blue1 * blue2;

        // Convert back to the range [0, 255] and clamp
        int red = Math.min(255, Math.max(0, Math.round(redResult * 255)));
        int green = Math.min(255, Math.max(0, Math.round(greenResult * 255)));
        int blue = Math.min(255, Math.max(0, Math.round(blueResult * 255)));

        // Create and return the resulting color
        return new Color(red, green, blue);
    }
}
