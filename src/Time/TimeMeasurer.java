package Time;

public class TimeMeasurer
{
    private double timeOfLastFrame;
    private int frameCount;
    private int fps;
    public double getFPS()
    {
        long currentTime = System.nanoTime();
        frameCount++;

        if (currentTime - timeOfLastFrame >= 1_000_000_000) { // 1 second has passed
            fps = frameCount;
            frameCount = 0;
            timeOfLastFrame = currentTime;
        }
        return fps;
    }
}
