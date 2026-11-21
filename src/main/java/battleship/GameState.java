package battleship;

import java.io.Serializable;

public record GameState(int turn, int winner, char[][] ownBoard, char[][] opponentBoard)
        implements Serializable {
}
