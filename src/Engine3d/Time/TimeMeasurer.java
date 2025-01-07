package Engine3d.Time;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TimeMeasurer {
    private final AtomicLong timeOfLastFrame = new AtomicLong(System.nanoTime());
    private final AtomicInteger frameCount = new AtomicInteger(0);
    private final AtomicInteger fps = new AtomicInteger(0);

    private final ConcurrentHashMap<String, AtomicLong> perSecondMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> cycleCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> timeOfLastCycle = new ConcurrentHashMap<>();


    private final ConcurrentHashMap<String, AtomicBoolean> newMeasurementMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> measurementMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> startTimeMap = new ConcurrentHashMap<>();

    public void addCycle(String key) {
        long currentTime = System.nanoTime();
        cycleCount.putIfAbsent(key, new AtomicLong());
        timeOfLastCycle.putIfAbsent(key, new AtomicLong(currentTime));
        perSecondMap.putIfAbsent(key, new AtomicLong());

        cycleCount.get(key).incrementAndGet();

        if (currentTime - timeOfLastCycle.get(key).get() >= 1_000_000_000L) { // 1 second
            perSecondMap.get(key).set(cycleCount.get(key).getAndSet(0));
            timeOfLastCycle.get(key).set(currentTime);
        }
    }
    public double getCyclesPerSecond(String key) {
        return perSecondMap.get(key).get();
    }

    public void clearMeasurements() {
        measurementMap.clear();
        startTimeMap.clear();
    }

    public void startMeasurement(String measurementName) {
        startTimeMap.put(measurementName, new AtomicLong(System.nanoTime()));
    }

    public void pauseMeasurement(String measurementName) {
        AtomicLong startTime = startTimeMap.get(measurementName);
        if (startTime == null) return;

        long elapsed = System.nanoTime() - startTime.get();

        if (!newMeasurementMap.getOrDefault(measurementName, new AtomicBoolean(false)).get()) {
            measurementMap.merge(measurementName, new AtomicLong(elapsed), (existing, added) -> {
                existing.addAndGet(added.get());
                return existing;
            });
        }
        else {
            measurementMap.put(measurementName, new AtomicLong(elapsed));
            newMeasurementMap.put(measurementName, new AtomicBoolean(false));
        }
        startTimeMap.remove(measurementName);
    }

    public void pauseAndEndMeasurement(String measurementName) {
        pauseMeasurement(measurementName);
        startTimeMap.remove(measurementName);
        newMeasurementMap.put(measurementName, new AtomicBoolean(true));
    }
    public void endMeasurement(String measurementName) {
        startTimeMap.remove(measurementName);
        newMeasurementMap.put(measurementName, new AtomicBoolean(true));
    }

    public long getMeasurement(String measurementName) {
        // If not measuring, start the measurement
        if (!newMeasurementMap.containsKey(measurementName) || !newMeasurementMap.get(measurementName).get()) {
            startMeasurement(measurementName);
        }
        return measurementMap.getOrDefault(measurementName, new AtomicLong(0)).get();
    }

    public double nanoToMilli(long ns) {
        return ns / 1_000_000.0;
    }

    public String getMsPrintOut(String measurementName) {
        return measurementName + ": " + nanoToMilli(getMeasurement(measurementName)) + "ms";
    }

    public String getMsPrintOut(String measurementName, long value) {
        return measurementName + ": " + nanoToMilli(value) + "ms";
    }

    public String getPercentAndMsPrintOut(String measurementName, long comparison) {
        long measurement = getMeasurement(measurementName);
        double percentage = getRelativeMeasurement(measurement, comparison);
        return "(" + percentage + "%) " + measurementName + ": " + nanoToMilli(measurement) + "ms";
    }

    public double getRelativeMeasurement(long measurement, long comparison) {
        return Math.round(((double) measurement / comparison) * 1000) / 10.0;
    }
}
