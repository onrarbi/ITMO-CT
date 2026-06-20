package game;

import java.util.*;

public class Tournament {
    private final List<Player> players;
    private final Map<Player, Integer> score;
    private final int m, n, k;
    private static final int WIN_POINTS = 3;
    private static final int DRAW_POINTS = 1;

    public Tournament(int m, int n, int k) {
        this.players = new ArrayList<>();
        this.score = new HashMap<>();
        this.m = m;
        this.n = n;
        this.k = k;
    }

    public void addPlayer(Player player) {
        players.add(player);
        score.put(player, 0);
    }

    public void playTournament() {
        for (int i = 0; i < players.size(); i++) {

            for (int j = i + 1; j < players.size(); j++) {
                playMatch(players.get(i), players.get(j));
                playMatch(players.get(j), players.get(i));
            }
        }
    }

    private void playMatch(Player player1, Player player2) {
        System.out.println(player1 + " (X) vs " + player2 + " (O)");

        Game game = new Game(false, player1, player2);
        int result = game.play(new MNKBoard(m, n, k));

        printMatchResult(result, player1, player2);
        updateScore(player1, player2, result);
    }

    private void printMatchResult(int result, Player player1, Player player2) {
        switch (result) {
            case 1:
                System.out.println("Result: " + player1 + " wins!");
                break;
            case 2:
                System.out.println("Result: " + player2 + " wins!");
                break;
            case 0:
                System.out.println("Result: Draw!");
                break;
        }
    }

    private void updateScore(Player player1, Player player2, int result) {
        switch (result) {
            case 1:
                score.put(player1, score.get(player1) + WIN_POINTS);
                break;
            case 2:
                score.put(player2, score.get(player2) + WIN_POINTS);
                break;
            case 0:
                score.put(player1, score.get(player1) + DRAW_POINTS);
                score.put(player2, score.get(player2) + DRAW_POINTS);
                break;
        }
    }

    public void printScore() {
        List<Map.Entry<Player, Integer>> sorted = new ArrayList<>(score.entrySet());
        sorted.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int place = 1;

        for (Map.Entry<Player, Integer> entry : sorted) {
            System.out.println(place + ". " + entry.getKey() + ": " + entry.getValue() + " points");
            place++;
        }
    }
}