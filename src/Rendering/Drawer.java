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
    PixelDrawer p;
    SpriteManager spriteManager = new SpriteManager();

    public Drawer(Camera camera)
    {
        this.camera = camera;
        p = new PixelDrawer(camera);
    }

    public void clearScreen(Graphics2D g, Color backgroundColour)
    {
        p.recomputeDepthBuffer();
        fillBackground(g, backgroundColour);
    }

    public void drawLine(Graphics2D g, Color c, Vector3D v1, Vector3D v2) {
        p.drawLine(g, c, v1, v2);
    }

    public void drawTriangle(Graphics2D g, Color c, Triangle t)
    {
        g.setColor(c);
        Vector3D points[] = t.getPoints();
        drawLine(g,c,points[0],points[1]);
        drawLine(g,c,points[1],points[2]);
        drawLine(g,c,points[2],points[0]);
    }

    public void fillTriangle(Graphics2D g, Triangle t)
    {
        p.fillTriangle(g, t);
    }

    public void textureTriangle(Graphics2D g, Triangle tri)
    {
        BufferedImage sprite = spriteManager.getResource(tri.getMaterial().getTexturePath());
        if (sprite == null) {System.err.println("Drawer: Triangle doesn't have a texture!"); return;}

        p.textureTriangle(g,tri,sprite);
    }

    private void fillBackground(Graphics2D g, Color c)
    {
        p.fillRectangle(g,c,camera.getResolution());
    }

    public static Color getColourShade(Color baseColor, double luminance) {
        luminance = Math.max(SHADING_HARSHNESS, Math.min(1, luminance)); //Clamp value for safety

        int red = (int) (baseColor.getRed() * luminance);
        int green = (int) (baseColor.getGreen() * luminance);
        int blue = (int) (baseColor.getBlue() * luminance);

        return new Color(red, green, blue);
    }
    public void recomputeDepthBuffer()
    {
        p.recomputeDepthBuffer();
    }
}
