package Engine3d.Time;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TimeMeasurer {
    private long timeOfLastFrame = System.nanoTime();
    private int frameCount = 0;
    private volatile int fps;

    private final ConcurrentHashMap<String, AtomicLong> measurementMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> startTime = new ConcurrentHashMap<>();

    public double getFPS() {
        long currentTime = System.nanoTime();
        frameCount++;

        if (currentTime - timeOfLastFrame >= 1_000_000_000) { // 1 second has passed
            fps = frameCount;
            frameCount = 0;
            timeOfLastFrame = currentTime;
        }
        return fps;
    }

    public void clearMeasurements() {
        startTime.clear();
        measurementMap.clear();
    }

    public void startMeasurement(String measurementName) {
        measurementMap.putIfAbsent(measurementName, new AtomicLong(0));
        startTime.put(measurementName, new AtomicLong(System.nanoTime()));
    }

    public void stopMeasurement(String measurementName) {
        updateMeasurement(measurementName);
        startTime.remove(measurementName); // Stop tracking the start time
    }

    public long getMeasurement(String measurementName) {
        measurementMap.putIfAbsent(measurementName, new AtomicLong(0));
        updateMeasurement(measurementName);
        return measurementMap.get(measurementName).get();
    }

    private void updateMeasurement(String measurementName) {
        AtomicLong start = startTime.get(measurementName);
        AtomicLong total = measurementMap.get(measurementName);

        if (start == null || total == null) {
            return; // Measurement not started or doesn't exist
        }

        long currentTime = System.nanoTime();
        long elapsed = currentTime - start.get();
        total.addAndGet(elapsed);

        // Reset the start time for continuous measurement
        start.set(currentTime);
    }

    public double getRelativeMeasurement(long measurement, long comparison)
    {
        double result = ((double) measurement / (double) comparison) * 100;
        return Math.round(result * 10) / 10.0;
    }

    public double nanoToMili(long ns) {
        return ns / 1_000_000.0;
    }

    public String getMsPrintOut(String measurementName)
    {
        return measurementName + ": " + nanoToMili(getMeasurement(measurementName)) + "ms";
    }

    public String getPercentAndMsPrintOut(String measurementName, long comparison)
    {
        long measurement = getMeasurement(measurementName);
        double percentage = getRelativeMeasurement(measurement, comparison);
        return "(" + percentage + "%) " + measurementName + ": " + nanoToMili(measurement) + "ms";
    }
}
