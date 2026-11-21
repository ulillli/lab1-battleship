package battleship;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Game extends Remote {
    int join() throws RemoteException;

    int getTurn() throws RemoteException;

    int getWinner() throws RemoteException;

    String getBoards(int player) throws RemoteException;

    GameState getState(int player) throws RemoteException;

    String shoot(int player, int row, int col) throws RemoteException;
}
