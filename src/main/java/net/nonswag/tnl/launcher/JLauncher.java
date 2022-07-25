package net.nonswag.tnl.launcher;

import net.nonswag.tnl.core.Core;
import net.nonswag.tnl.launcher.images.ImageAspect;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class JLauncher {

    @Nonnull
    public static final JFrame WINDOW = new JFrame("JLauncher");
    @Nonnull
    public static final Screen SCREEN = new Screen();
    @Nonnull
    public static final Image BACKGROUND;

    static {
        try {
            InputStream stream = JLauncher.class.getClassLoader().getResourceAsStream("background.jpg");
            if (stream == null) throw new FileNotFoundException("background.jpg");
            BACKGROUND = ImageAspect.scale(ImageIO.read(stream), SCREEN.getWidth(), SCREEN.getHeight());
            WINDOW.setPreferredSize(new Dimension(SCREEN.getWidth(), SCREEN.getHeight()));
            WINDOW.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            WINDOW.setContentPane(SCREEN);
            WINDOW.setUndecorated(true);
            WINDOW.setResizable(false);
            WINDOW.pack();
            WINDOW.setLocationRelativeTo(null);
            Runtime.getRuntime().addShutdownHook(new Thread(Screen.DESKTOP::save, "shutdown-thread"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Core.main(args);
        WINDOW.setVisible(true);
    }
}
