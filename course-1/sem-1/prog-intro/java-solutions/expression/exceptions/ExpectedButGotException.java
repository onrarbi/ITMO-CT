package expression.exceptions;

public class ExpectedButGotException extends ParsingException {
    public ExpectedButGotException(char expected, char got, int pos) {
        super("Expected '" + expected + "' but got " + getChar(got), pos);
    }

    private static String getChar(char ch) {
        if (ch == '\0') {
            return "end of expression";
        }

        return "'" + ch + "'";
    }
}