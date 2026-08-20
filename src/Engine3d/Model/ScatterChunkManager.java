package Engine3d.Model;

import Engine3d.Scene;
import Engine3d.Time.Updatable;
import Engine3d.Translatable;
import Math.Vector.Vector3D;
import Physics.Object3D;

import java.awt.image.BufferedImage;
import java.util.*;

public class ScatterChunkManager implements Updatable {
    private static final class Chunk {
        final BillboardScatterMesh mesh;
        Chunk(BillboardScatterMesh mesh) { this.mesh = mesh; }
    }

    private final Translatable player;
    private final int chunkSize, radius;
    private final ChunkPopulator populator;
    private final List<Chunk> pool = new ArrayList<>();
    private final Map<Long, Chunk> active = new HashMap<>();
    private int lastPcx = Integer.MIN_VALUE, lastPcz = Integer.MIN_VALUE;

    public ScatterChunkManager(Scene scene, Translatable player, BufferedImage sprite,
                               int chunkSize, int radius, ChunkPopulator populator,
                               BillboardScatterMesh.Wind wind) {
        this.player = player;
        this.chunkSize = chunkSize;
        this.radius = radius;
        this.populator = populator;

        int count = (2*radius+1) * (2*radius+1);
        for (int i = 0; i < count; i++) {
            BillboardScatterMesh mesh = new BillboardScatterMesh(sprite);
            mesh.add(wind);
            Object3D obj = new Object3D(scene);   // stays at origin, auto-added to scene
            obj.setMesh(mesh);
            pool.add(new Chunk(mesh));
        }
        refresh(true);   // initial fill
    }

    @Override public void update(double deltaTime) { refresh(false); }

    private void refresh(boolean force) {
        Vector3D p = player.getPosition();
        int pcx = (int)Math.floor(p.x() / chunkSize);
        int pcz = (int)Math.floor(p.z() / chunkSize);
        if (!force && pcx == lastPcx && pcz == lastPcz) return;
        lastPcx = pcx; lastPcz = pcz;

        // coords we want around the player now
        Set<Long> desired = new HashSet<>();
        for (int dz = -radius; dz <= radius; dz++)
            for (int dx = -radius; dx <= radius; dx++)
                desired.add(key(pcx + dx, pcz + dz));

        // chunks now out of range become free for reuse
        List<Chunk> free = new ArrayList<>();
        Iterator<Map.Entry<Long, Chunk>> it = active.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Chunk> e = it.next();
            if (!desired.contains(e.getKey())) { free.add(e.getValue()); it.remove(); }
        }
        if (force) { free.clear(); free.addAll(pool); active.clear(); }

        // assign freed chunks to desired coords that aren't populated yet
        int fi = 0;
        for (long dkey : desired) {
            if (active.containsKey(dkey)) continue;
            Chunk c = free.get(fi++);
            List<BillboardScatterMesh.Instance> list = new ArrayList<>();
            populator.populate((int)(dkey >> 32), (int) dkey, chunkSize, list);
            c.mesh.setInstances(list);   // atomic swap
            active.put(dkey, c);
        }
    }

    private static long key(int cx, int cz) {
        return (((long) cx) << 32) | (cz & 0xffffffffL);
    }
}
