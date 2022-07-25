package net.nonswag.tnl.launcher;

import net.nonswag.tnl.launcher.desktop.Desktop;
import net.nonswag.tnl.launcher.desktop.DesktopEntry;
import net.nonswag.tnl.launcher.images.ImageAspect;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Screen extends JPanel {

    @Nonnull
    public static Color MARK = new Color(18, 126, 221, 75), TEXT = Color.BLACK;
    @Nonnull
    public static Font FONT = new Font("Ubuntu", Font.PLAIN, 15);
    @Nonnull
    private static final JPopupMenu ENTRY_ACTION_MENU = new JPopupMenu(), NEW_ACTION_MENU = new JPopupMenu();
    public static boolean SHOW_GRID = false, MARK_NULL_SELECTION = false;
    public static int ROWS, COLUMNS, TILE_SIZE;

    private int selected = -1;

    @Nonnull
    private final Runnable NEW_ENTRY = () -> {
        try {
            if (selected < 0 || selected >= ROWS * COLUMNS || DESKTOP.containsKey(selected)) return;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select a jar file");
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(@Nonnull File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith(".jar");
                }

                @Override
                public String getDescription() {
                    return "*.jar";
                }
            });
            if (chooser.showOpenDialog(this) == 0) {
                File file = chooser.getSelectedFile();
                String name;
                if (!file.getName().toLowerCase().endsWith(".jar")) name = file.getName();
                else name = file.getName().substring(0, file.getName().length() - 4);
                chooser.setSelectedFile(null);
                chooser.setCurrentDirectory(null);
                chooser.resetChoosableFileFilters();
                chooser.setFileHidingEnabled(false);
                chooser.setDialogTitle("Select the running directory");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (chooser.showOpenDialog(this) == 0) {
                    DESKTOP.put(selected, new DesktopEntry(name, file, chooser.getSelectedFile()).editShellFile());
                }
            }
            repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }, DELETE_ENTRY = () -> {
        DesktopEntry entry = DESKTOP.get(selected);
        if (entry != null) entry.getShellFile().delete();
        DESKTOP.remove(selected);
        repaint();
    }, LAUNCH_ENTRY = () -> {
        DesktopEntry entry = DESKTOP.get(selected);
        if (entry != null) new Thread(() -> {
            try {
                entry.launch();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                repaint();
            }
        }, "proxy-instance (%s)".formatted(entry.getName())).start();
        else repaint();
    }, EDIT_ENTRY = () -> {
        DesktopEntry entry = DESKTOP.get(selected);
        if (entry != null) entry.editShellFile();
        repaint();
    };

    static {
        DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        TILE_SIZE = 64;
        ROWS = display.getHeight() / TILE_SIZE;
        COLUMNS = display.getWidth() / TILE_SIZE;
    }

    {
        JMenuItem delete = new JMenuItem("Delete", KeyEvent.VK_D);
        JMenuItem launch = new JMenuItem("Launch", KeyEvent.VK_L);
        JMenuItem edit = new JMenuItem("Edit", KeyEvent.VK_E);
        JMenuItem newEntry = new JMenuItem("New", KeyEvent.VK_N);

        delete.addActionListener(actionEvent -> DELETE_ENTRY.run());
        newEntry.addActionListener(actionEvent -> NEW_ENTRY.run());
        launch.addActionListener(actionEvent -> LAUNCH_ENTRY.run());
        edit.addActionListener(actionEvent -> EDIT_ENTRY.run());

        ENTRY_ACTION_MENU.add(launch);
        ENTRY_ACTION_MENU.add(delete);
        ENTRY_ACTION_MENU.add(edit);
        NEW_ACTION_MENU.add(newEntry);
    }

    @Nonnull
    public static final Desktop DESKTOP = new Desktop();

    public Screen() {
        setSize(COLUMNS * TILE_SIZE, ROWS * TILE_SIZE);
        setFocusable(true);
        setBackground(Color.BLACK);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(@Nonnull MouseEvent event) {
                int row = event.getY() / (getWidth() / COLUMNS);
                int column = event.getX() / (getHeight() / ROWS);
                selected = row * COLUMNS + column;
                if (event.getButton() == MouseEvent.BUTTON3 && selected >= 0 && selected < ROWS * COLUMNS) {
                    JPopupMenu menu = DESKTOP.containsKey(selected) ? ENTRY_ACTION_MENU : NEW_ACTION_MENU;
                    menu.show(event.getComponent(), event.getX(), event.getY());
                } else if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) LAUNCH_ENTRY.run();
                repaint();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@Nonnull KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_UP) selected = getPreviousIndexUp();
                else if (event.getKeyCode() == KeyEvent.VK_DOWN) selected = getNextIndexDown();
                else if (event.getKeyCode() == KeyEvent.VK_LEFT) selected = getPreviousIndex(selected);
                else if (event.getKeyCode() == KeyEvent.VK_RIGHT) selected = getNextIndex(selected);
                else if (event.getKeyCode() == KeyEvent.VK_ENTER) LAUNCH_ENTRY.run();
                else if (event.getKeyCode() == KeyEvent.VK_DELETE) DELETE_ENTRY.run();
                else return;
                if (selected >= ROWS * COLUMNS) selected = ROWS * COLUMNS - 1;
                else if (selected < 0) selected = 0;
                repaint();
            }
        });
    }

    private int getNextIndexDown() {
        int index = selected + COLUMNS;
        return DESKTOP.containsKey(index) ? index : getNextIndex(selected);
    }

    private int getPreviousIndexUp() {
        int index = selected - COLUMNS;
        return DESKTOP.containsKey(index) ? index : getPreviousIndex(selected);
    }

    private int getPreviousIndex(int selected) {
        int previous = 0;
        for (int index : DESKTOP.keySet()) if (index > previous && index < selected) previous = index;
        return previous;
    }

    private int getNextIndex(int selected) {
        for (int index : DESKTOP.keySet()) if (index > selected) return index;
        return selected;
    }

    @Override
    public void paint(@Nonnull Graphics graphics) {
        super.paint(graphics);
        renderBackground(graphics);
        if (SHOW_GRID) renderGrid(graphics);
        renderEntries(graphics);
        graphics.dispose();
    }

    private void renderBackground(@Nonnull Graphics graphics) {
        graphics.drawImage(JLauncher.BACKGROUND, 0, 0, null);
    }

    private void renderEntries(@Nonnull Graphics graphics) {
        for (int index = 0, row = 0, column = 0; index < ROWS * COLUMNS; index++) {
            DesktopEntry entry = DESKTOP.get(index);
            int scaleX = getWidth() / COLUMNS;
            int scaleY = getHeight() / ROWS;
            int x = column * scaleX;
            int y = row * scaleY;
            if (entry != null) {
                graphics.setFont(FONT);
                graphics.setColor(TEXT);
                graphics.drawImage(ImageAspect.scale(entry.getImage(), scaleX - 1, scaleY - 1), x, y, null);
                graphics.drawString(entry.getName(), x, y + TILE_SIZE + 12);
            }
            graphics.setColor(MARK);
            if (selected == index && (MARK_NULL_SELECTION || entry != null)) graphics.fillRect(x - 1, y - 1, scaleX, scaleY);
            if (++column < COLUMNS) continue;
            column = 0;
            row++;
        }
    }

    private void renderGrid(@Nonnull Graphics graphics) {
        graphics.setColor(Color.DARK_GRAY);
        int rowHeight = getHeight() / ROWS;
        int rowWidth = getWidth() / COLUMNS;
        for (int i = 0; i < ROWS; i++) graphics.drawLine(0, i * rowHeight - 1, getWidth(), i * rowHeight - 1);
        for (int i = 0; i < COLUMNS; i++) graphics.drawLine(i * rowWidth - 1, 0, i * rowWidth - 1, getHeight());
    }
}
