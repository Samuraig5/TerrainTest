package Engine3d.Rendering;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;

public interface SkyBox {
    void render(ScreenBuffer buffer, Camera camera);   // paint the background before geometry
}
