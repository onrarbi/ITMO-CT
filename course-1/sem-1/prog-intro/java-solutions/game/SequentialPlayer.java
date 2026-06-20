package game;

public class SequentialPlayer implements Player {
    public SequentialPlayer() {}
    @Override
    public Move move(final Position position, final Cell cell) {
        int m = position.getM();
        int n = position.getN();

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                final Move move = new Move(r, c, cell);
                if (position.isValid(move)) {
                    return move;
                }
            }
        }

        throw new InvalidMoveException("No valid moves");
    }

    @Override
    public String toString() {
        return "Sequential";
    }

}