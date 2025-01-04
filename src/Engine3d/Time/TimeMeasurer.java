package Engine3d.Time;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TimeMeasurer {
    public boolean measureTime = false;
    private long timeOfLastFrame = System.nanoTime();
    private int frameCount = 0;
    private volatile int fps;

    private final HashMap<String, Long> measurementMap = new HashMap<>();
    private final HashMap<String, Long> startTime = new HashMap<>();

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

    private void startSelfMeasurement()
    {
        measurementMap.putIfAbsent("SelfMeasurement", 0L);
        startTime.put("SelfMeasurement",  System.nanoTime());
    }
    private void stopSelfMeasurement()
    {
        Long start = startTime.get("SelfMeasurement");
        Long total = measurementMap.get("SelfMeasurement");
        if (start == null || total == null) {return;}
        long currentTime = System.nanoTime();
        long elapsed = currentTime - start;
        measurementMap.put("SelfMeasurement", total + elapsed);
        startTime.remove("SelfMeasurement"); // Stop tracking the start time
    }
    public String getSelfMeasurement() {
        return getMsPrintOut("SelfMeasurement");
    }

    public void startMeasurement(String measurementName) {
        if (!measureTime) {return;}
        startSelfMeasurement();
        measurementMap.putIfAbsent(measurementName, 0L);
        startTime.put(measurementName, System.nanoTime());
        stopSelfMeasurement();
    }

    public void stopMeasurement(String measurementName) {
        if (!measureTime) {return;}
        startSelfMeasurement();
        updateMeasurement(measurementName);
        startTime.remove(measurementName); // Stop tracking the start time
        stopSelfMeasurement();
    }

    public long getMeasurement(String measurementName) {
        if (!measureTime) {measureTime = true;}
        startSelfMeasurement();
        measurementMap.putIfAbsent(measurementName, 0L);
        updateMeasurement(measurementName);
        stopSelfMeasurement();
        return measurementMap.get(measurementName);
    }

    private void updateMeasurement(String measurementName) {
        if (!measureTime) {return;}
        startSelfMeasurement();
        Long start = startTime.get(measurementName);
        Long total = measurementMap.get(measurementName);

        if (start == null || total == null) {
            return; // Measurement not started or doesn't exist
        }

        long currentTime = System.nanoTime();
        long elapsed = currentTime - start;
        measurementMap.put(measurementName, total + elapsed);
        startTime.put(measurementName, currentTime);

        stopSelfMeasurement();
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
