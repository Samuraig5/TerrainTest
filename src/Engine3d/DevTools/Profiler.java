package Engine3d.DevTools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Profiler {
    private static final Map<String, AtomicLong> durations = new ConcurrentHashMap<>();
    private static final Map<String, Rate> rates = new ConcurrentHashMap<>();

    public static Span span(String name) {
        return new Span(name);
    }
    public static void count(String name) { rates.computeIfAbsent(name, k -> new Rate()).tick(); }
    public static double rate(String name) { Rate r = rates.get(name); return r == null ? 0 : r.perSecond; }

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

    private static final class Rate {
        private long windowStart = System.nanoTime();
        private int count = 0;
        volatile double perSecond = 0;
        synchronized void tick() {
            count++;
            long now = System.nanoTime(), elapsed = now - windowStart;
            if (elapsed >= 1_000_000_000L) { perSecond = count * 1e9 / elapsed; count = 0; windowStart = now; }
        }
    }
}
