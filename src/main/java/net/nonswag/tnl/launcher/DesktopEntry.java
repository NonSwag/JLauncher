package net.nonswag.tnl.launcher;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class DesktopEntry {

    private final BufferedImage image;

    public DesktopEntry() throws FileNotFoundException {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("test.png");
            if (stream == null) throw new FileNotFoundException();
            image = ImageIO.read(stream);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }
}
