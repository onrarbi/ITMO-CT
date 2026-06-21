package expression.exceptions;

public class DivisionByZeroException extends ExpressionException {
    public DivisionByZeroException(int first, int second) {
        super("Division by zero: " + first + " / " + second);
    }
}