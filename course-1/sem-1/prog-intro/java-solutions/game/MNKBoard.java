package game;

import java.util.Arrays;

public class MNKBoard implements Board{
    private final Cell[][] cells;
    private Cell turn;
    private final int m;
    private final int n;
    private final int k;
    private int empty;

    public MNKBoard(int m, int n, int k) {
        this.m = m;
        this.n = n;
        this.k = k;
        this.cells = new Cell[m][n];

        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }

        this.turn = Cell.X;
        this.empty = m * n;
    }

    @Override
    public Position getPosition() {
        return new MNKPosition(cells, m, n);
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        int row = move.getRow();
        int col = move.getColumn();

        cells[row][col] = move.getValue();
        empty--;

        Cell curr = cells[row][col];
        
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int[] dir : directions) {
            if (checkCells(row, col, dir[0], dir[1], curr)) {
                return Result.WIN;
            }
        }
    
        if (empty == 0) {
            return Result.DRAW;
        }
        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;
    }

    private boolean checkCells(int row, int col, int rowStep, int colStep, Cell curr) {
        int count = 1;

        count += countDirection(row, col, rowStep, colStep, curr);
        count += countDirection(row, col, -rowStep, -colStep, curr);

        return count >= k;
    }

    private int countDirection(int row, int col, int rowStep, int colStep, Cell curr) {
        int count = 0;

        for (int i = 1; i < k; i++) {
            int r = row + i * rowStep;
            int c = col + i * colStep;

            if (r < 0 || r >= m || c < 0 || c >= n || cells[r][c] != curr) {
                break;
            }

            count++;
        }

        return count;
    }

    private boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && move.getValue() == turn;
    }
}