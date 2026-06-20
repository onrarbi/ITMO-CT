package expression.exceptions;

public class EmptyExpressionException extends ParsingException {
    public EmptyExpressionException(int pos) {
        super("Expression is empty", pos);
    }
}
