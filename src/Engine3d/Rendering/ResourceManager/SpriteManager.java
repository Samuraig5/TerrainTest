package Engine3d.Rendering.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteManager extends ResourceManager<BufferedImage>
{
    @Override
    protected BufferedImage loadResource(String filepath)
    {
        try
        {
            return ImageIO.read(new File(filepath));
        }
        catch (IOException e1)
        {
            System.err.println("ImageManager: Unable to open: " + filepath);
            return null;
            /*
            if (filepath.equals(Settings.missingTextureSprite))
            {
                logger.error("CRITICAL ERROR: UNABLE TO LOAD 'MISSING TEXTURE'!");
                System.exit(0);
                return null;
            }
            logger.debug("IOException when trying to open: " + filepath + ". Opening 'Missing Texture' instead ");
            return getResource(Settings.missingTextureSprite);
             */
        }
    }
}
