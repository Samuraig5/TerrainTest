package Engine3d.Rendering.ScreenDrawing;

import Math.Vector.Vector2D;
import Engine3d.Rendering.Camera;
import Math.MeshTriangle;
import Math.Vector.Vector3D;

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

    public void drawLine(Color c, Vector3D v1, Vector3D v2, boolean checkPixelDepth) {
        p.drawLine(camera.getScreenBuffer(), c, v1, v2, checkPixelDepth);
    }

    public void drawTriangle(Color c, MeshTriangle t, boolean checkDepth)
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

        drawLine(c,depthPoints[0],depthPoints[1], checkDepth);
        drawLine(c,depthPoints[1],depthPoints[2], checkDepth);
        drawLine(c,depthPoints[2],depthPoints[0], checkDepth);
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

    public static int getColourShade(int baseColor, double luminance) {
        luminance = Math.max(SHADING_HARSHNESS, Math.min(1, luminance)); //Clamp value for safety
        int luminanceInt = (int) (luminance * 255);

        int luminanceColour = (255 << 24) | (luminanceInt << 16) | (luminanceInt << 8) | luminanceInt;

        int colourOUT = multiplyColors(baseColor, luminanceColour);

        return colourOUT;
    }
    public static int multiplyColors(int colour1, int colour2) {
        if (colour1 == 0 || colour2 == 0) {return 0;}

        int alpha1 = (colour1 >> 24) & 0xFF;
        int red1 = (colour1 >> 16) & 0xFF;
        int green1 = (colour1 >> 8) & 0xFF;
        int blue1 = colour1 & 0xFF;

        int alpha2 = (colour2 >> 24) & 0xFF;
        int red2 = (colour2 >> 16) & 0xFF;
        int green2 = (colour2 >> 8) & 0xFF;
        int blue2 = colour2 & 0xFF;

        int alphaOUT = (alpha1 * alpha2) / 255;
        int redOUT = (red1 * red2) / 255;
        int greenOUT = (green1 * green2) / 255;
        int blueOUT = (blue1 * blue2) / 255;

        int colourOUT = (alphaOUT << 24) | (redOUT << 16) | (greenOUT << 8) | blueOUT;

        return colourOUT;
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

    public static boolean colourEmpty(Color c, float tolerance)
    {
        int tol = (int) (tolerance*255);
        return c.getRed() <= tol && c.getGreen() <= tol && c.getBlue() <= tol && c.getAlpha() == 0;
    }
}
