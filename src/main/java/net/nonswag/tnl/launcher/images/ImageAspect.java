package net.nonswag.tnl.launcher.images;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageAspect {

    @Nonnull
    public static Image scale(@Nonnull BufferedImage image, int width, int height) {
        double scale, originalWidth = image.getWidth(), originalHeight = image.getHeight();
        if (width / originalWidth < height / originalHeight) {
            scale = width / originalWidth;
            return image.getScaledInstance((int) (scale * originalWidth), (int) (scale * originalHeight), Image.SCALE_SMOOTH);
        } else if (width / originalWidth > height / originalHeight) {
            scale = height / originalHeight;
            return image.getScaledInstance((int) (scale * originalWidth), (int) (scale * originalHeight), Image.SCALE_SMOOTH);
        } else if (width / originalWidth == height / originalHeight) {
            scale = width / originalWidth;
            return image.getScaledInstance((int) (scale * originalWidth), (int) (scale * originalHeight), Image.SCALE_SMOOTH);
        } else return image;
    }
}
