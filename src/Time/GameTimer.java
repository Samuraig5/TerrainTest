package Time;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameTimer implements ActionListener
{
    private final Timer timer;
    private final List<Updatable> updatables = new ArrayList<>();

    public GameTimer()
    {
        timer = new Timer(16, this);
        timer.start();
    }

    public void subscribe(Updatable updatable)
    {
        updatables.add(updatable);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        for (Updatable updatable : updatables) {
            updatable.update(timer.getDelay());
        }
    }
}
