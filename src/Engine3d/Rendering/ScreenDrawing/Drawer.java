package Engine3d.Rendering.ScreenDrawing;

import Engine3d.Math.Vector.Vector2D;
import Engine3d.Rendering.Camera;
import Engine3d.Rendering.PlayerCamera;
import Engine3d.Math.MeshTriangle;
import Engine3d.Math.Vector.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Drawer
{
    static double SHADING_HARSHNESS = 0; //The lower this is the darker the shadows will be (0-1)
    Camera camera;
    PixelDrawer p;

    public Drawer(Camera camera)
    {
        this.camera = camera;
        p = new PixelDrawer(camera);
    }

    public void drawBuffer(Graphics g, ScreenBuffer screenBuffer)
    {
        BufferedImage buffer = screenBuffer.getBufferedImage();
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
        Vector3D[] points = t.getPoints();
        Vector2D[] texPoints = t.getMaterial().getTextureCoords();
        Vector3D[] depthPoints = new Vector3D[3];
        for (int i = 0; i < 3; i++) {
            double x = points[i].x();
            double y = points[i].y();
            double w = texPoints[i].w();
            depthPoints[i] = new Vector3D(x,y,0,w);
        }

        drawLine(c,depthPoints[0],depthPoints[1]);
        drawLine(c,depthPoints[1],depthPoints[2]);
        drawLine(c,depthPoints[2],depthPoints[0]);
    }

    public void fillTriangle(MeshTriangle t)
    {
        p.fillTriangle(camera.getScreenBuffer(), t);
    }

    public void textureTriangle(MeshTriangle tri)
    {
        BufferedImage sprite = tri.getMaterial().getTexture();
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
        if (color1 == null || color2 == null) {return Color.magenta;}
        // Normalize RGB components to the range [0, 1]
        float red1 = color1.getRed() / 255.0f;
        float green1 = color1.getGreen() / 255.0f;
        float blue1 = color1.getBlue() / 255.0f;
        float alpha1 = color1.getAlpha() / 255.0f;

        float red2 = color2.getRed() / 255.0f;
        float green2 = color2.getGreen() / 255.0f;
        float blue2 = color2.getBlue() / 255.0f;
        float alpha2 = color2.getAlpha() / 255.0f;

        // Multiply corresponding components
        float redResult = red1 * red2;
        float greenResult = green1 * green2;
        float blueResult = blue1 * blue2;
        float alphaResult = alpha1 * alpha2;

        // Convert back to the range [0, 255] and clamp
        int red = Math.min(255, Math.max(0, Math.round(redResult * 255)));
        int green = Math.min(255, Math.max(0, Math.round(greenResult * 255)));
        int blue = Math.min(255, Math.max(0, Math.round(blueResult * 255)));
        int alpha = Math.min(255, Math.max(0, Math.round(alphaResult * 255)));

        // Create and return the resulting color
        return new Color(red, green, blue, alpha);
    }
    public static Color alphaBlend(Color color1, Color color2) {
        float alpha1 = color1.getAlpha() / 255.0f;
        float alpha2 = color2.getAlpha() / 255.0f;

        float red = (color1.getRed() * alpha1 + color2.getRed() * alpha2 * (1 - alpha1));
        float green = (color1.getGreen() * alpha1 + color2.getGreen() * alpha2 * (1 - alpha1));
        float blue = (color1.getBlue() * alpha1 + color2.getBlue() * alpha2 * (1 - alpha1));
        float alphaResult = alpha1 + alpha2 * (1 - alpha1);

        // Normalize to [0, 255]
        int redResult = Math.round(red / alphaResult);
        int greenResult = Math.round(green / alphaResult);
        int blueResult = Math.round(blue / alphaResult);
        int alphaResultInt = Math.round(alphaResult * 255);

        return new Color(redResult, greenResult, blueResult, alphaResultInt);
    }

    public void drawDebugTriangle(Color c, MeshTriangle t)
    {
        Vector3D[] points = t.getPoints();
        Vector2D[] texPoints = t.getMaterial().getTextureCoords();
        Vector3D[] depthPoints = new Vector3D[3];
        for (int i = 0; i < 3; i++) {
            double x = points[i].x();
            double y = points[i].y();
            double w = texPoints[i].w() * 1f; //Make these pixels be drawn with a slight preference
            depthPoints[i] = new Vector3D(x,y,0,w);
        }

        drawLine(c,depthPoints[0],depthPoints[1]);
        drawLine(c,depthPoints[1],depthPoints[2]);
        drawLine(c,depthPoints[2],depthPoints[0]);
    }

    public static boolean colourEmpty(Color c, float tolerance)
    {
        int tol = (int) (tolerance*255);
        return c.getRed() <= tol && c.getGreen() <= tol && c.getBlue() <= tol && c.getAlpha() == 0;
    }
}
