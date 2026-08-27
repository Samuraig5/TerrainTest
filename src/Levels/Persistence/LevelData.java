package Levels.Persistence;

import Engine3d.Objects.Object3D;

import java.util.List;

public record LevelData(
        List<Object3D.ObjectRecord> objectRecords
) { }
