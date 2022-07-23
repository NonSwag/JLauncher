package net.nonswag.tnl.launcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Screen extends JPanel {

    @Nonnull
    private static final List<DesktopEntry> ENTRIES = new ArrayList<>() {
        {
            try {
                for (int i = 0; i < 190; i++) add(new DesktopEntry());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean add(@Nullable DesktopEntry entry) {
            if (entry == null) throw new NullPointerException("entry cannot be null");
            if (size() < ROWS * COLUMNS) return super.add(entry);
            throw new IllegalStateException("Too much entries");
        }
    };
    @Nonnull
    private static final JPopupMenu ACTION_MENU = new JPopupMenu();
    private static final boolean SHOW_GRID = true;
    private static final int ROWS = 10, COLUMNS = 19;

    private int selected = 2;

    static {
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem add = new JMenuItem("Add");
        ACTION_MENU.add(delete);
        ACTION_MENU.add(add);
        ACTION_MENU.setVisible(false);
    }

    public Screen() {
        setFocusable(true);
        setBackground(Color.BLACK);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(@Nonnull MouseEvent event) {
                if (event.getButton() != MouseEvent.BUTTON3) return;
                ACTION_MENU.show(event.getComponent(), event.getX(), event.getY());
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@Nonnull KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_UP) {
                    selected -= COLUMNS;
                } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    selected += COLUMNS;
                } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                    selected--;
                } else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                    selected++;
                } else return;
                if (selected < 0) selected = 0;
                if (selected >= ROWS * COLUMNS) selected = ROWS * COLUMNS - 1;
                repaint();
            }
        });
    }

    @Override
    public void paint(@Nonnull Graphics graphics) {
        super.paint(graphics);
        if (SHOW_GRID) renderGrid(graphics);
        renderEntries(graphics);
        graphics.dispose();
    }

    private void renderEntries(@Nonnull Graphics graphics) {
        for (int index = 0, row = 0, column = 0; index < ENTRIES.size(); index++) {
            DesktopEntry entry = ENTRIES.get(index);
            int scaleX = getSize().width / COLUMNS;
            int scaleY = getSize().height / ROWS;
            int x = column * scaleX;
            int y = row * scaleY;
            graphics.drawImage(entry.getImage(), x, y, scaleX, scaleY, null);
            if (selected == index) {
                graphics.setColor(Color.RED);
                graphics.drawRect(x, y, scaleX - 1, scaleY - 1);
            }
            if (++column < COLUMNS) continue;
            column = 0;
            row++;
        }
    }

    private void renderGrid(@Nonnull Graphics graphics) {
        graphics.setColor(Color.GREEN);
        int rowHeight = getSize().height / ROWS;
        int rowWidth = getSize().width / COLUMNS;
        for (int i = 0; i < ROWS; i++) graphics.drawLine(0, i * rowHeight - 1, getSize().width, i * rowHeight - 1);
        for (int i = 0; i < COLUMNS; i++) graphics.drawLine(i * rowWidth - 1, 0, i * rowWidth - 1, getSize().height);
    }
}
