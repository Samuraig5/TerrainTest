package Engine3d.Objects;

public sealed interface ObjectSource permits
        ObjectSource.ModelSource,
        ObjectSource.BoxSource {
    record ModelSource(String path, String fileName) implements ObjectSource {}
    record BoxSource(double sx, double sy, double sz) implements ObjectSource {}
}
