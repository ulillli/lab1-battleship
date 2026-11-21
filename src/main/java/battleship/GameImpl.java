package battleship;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

// Методы synchronized: вызовы разных клиентов RMI выполняет в разных потоках
public class GameImpl extends UnicastRemoteObject implements Game {
    public static final int PORT = 1099;

    // Ссылки в static, иначе сборщик мусора может удалить объект до подключения клиентов
    private static Registry registry;
    private static GameImpl game;

    private final Field[] fields = {new Field(), new Field()};
    private final String[] lastShot = {"", ""};
    private int players = 0;
    private int turn = 0;
    private int winner = -1;

    public GameImpl() throws RemoteException {
        super();
    }

    public synchronized int join() {
        if (players == 2) {
            return -1;
        }
        return players++;
    }

    public synchronized int getTurn() {
        return players < 2 ? -1 : turn;
    }

    public synchronized int getWinner() {
        return winner;
    }

    public synchronized String getBoards(int player) {
        String text = "Ваше поле:\n" + fields[player].draw(true)
                + "Поле соперника:\n" + fields[1 - player].draw(false)
                + "# - корабль, X - попадание, o - промах\n";
        if (!lastShot[1 - player].isEmpty()) {
            text += "Последний выстрел соперника: " + lastShot[1 - player] + "\n";
        }
        return text;
    }

    public synchronized String shoot(int player, int row, int col) {
        if (player != turn || winner != -1) {
            return "сейчас не ваш ход";
        }
        Field enemy = fields[1 - player];
        String result = enemy.shoot(row, col);
        lastShot[player] = (char) ('A' + col) + "" + (row + 1) + " - " + result;
        if (enemy.isLost()) {
            winner = player;
        } else if (result.equals("мимо")) {
            turn = 1 - player;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        // Адрес, который RMI запишет в заглушку: без него Java может выбрать адрес виртуального адаптера
        System.setProperty("java.rmi.server.hostname", args.length > 0 ? args[0] : "localhost");
        game = new GameImpl();
        registry = LocateRegistry.createRegistry(PORT);
        registry.bind("Battleship", game);
        System.out.println("Сервер RMI готов, ожидание игроков...");
    }
}
