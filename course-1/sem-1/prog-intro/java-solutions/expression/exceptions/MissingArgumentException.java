package expression.exceptions;

public class MissingArgumentException extends ParsingException {
    public MissingArgumentException(int pos) {
        super("No last argument", pos);
    }

    public MissingArgumentException(char operator, String argument, int pos) {
        super("Missing " + argument + " argument for " + operator, pos);
    }
}
