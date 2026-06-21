package expression.exceptions;

public class UnexpectedCharException extends ParsingException {
    public UnexpectedCharException(char ch, int pos) {
        super("Unexpected character '" + ch + "'", pos);
    }
}
