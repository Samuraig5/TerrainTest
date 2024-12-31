package Controls;

import WorldSpace.Translatable;
import WorldSpace.Vector3D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Controller implements KeyListener
{
    private List<Translatable> attachedTranslatables = new ArrayList<>();

    public void attachTranslatable(Translatable translatable){attachedTranslatables.add(translatable);}

    @Override
    public void keyPressed(KeyEvent e) {
        double step = 10; // Camera movement speed
        Vector3D delta = new Vector3D(0,0,0);
        System.out.println("Out");
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> delta.translate(0, 0, step); // Move forward
            case KeyEvent.VK_S -> delta.translate(0, 0, -step); // Move backward
            case KeyEvent.VK_A -> delta.translate(-step, 0, 0); // Move left
            case KeyEvent.VK_D -> delta.translate(step, 0, 0); // Move right
            case KeyEvent.VK_SPACE -> delta.translate(0, -step, 0); // Move up
            case KeyEvent.VK_SHIFT -> delta.translate(0, step, 0); // Move down
        }
        updateTranslatables(delta);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed for key releases in this example
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed for key typing in this example
    }

    private void updateTranslatables(Vector3D delta)
    {
        for (Translatable trans : attachedTranslatables) {
            trans.translate(delta);
        }
    }
}
