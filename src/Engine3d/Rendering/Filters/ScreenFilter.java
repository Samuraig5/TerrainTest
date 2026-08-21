package Engine3d.Rendering.Filters;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;
import Math.Vector.Vector3D;

public interface ScreenFilter {
    void apply(ScreenBuffer buffer, Vector3D camPos, Vector3D camDir);
    default boolean isDone() {
        return false;
    }
}