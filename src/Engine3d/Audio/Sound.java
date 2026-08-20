package Engine3d.Audio;

import javax.sound.sampled.*;

public class Sound {
    private static final AudioManager manager = new AudioManager();

    /** Fire-and-forget SFX. Overlaps freely; releases its line when done. */
    public static void play(String path) {
        AudioManager.Sample s = manager.getResource(path);
        if (s == null) return;
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(s.format(), s.data(), 0, s.data().length);
            clip.addLineListener(e -> { if (e.getType() == LineEvent.Type.STOP) clip.close(); });
            clip.start();
        } catch (LineUnavailableException e) { System.err.println(e.getMessage()); }
    }

    /** Looping music. Returns the Clip so you can stop()/close() it later. */
    public static Clip playLoop(String path) {
        AudioManager.Sample s = manager.getResource(path);
        if (s == null) return null;
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(s.format(), s.data(), 0, s.data().length);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            return clip;
        } catch (LineUnavailableException e) { System.err.println(e.getMessage()); return null; }
    }
}