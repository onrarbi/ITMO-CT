package expression.exceptions;

public class EOFException extends ParsingException {
    public EOFException(int pos) {
        super("End of expression", pos);
    }
}
