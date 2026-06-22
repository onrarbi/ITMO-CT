package expression.generic;

import expression.exceptions.ExpressionException;

import java.util.List;

public class GenericTabulator implements Tabulator {

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ExpressionException {
        Operation<?> operation = switch (mode) {
            case "i" -> new IntegerOperation();
            case "d" -> new DoubleOperation();
            case "bi" -> new BigIntegerOperation();
            case "u" -> new UnsignedOperation();
            case "s" -> new ShortOperation();
            case "f" -> new FloatOperation();
            default -> throw new IllegalArgumentException("Unknown mode");
        };

        return generate(operation, expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] generate(Operation<T> op, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ExpressionException {
        int xSize = x2 - x1 + 1, ySize = y2 - y1 + 1, zSize = z2 - z1 + 1;
        Object[][][] table = new Object[xSize][ySize][zSize];
        GenericExpression<T> parsedExpression = new GenericExpressionParser<T>().parse(expression, List.of("x", "y", "z"), op);

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                for (int k = 0; k < zSize; k++) {
                    try {
                        table[i][j][k] = parsedExpression.evaluate(op.values(x1 + i, y1 + j, z1 + k));
                    } catch (ExpressionException e) {
                        table[i][j][k] = null;
                    }
                }
            }
        }

        return table;
    }
}