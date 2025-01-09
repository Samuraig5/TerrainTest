package Engine3d.Rendering;

import Engine3d.Model.MTL;
import Engine3d.Rendering.ScreenDrawing.Drawer;
import Math.Vector.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Material
{
    private double luminance = 1;
    private final Vector2D[] textureCoords = new Vector2D[3];
    private BufferedImage texture;
    private MTL mtl = new MTL("Empty");

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
        this.luminance = source.luminance;
        for (int i = 0; i < 3; i++) {
            textureCoords[i] = new Vector2D(source.getTextureCoords()[i]);
        }
        this.setTexture(source.getTexture());
        this.mtl = source.getMTL();
    }

    public Color getShadedColour() {
        return Drawer.getColourShade(getBaseColour(), luminance);
    }

    public void setBaseColour(Color baseColour) {
        mtl.setDiffuseColour(baseColour);
    }
    public Color getBaseColour() {
        return mtl.getDiffuseColour();
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

    public MTL getMTL() {
        return mtl;
    }
    public void setMTL(MTL mtl) {
        this.mtl = mtl;
    }
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }
    public BufferedImage getTexture() {
        return texture;
    }
    public String getTexturePath() {
        return mtl.getColourTexture();
    }
    public void setTexturePath(String texturePath) {
        mtl.setColourTexture(texturePath);
    }
}
