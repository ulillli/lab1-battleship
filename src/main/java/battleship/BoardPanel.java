package battleship;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

class BoardPanel extends JPanel {
    private static final int CELL = 28;
    private static final int MARGIN = 28;
    private static final int EDGE = Field.SIZE * CELL;
    private static final Color WATER = new Color(225, 240, 248);
    private static final Color GRID = new Color(134, 164, 182);
    private static final Color SHIP = new Color(59, 93, 113);
    private static final Color HIT = new Color(167, 40, 40);

    private char[][] cells = new char[Field.SIZE][Field.SIZE];

    BoardPanel() {
        setPreferredSize(new Dimension(MARGIN + EDGE + 8, MARGIN + EDGE + 8));
    }

    void setCells(char[][] cells) {
        this.cells = cells;
        repaint();
    }

    Point cellAt(Point point) {
        int x = point.x - MARGIN;
        int y = point.y - MARGIN;
        if (x < 0 || y < 0 || x >= EDGE || y >= EDGE) {
            return null;
        }
        return new Point(x / CELL, y / CELL);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getForeground());
        for (int i = 0; i < Field.SIZE; i++) {
            int offset = MARGIN + i * CELL;
            drawCentered(g, String.valueOf((char) ('A' + i)), offset + CELL / 2, MARGIN / 2);
            drawCentered(g, Integer.toString(i + 1), MARGIN / 2, offset + CELL / 2);
        }

        for (int row = 0; row < Field.SIZE; row++) {
            for (int col = 0; col < Field.SIZE; col++) {
                int x = MARGIN + col * CELL;
                int y = MARGIN + row * CELL;
                char cell = cells[row][col];
                g.setColor(cell == '#' ? SHIP : WATER);
                g.fillRect(x, y, CELL, CELL);
                g.setColor(GRID);
                g.drawRect(x, y, CELL, CELL);
                if (cell == 'X' || cell == 'o') {
                    g.setColor(cell == 'X' ? HIT : SHIP);
                    drawCentered(g, String.valueOf(cell), x + CELL / 2, y + CELL / 2);
                }
            }
        }
        g.setColor(GRID);
        g.drawRect(MARGIN, MARGIN, EDGE, EDGE);
    }

    private void drawCentered(Graphics g, String text, int x, int y) {
        FontMetrics font = g.getFontMetrics();
        g.drawString(text, x - font.stringWidth(text) / 2,
                y + (font.getAscent() - font.getDescent()) / 2);
    }
}
