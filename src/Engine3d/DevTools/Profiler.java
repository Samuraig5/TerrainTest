package Engine3d.DevTools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Profiler {
    private static final Map<String, AtomicLong> durations = new ConcurrentHashMap<>();

    public static Span span(String name) {
        return new Span(name);
    }

    public static double ms(String name) {
        AtomicLong v = durations.get(name);
        if (v == null) {
            return 0;
        }
        return v.get() / 1000000.0;
    }

    public static final class Span implements AutoCloseable {
        private final String name;
        private final long start = System.nanoTime();
        private Span(String name) {
            this.name = name;
        }

        @Override
        public void close() {
            long elapsed = System.nanoTime() - start;
            durations.computeIfAbsent(name, k -> new AtomicLong()).set(elapsed);
        }
    }
}
