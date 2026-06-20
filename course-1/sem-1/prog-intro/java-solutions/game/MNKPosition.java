package game;

import java.util.Map;

public class MNKPosition implements Position{
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private final int m;
    private final int n;

    public MNKPosition(Cell[][] cells, int m, int n) {
        this.cells = cells;
        this.m = m;
        this.n = n;
    }

    @Override
    public Cell getCell(int r, int c) {
        return cells[r][c];
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("  ");

        for (int c = 0; c < n; c++) {
            sb.append(c);
        }

        for (int r = 0; r < m; r++) {
            sb.append("\n");
            sb.append(r).append(" ");

            for (int c = 0; c < n; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
            }
        }

        return sb.toString();
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E;
    }
}