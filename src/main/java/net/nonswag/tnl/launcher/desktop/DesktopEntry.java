package net.nonswag.tnl.launcher.desktop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.nonswag.tnl.core.api.file.formats.ShellFile;
import net.nonswag.tnl.core.utils.LinuxUtil;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class DesktopEntry {

    @Nonnull
    @Setter(AccessLevel.NONE)
    private BufferedImage image;
    @Nonnull
    private String name, icon;
    @Nonnull
    private File file;
    @Nonnull
    private final ShellFile shellFile;

    public DesktopEntry(@Nonnull String name, @Nonnull File file) throws FileNotFoundException {
        this(name, "default.png", file);
    }

    public DesktopEntry(@Nonnull String name, @Nonnull String icon, @Nonnull File file) throws FileNotFoundException {
        try {
            this.name = name;
            this.file = file;
            this.shellFile = new ShellFile(".jlauncher", "%s.sh".formatted(name)) {{
                String command = "java -jar %s".formatted(file.getAbsolutePath());
                if (getContent().length == 0) setContent(new String[]{command}).save();
            }};
            setIcon(icon);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }

    public void setIcon(@Nonnull String icon) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(this.icon = icon);
        if (stream == null) throw new FileNotFoundException(icon);
        this.image = ImageIO.read(stream);
    }

    public void launch() throws Exception {
        if (!getFile().exists()) throw new FileNotFoundException("File not found");
        LinuxUtil.runShellCommand("/bin/bash %s".formatted(getShellFile().getFile().getAbsolutePath()));
    }
}
