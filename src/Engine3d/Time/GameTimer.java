package Engine3d.Time;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameTimer implements ActionListener
{
    private final Timer timer;
    private long lastTime;
    private final List<Updatable> updatables = new ArrayList<>();

    public GameTimer() {
        timer = new Timer(16, this);
        lastTime = System.nanoTime();
        timer.start();
    }

    public void subscribe(Updatable updatable) {
        updatables.add(updatable);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double deltaTime = deltaTime();
        for (Updatable updatable : updatables) {
            updatable.update(deltaTime);
        }
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
