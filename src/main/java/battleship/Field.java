package battleship;

import java.util.Random;

public class Field {
    // 0 - вода, 1..10 - номер корабля в клетке
    private final int[][] ships = new int[10][10];
    private final boolean[][] shot = new boolean[10][10];
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
                row = random.nextInt(vertical ? 11 - size : 10);
                col = random.nextInt(vertical ? 10 : 11 - size);
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
        for (int i = Math.max(row - 1, 0); i <= Math.min(lastRow + 1, 9); i++) {
            for (int j = Math.max(col - 1, 0); j <= Math.min(lastCol + 1, 9); j++) {
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

    public String draw(boolean showShips) {
        StringBuilder sb = new StringBuilder("   A B C D E F G H I J\n");
        for (int r = 0; r < 10; r++) {
            sb.append(String.format("%2d ", r + 1));
            for (int c = 0; c < 10; c++) {
                char ch = '~';
                if (shot[r][c]) {
                    ch = ships[r][c] != 0 ? 'X' : 'o';
                } else if (showShips && ships[r][c] != 0) {
                    ch = '#';
                }
                sb.append(ch).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
