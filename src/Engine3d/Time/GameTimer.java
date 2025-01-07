package Engine3d.Time;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameTimer implements ActionListener
{
    private final Timer timer;
    private long lastTime;
    private final List<Updatable> updatables = new CopyOnWriteArrayList<>();

    private TimeMeasurer timeMeasurer;

    public GameTimer() {
        timer = new Timer(16, this);
        lastTime = System.nanoTime();
        timer.start();
    }

    public void addTimeMeasurer(TimeMeasurer tm) {
        timeMeasurer = tm;
    }

    public void subscribe(Updatable updatable) {
        updatables.add(updatable);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (timeMeasurer != null) { timeMeasurer.startMeasurement("update"); }
        double deltaTime = deltaTime();
        for (Updatable updatable : updatables) {
            updatable.update(deltaTime);
        }
        if (timeMeasurer != null) { timeMeasurer.pauseAndEndMeasurement("update"); }
    }

    private double deltaTime()
    {
        long currentTime = System.nanoTime();
        long deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        return nanoToSec(deltaTime);
    }

    private double nanoToSec(long ns)
    {
        return (double) ns / 1_000_000_000;
    }
}
