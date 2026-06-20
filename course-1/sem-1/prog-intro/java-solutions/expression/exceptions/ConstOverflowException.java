package expression.exceptions;

public class ConstOverflowException extends ParsingException {
    public ConstOverflowException(String number, int pos) {
        super("Constant overflow in " + number, pos);
    }
}
