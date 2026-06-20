package expression.exceptions;

public class EmptyModuleException extends ParsingException {
    public EmptyModuleException(int pos) {
        super("Empty expression in module", pos);
    }
}
