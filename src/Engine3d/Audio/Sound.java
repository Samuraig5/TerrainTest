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

    /** Fire-and-forget SFX at a given volume (0.0 = silent, 1.0 = full). */
    public static void play(String path, double volume) {
        AudioManager.Sample s = manager.getResource(path);
        if (s == null) return;
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(s.format(), s.data(), 0, s.data().length);
            setVolume(clip, volume);                 // ← before start()
            clip.addLineListener(e -> { if (e.getType() == LineEvent.Type.STOP) clip.close(); });
            clip.start();
        } catch (LineUnavailableException e) { System.err.println(e.getMessage()); }
    }

    private static void setVolume(Clip clip, double volume) {
        volume = Math.max(0, Math.min(1, volume));
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;   // not all lines have it
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (volume <= 0.0001f)
                ? gain.getMinimum()                          // effectively mute
                : (float) (20.0 * Math.log10(volume));       // linear → decibels
        gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
    }
}