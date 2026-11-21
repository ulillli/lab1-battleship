package battleship;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        Registry registry = LocateRegistry.getRegistry(host, GameImpl.PORT);
        Game game = (Game) registry.lookup("Battleship");

        int me = game.join();
        if (me == -1) {
            System.out.println("В игре уже два игрока");
            return;
        }
        System.out.println("Вы игрок " + (me + 1) + ". Корабли расставлены автоматически, ждите своего хода...");

        Scanner scanner = new Scanner(System.in);
        while (game.getWinner() == -1) {
            if (game.getTurn() != me) {
                Thread.sleep(500);
                continue;
            }
            System.out.println(game.getBoards(me));
            System.out.print("Ваш выстрел (например, B7): ");
            String cell = scanner.nextLine().trim().toUpperCase();
            if (!cell.matches("[A-J]([1-9]|10)")) {
                System.out.println("Неверные координаты");
                continue;
            }
            int col = cell.charAt(0) - 'A';
            int row = Integer.parseInt(cell.substring(1)) - 1;
            System.out.println("Результат: " + game.shoot(me, row, col));
        }

        System.out.println(game.getBoards(me));
        System.out.println(game.getWinner() == me ? "Вы победили!" : "Вы проиграли.");
    }
}
