package Rendering;

import WorldSpace.Triangle;

import java.awt.*;

public class Material
{
    private Color baseColour = Color.magenta;
    private double luminance;

    public Material(){}
    public Material(Material source)
    {
        this.baseColour = source.baseColour;
        this.luminance = source.luminance;
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
}
