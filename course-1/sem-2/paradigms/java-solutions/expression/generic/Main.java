package expression.generic;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Not enough arguments");
            return;
        }

        String mode = args[0];
        String expr = args[1];
        GenericTabulator tabulator = new GenericTabulator();

        Object[][][] table = tabulator.tabulate(
                switch (mode) {
                    case "-i" -> "i";
                    case "-d" -> "d";
                    case "-bi" -> "bi";
                    case "-u" -> "u";
                    case "-s" -> "s";
                    case "-f" -> "f";
                    default -> throw new IllegalArgumentException("Unknown mode");
                }, expr, -2, 2, -2, 2, -2, 2);

        for (int x = 0; x < table.length; x++) {
            for (int y = 0; y < table[x].length; y++) {
                for (int z = 0; z < table[x][y].length; z++) {
                    System.out.println("x=" + (x - 2) + " y=" + (y - 2) + " z=" + (z - 2) + " => " + table[x][y][z]);
                }
            }
        }
    }
}
