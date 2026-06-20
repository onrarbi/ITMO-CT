package expression.exceptions;

public class OverflowException extends ExpressionException {
    public OverflowException(String operator, int first, int second) {
        super("Overflow occurred in " + first + " " + operator + " " + second);
    }

    public OverflowException(String operator, int expression) {
        super("Overflow occurred in " + operator + " " + expression);
    }
}