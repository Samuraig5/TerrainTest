package Engine3d.DevTools;

public final class Log {
    private static Console console;
    private Log() {}

    public static void bind(Console c) {
        console = c;
    }

    public static void println(String message) {
        if (console != null) {
            console.println(message);
        }
        else {
            System.err.println(message);
        }
    }
}
