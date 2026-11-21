package battleship;

import java.util.Random;

public class Field {
    public static final int SIZE = 10;

    // 0 - вода, 1..10 - номер корабля в клетке
    private final int[][] ships = new int[SIZE][SIZE];
    private final boolean[][] shot = new boolean[SIZE][SIZE];
    private final int[] decksLeft = new int[11];
    private int shipsLeft = 10;

    public Field() {
        int[] sizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        Random random = new Random();
        for (int n = 1; n <= 10; n++) {
            int size = sizes[n - 1];
            boolean vertical;
            int row;
            int col;
            do {
                vertical = random.nextBoolean();
                row = random.nextInt(vertical ? SIZE - size + 1 : SIZE);
                col = random.nextInt(vertical ? SIZE : SIZE - size + 1);
            } while (!isFree(row, col, size, vertical));

            for (int k = 0; k < size; k++) {
                if (vertical) {
                    ships[row + k][col] = n;
                } else {
                    ships[row][col + k] = n;
                }
            }
            decksLeft[n] = size;
        }
    }

    private boolean isFree(int row, int col, int size, boolean vertical) {
        int lastRow = vertical ? row + size - 1 : row;
        int lastCol = vertical ? col : col + size - 1;
        for (int i = Math.max(row - 1, 0); i <= Math.min(lastRow + 1, SIZE - 1); i++) {
            for (int j = Math.max(col - 1, 0); j <= Math.min(lastCol + 1, SIZE - 1); j++) {
                if (ships[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public String shoot(int row, int col) {
        if (shot[row][col]) {
            return "сюда уже стреляли";
        }
        shot[row][col] = true;
        int n = ships[row][col];
        if (n == 0) {
            return "мимо";
        }
        decksLeft[n]--;
        if (decksLeft[n] > 0) {
            return "ранил";
        }
        shipsLeft--;
        return "убил";
    }

    public boolean isLost() {
        return shipsLeft == 0;
    }

    public char[][] view(boolean showShips) {
        char[][] cells = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                char ch = '~';
                if (shot[r][c]) {
                    ch = ships[r][c] != 0 ? 'X' : 'o';
                } else if (showShips && ships[r][c] != 0) {
                    ch = '#';
                }
                cells[r][c] = ch;
            }
        }
        return cells;
    }

    public String draw(boolean showShips) {
        char[][] cells = view(showShips);
        StringBuilder sb = new StringBuilder("   A B C D E F G H I J\n");
        for (int row = 0; row < SIZE; row++) {
            sb.append(String.format("%2d ", row + 1));
            for (char cell : cells[row]) {
                sb.append(cell).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
