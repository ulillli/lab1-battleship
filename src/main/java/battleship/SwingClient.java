package battleship;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ExecutionException;

public class SwingClient {
    private final Game game;
    private final int player;
    private final BoardPanel ownBoard = new BoardPanel();
    private final BoardPanel opponentBoard = new BoardPanel();
    private final JLabel status = new JLabel("Загрузка поля…");
    private final JLabel lastShot = new JLabel("Кликните по клетке соперника в свой ход.");
    private final Timer timer = new Timer(500, event -> {
        if (!isMyTurn()) {
            requestState(null);
        }
    });
    private GameState state;
    private boolean requestInProgress;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        try {
            Game game = (Game) LocateRegistry.getRegistry(host, GameImpl.PORT).lookup("Battleship");
            int player = game.join();
            if (player == -1) {
                showMessage("В игре уже два игрока.", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            SwingUtilities.invokeLater(() -> new SwingClient(game, player).show());
        } catch (RemoteException | NotBoundException e) {
            showMessage("Не удалось подключиться к серверу " + host + ".", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showMessage(String message, int type) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Морской бой", type));
    }

    private SwingClient(Game game, int player) {
        this.game = game;
        this.player = player;
    }

    private void show() {
        JPanel boards = new JPanel(new GridLayout(1, 2, 12, 0));
        boards.add(withTitle("Ваше поле", ownBoard));
        boards.add(withTitle("Поле соперника", opponentBoard));

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(status, BorderLayout.NORTH);
        content.add(boards, BorderLayout.CENTER);
        content.add(lastShot, BorderLayout.SOUTH);

        opponentBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    shootAt(opponentBoard.cellAt(event.getPoint()));
                }
            }
        });

        JFrame frame = new JFrame("Морской бой — игрок " + (player + 1));
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        requestState(null);
        timer.start();
    }

    private JPanel withTitle(String title, BoardPanel board) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(board);
        return panel;
    }

    private void shootAt(Point cell) {
        if (cell != null && isMyTurn()
                && state.opponentBoard()[cell.y][cell.x] == '~') {
            requestState(cell);
        }
    }

    private boolean isMyTurn() {
        return state != null && state.winner() == -1 && state.turn() == player;
    }

    private void requestState(Point shot) {
        if (requestInProgress) {
            return;
        }
        requestInProgress = true;
        new SwingWorker<GameState, Void>() {
            private String shotResult;

            @Override
            protected GameState doInBackground() throws RemoteException {
                if (shot != null) {
                    shotResult = game.shoot(player, shot.y, shot.x);
                }
                return game.getState(player);
            }

            @Override
            protected void done() {
                try {
                    updateBoards(get());
                    if (shotResult != null) {
                        String coordinate = String.valueOf((char) ('A' + shot.x)) + (shot.y + 1);
                        lastShot.setText("Ваш выстрел " + coordinate + " — " + shotResult);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    timer.stop();
                    state = null;
                    status.setText("Связь с сервером потеряна.");
                } finally {
                    requestInProgress = false;
                }
            }
        }.execute();
    }

    private void updateBoards(GameState state) {
        this.state = state;
        ownBoard.setCells(state.ownBoard());
        opponentBoard.setCells(state.opponentBoard());
        if (state.winner() != -1) {
            timer.stop();
            status.setText(state.winner() == player ? "Вы победили!" : "Вы проиграли.");
        } else if (state.turn() == -1) {
            status.setText("Ожидаем второго игрока…");
        } else {
            status.setText(state.turn() == player ? "Ваш ход" : "Ход соперника");
        }
    }
}
