package Levels.Persistence;

import Engine3d.Objects.Object3D;
import Engine3d.Objects.ObjectSource;
import Engine3d.Scene;
import Math.Vector.Vector3D;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LevelIO {
    public static LevelData snapshot(Scene scene) {
        List<Object3D.ObjectRecord> out = new ArrayList<>();
        for (Object3D o : scene.getObjects()) {
            if (o.getObjectSource() == null) continue;          // skip engine-managed objects
            out.add(new Object3D.ObjectRecord(
                    o.getObjectSource(),
                    toArr(o.getPosition()),
                    toArr(o.getRotation()),
                    toArr(o.getScale())));
        }
        return new LevelData(out);
    }

    public static void restore(LevelData data, Scene scene) {
        for (Object3D o : scene.getObjects()) {
            if (o.getObjectSource() != null) scene.removeObject(o);
        }

        for (Object3D.ObjectRecord r : data.objectRecords()) {
            Object3D obj;
            if (r.source() instanceof ObjectSource.ModelSource) {
                ObjectSource.ModelSource m = (ObjectSource.ModelSource) r.source();
                obj = scene.loadFromFile(m.path(), m.fileName());
            }
            else {
                obj = new Object3D(scene);
            }
            obj.translate(vec(r.position()));
            obj.rotate(vec(r.rotation()));
            obj.setScale(vec(r.scale()));
        }
    }

    private static double[] toArr(Vector3D v){ return new double[]{v.x(), v.y(), v.z()}; }
    private static Vector3D vec(double[] a){ return new Vector3D(a[0], a[1], a[2]); }

    public static void save(LevelData data, String path) throws IOException {
        Path p = Path.of(path);
        if (p.getParent() != null) Files.createDirectories(p.getParent());  // make "Levels/saved" if missing

        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            for (Object3D.ObjectRecord r : data.objectRecords()) {
                w.write(toLine(r));
                w.newLine();
            }
        }
    }

    public static LevelData load(String path) throws IOException {
        List<Object3D.ObjectRecord> out = new ArrayList<>();
        try (BufferedReader rd = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.isBlank()) continue;
                out.add(fromLine(line));
            }
        }
        return new LevelData(out);
    }

// --- line <-> record ---

    private static String toLine(Object3D.ObjectRecord r) {
        String head;
        if (r.source() instanceof ObjectSource.ModelSource m) {
            head = "MODEL|" + m.path() + "|" + m.fileName();
        } else if (r.source() instanceof ObjectSource.BoxSource b) {
            head = "BOX|" + b.sx() + " " + b.sy() + " " + b.sz();
        } else {
            throw new IllegalArgumentException("Unknown source: " + r.source());
        }
        return head + "|" + nums(r.position()) + "|" + nums(r.rotation()) + "|" + nums(r.scale());
    }

    private static Object3D.ObjectRecord fromLine(String line) throws IOException {
        String[] f = line.split("\\|");
        ObjectSource src;
        switch (f[0]) {
            case "MODEL" -> src = new ObjectSource.ModelSource(f[1], f[2]);
            case "BOX"   -> { double[] s = parse3(f[1]);
                src = new ObjectSource.BoxSource(s[0], s[1], s[2]); }
            default      -> throw new IOException("Unknown kind: " + f[0]);
        }
        int n = f.length;   // transforms are always the last three fields
        return new Object3D.ObjectRecord(src, parse3(f[n-3]), parse3(f[n-2]), parse3(f[n-1]));
    }

    private static String nums(double[] a) { return a[0] + " " + a[1] + " " + a[2]; }

    private static double[] parse3(String s) {
        String[] p = s.trim().split("\\s+");
        return new double[]{ Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]) };
    }
}
