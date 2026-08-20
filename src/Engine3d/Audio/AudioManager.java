package Engine3d.Audio;

import Engine3d.Rendering.ResourceManager.ResourceManager;
import javax.sound.sampled.*;
import java.io.File;

public class AudioManager extends ResourceManager<AudioManager.Sample> {
    public record Sample(byte[] data, AudioFormat format) {}

    @Override
    protected Sample loadResource(String filepath) {
        try (AudioInputStream in = AudioSystem.getAudioInputStream(new File(filepath))) {
            return new Sample(in.readAllBytes(), in.getFormat());
        } catch (Exception e) {
            System.err.println("AudioManager: unable to load " + filepath + ": " + e.getMessage());
            return null;
        }
    }
}