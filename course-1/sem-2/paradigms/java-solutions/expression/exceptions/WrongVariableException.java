package expression.exceptions;

public class WrongVariableException extends ParsingException {
    public WrongVariableException(String name, int pos) {
        super("Wrong variable " + name, pos);
    }
}
