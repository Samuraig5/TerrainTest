package Engine3d.Model;

import java.awt.*;

public class MTL
{
    String name;
    String colourTexture;
    Color diffuseColour;
    public MTL(String name) {
        this.name = name;
        this.setDiffuseColour(new Color(255,255,255,255));
    }
    public MTL(MTL source) {
        this.name = source.name;
        this.colourTexture = source.colourTexture;
        this.diffuseColour = source.diffuseColour;
    }

    public void setColourTexture(String colourTexture) {
        this.colourTexture = colourTexture;
    }
    public String getColourTexture() {
        return colourTexture;
    }

    public void setDiffuseColour(Color diffuseColour) {
        this.diffuseColour = diffuseColour;
    }
    public Color getDiffuseColour() {
        return diffuseColour;
    }
}
