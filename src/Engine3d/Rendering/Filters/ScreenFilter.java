package Engine3d.Rendering.Filters;

import Engine3d.Rendering.ScreenDrawing.ScreenBuffer;

public interface ScreenFilter {
    void apply(ScreenBuffer buffer);
    default boolean isDone() {
        return false;
    }
}