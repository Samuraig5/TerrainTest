package Engine3d.Rendering;

import java.awt.*;

public class DrawInstructions
{
    public boolean drawWireFrame;
    public boolean drawFlatColour;
    public boolean drawTexture;
    public boolean doShading;
    public Color wireFrameColour = new Color(255, 255, 255, 255);
    public boolean ignorePixelDepth = false;
    public DrawInstructions(boolean drawWireFrame, boolean drawFlatColour, boolean drawTexture, boolean doShading) {
        this.drawWireFrame = drawWireFrame;
        this.drawFlatColour = drawFlatColour;
        this.drawTexture = drawTexture;
        this.doShading = doShading;
    }
}
