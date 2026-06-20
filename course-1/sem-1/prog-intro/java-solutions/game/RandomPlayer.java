package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random;
    private final int maxAttempts;

    public RandomPlayer(final Random random, int maxAttempts) {
        this.random = random;
        this.maxAttempts = maxAttempts;
    }

    public RandomPlayer(final Random random) {
        this(random, 1000);
    }

    public RandomPlayer() {
        this(new Random());
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        int m = position.getM();
        int n = position.getN();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int r = random.nextInt(m);
            int c = random.nextInt(n);
            Move move = new Move(r, c, cell);

            if (position.isValid(move)) {
                return move;
            }
        }

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                Move move = new Move(r, c, cell);

                if (position.isValid(move)) {
                    return move;
                }
            }
        }

        throw new InvalidMoveException("No valid moves");
    }

    @Override
    public String toString() {
        return "Random";
    }

}
