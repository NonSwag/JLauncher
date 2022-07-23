package net.nonswag.tnl.launcher;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

public class JLauncher {

    @Nonnull
    public static final JFrame WINDOW = new JFrame("JLauncher");
    @Nonnull
    public static final Screen SCREEN = new Screen();

    static {
        DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        WINDOW.setPreferredSize(new Dimension(display.getWidth() / 2, display.getHeight() / 2));
        WINDOW.setMinimumSize(new Dimension(display.getWidth() / 3, display.getHeight() / 3));
        WINDOW.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        WINDOW.setContentPane(SCREEN);
        WINDOW.pack();
        WINDOW.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        WINDOW.setVisible(true);
    }
}
