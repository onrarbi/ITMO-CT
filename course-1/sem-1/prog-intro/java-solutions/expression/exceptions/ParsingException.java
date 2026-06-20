package expression.exceptions;

public abstract class ParsingException extends ExpressionException {
    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, int pos) {
        super(message + " at position " + (pos + 1));
    }
}