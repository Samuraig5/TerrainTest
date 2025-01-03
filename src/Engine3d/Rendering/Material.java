package Engine3d.Rendering;

import Engine3d.Rendering.ScreenDrawing.Drawer;
import Engine3d.Math.Vector2D;

import java.awt.*;

public class Material
{
    private Color baseColour = Color.magenta;
    private double luminance;
    private Vector2D[] textureCoords = new Vector2D[3];
    private String texturePath;

    public Material()
    {
        textureCoords[0] = new Vector2D();
        textureCoords[1] = new Vector2D();
        textureCoords[2] = new Vector2D();
    }
    public Material(Vector2D t1, Vector2D t2, Vector2D t3)
    {
        textureCoords[0] = new Vector2D(t1);
        textureCoords[1] = new Vector2D(t2);
        textureCoords[2] = new Vector2D(t3);
    }
    public Material(Material source)
    {
        this.baseColour = source.baseColour;
        this.luminance = source.luminance;
        for (int i = 0; i < 3; i++) {
            textureCoords[i] = new Vector2D(source.getTextureCoords()[i]);
        }
        setTexturePath(source.getTexturePath());
    }

    public Color getShadedColour() {
        return Drawer.getColourShade(getBaseColour(), luminance);
    }

    public void setBaseColour(Color baseColour) {
        this.baseColour = baseColour;
    }
    public Color getBaseColour() {
        return baseColour;
    }
    public void setLuminance(double luminance) {
        this.luminance = luminance;
    }
    public double getLuminance() {
        return luminance;
    }

    public Vector2D[] getTextureCoords() {
        return textureCoords;
    }
    public void setTextureCoords(Vector2D t0, Vector2D t1, Vector2D t2)
    {
        textureCoords[0] = t0;
        textureCoords[1] = t1;
        textureCoords[2] = t2;
    }
    public String getTexturePath() {
        return texturePath;
    }
    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
}
