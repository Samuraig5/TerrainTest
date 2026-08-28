package Engine3d.DevTools;

import Engine3d.Scene;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Console extends KeyAdapter {
    private final Scene scene;

    private boolean open = false;

    private final StringBuilder input = new StringBuilder();
    private final List<String> output = new ArrayList<>();
    private final Map<String, Consumer<String[]>> commands = new HashMap<>();

    public Console(Scene scene) {
        this.scene = scene;
    }

    // COMMANDS
    public void registerCommand(String name, Consumer<String[]> action) {
        commands.put(name, action);
    }

    public void println(String line) {
        output.add(line);
    }

    public boolean isOpen() {
        return open;
    }

    public void toggle() {
        open = !open;
        scene.setConsoleOpen(open);
        if (!open) {
            input.setLength(0);
        }
    }

    // INPUT
    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == '§' || c == '`' || c == '~') {
            toggle();
            return;
        }
        if (!open) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER -> submit();
            case KeyEvent.VK_BACK_SPACE -> {
                if (!input.isEmpty()) {
                    input.deleteCharAt(input.length() - 1);
                }
            }
            case KeyEvent.VK_ESCAPE -> toggle();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!open) {
            return;
        }
        char c = e.getKeyChar();
        if (c == '§' || c == '`' || c == '~') {
            return;
        }
        if (c >= ' ' && c != KeyEvent.CHAR_UNDEFINED) {
            input.append(c);
        }
    }

    private void  submit() {
        String line = input.toString().trim();
        input.setLength(0);
        if (line.isEmpty()) {
            return;
        }
        println("> " + line);
        String[] parts = line.split("\\s");
        Consumer<String[]> cmd = commands.get(parts[0]);
        if (cmd == null) {
            println("Unknown command: " + parts[0]);
            return;
        }
        try {
            cmd.accept(parts);
        }
        catch (Exception e) {
            println("Error: " + e.getMessage());
        }
    }

    // RENDERING
    public void render(Graphics g, int width, int height) {
        if (!open) {
            return;
        }
        int h = 160;
        g.setColor(new Color(0,0,0, 200));
        g.fillRect(0,0, width, h);
        g.setColor(Color.lightGray);
        int y = h - 30;
        for (int i = output.size()-1; i >= 0 && y > 12; i--, y-=16) {
            g.drawString(output.get(i), 8, y);
        }
        g.setColor(Color.white);
        g.drawString("> " + input + "_", 8, h - 8);
    }
}
