package game;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int mode;

        do {
            System.out.println("Choose game mode: 1 - Classic game, 2 - Tournament");

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter 1 or 2");
                scanner.next();
            }

            mode = scanner.nextInt();
        } while (mode != 1 && mode != 2);

        if (mode == 1) {
            playClassicGame(scanner);
        } else {
            playTournament(scanner);
        }

        scanner.close();
    }

    private static int[] readBoardParameters(Scanner scanner) {
        System.out.println("Enter M N and K: ");

        int m = scanner.nextInt();
        int n = scanner.nextInt();
        int k = scanner.nextInt();

        return new int[]{m, n, k};
    }

    private static Player[] createPlayers(Scanner scanner, int count) {
    Player[] players = new Player[count];

    for (int i = 0; i < count; i++) {
        while (true) {
            System.out.println("Player " + (i + 1) + " type: 1 - Human; 2 - Sequential; 3 - Random. Enter type (1-3):");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number 1-3");
                scanner.next();
                continue;
            }

            int type = scanner.nextInt();
            switch (type) {
                case 1:
                    players[i] = new HumanPlayer(System.out, scanner);
                    break;
                case 2:
                    players[i] = new SequentialPlayer();
                    break;
                case 3:
                    players[i] = new RandomPlayer();
                    break;
                default:
                    System.out.println("Unknown type '" + type + "'. Please enter: 1, 2 or 3");
                    continue;
            }
            break;
        }
    }

    return players;
}

    private static void playTournament(Scanner scanner) {
        int[] params = readBoardParameters(scanner);

        System.out.println("Enter number of players: ");

        int playersCount = scanner.nextInt();
        Player[] players = createPlayers(scanner, playersCount);
        Tournament tournament = new Tournament(params[0], params[1], params[2]);

        for (Player player : players) {
            tournament.addPlayer(player);
        }

        tournament.playTournament();
        tournament.printScore();
    }

    private static void playClassicGame(Scanner scanner) {
        int[] params = readBoardParameters(scanner);
        Player[] players = createPlayers(scanner, 2);

        Game game = new Game(false, players[0], players[1]);
        int result = game.play(new MNKBoard(params[0], params[1], params[2]));
        printGameResult(result, players[0], players[1]);
    }

    private static void printGameResult(int result, Player player1, Player player2) {
        switch (result) {
            case 1:
                System.out.println(player1 + " (X) wins!");
                break;
            case 2:
                System.out.println(player2 + " (O) wins!");
                break;
            case 0:
                System.out.println("Draw between " + player1 + " and " + player2 + "!");
                break;
        }
    }
}