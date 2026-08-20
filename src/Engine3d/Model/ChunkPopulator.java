package Engine3d.Model;

import java.util.List;

public interface ChunkPopulator {
    /** Fill `out` with instances for chunk (cx,cz), in WORLD coordinates. Must be deterministic. */
    void populate(int cx, int cz, int chunkSize, List<BillboardScatterMesh.Instance> out);
}