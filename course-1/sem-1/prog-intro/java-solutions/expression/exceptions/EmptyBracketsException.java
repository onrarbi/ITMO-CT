package expression.exceptions;

public class EmptyBracketsException extends ParsingException {
    public EmptyBracketsException(int pos) {
        super("Empty expression in brackets", pos);
    }
}
