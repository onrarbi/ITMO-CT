package expression.exceptions;

public abstract class ExpressionException extends RuntimeException {
    public ExpressionException(String message) {
        super(message);
    }
}