package expression.exceptions;

import expression.*;
import java.util.List;

public class ExpressionParser implements ListParser {
    private static final char EOF = '\0';

    private String expression;
    private List<String> variables;
    private char ch;
    private int pos;

    @Override
    public ListExpression parse(String expression, List<String> variables) {
        this.expression = expression;
        this.variables = variables;
        this.pos = 0;

        updateChar();
        skipWhitespace();
        if (ch == EOF) {
            throw new EmptyExpressionException(pos);
        }

        ListExpression result = parseLastPriority();

        skipWhitespace();

        if (ch != EOF) {
            throw new UnexpectedCharException(ch, pos);
        }

        return result;
    }

    private void updateChar() {
        if (pos < expression.length()) {
            ch = expression.charAt(pos);
        } else {
            ch = EOF;
        }
    }

    private void nextChar() {
        pos++;
        updateChar();
    }

    private void skipWhitespace() {
        while (ch != EOF && Character.isWhitespace(ch)) {
            nextChar();
        }
    }

    protected boolean take(char c) {
        if (ch == c) {
            nextChar();
            skipWhitespace();

            return true;
        }

        return false;
    }

    protected void expect(char c) {
        if (!take(c)) {
            throw new ExpectedButGotException(c, ch, pos);
        }
    }

    private ListExpression parseFirstPriority() {
        skipWhitespace();

        if (take('-')) {
            if (Character.isDigit(ch)) {
                return parseConst(true);
            }

            return new CheckedNegate(parseFirstPriority());
        }

        if (Character.isDigit(ch)) {
            return parseConst(false);
        }

        if (parseWord("low")) {
            return new Low(parseFirstPriority());
        }

        if (parseWord("high")) {
            return new High(parseFirstPriority());
        }

        if (ch == '$') {
            return parseVariable();
        }

        if (take('(')) {
            ListExpression result = parseLastPriority();
            expect(')');
            return result;
        }

        throw new IllegalArgumentException("Unexpected character: " + ch);
    }

    private ListExpression parseMidPriority() {
        ListExpression result = parseFirstPriority();

        while (true) {
            if (take('*')) {
                result = new CheckedMultiply(result, parseFirstPriority());
            } else if (take('/')) {
                result = new CheckedDivide(result, parseFirstPriority());
            } else {
                return result;
            }
        }
    }

    private ListExpression parseLastPriority() {
        ListExpression result = parseMidPriority();

        while (true) {
            if (take('+')) {
                result = new CheckedAdd(result, parseMidPriority());
            } else if (take('-')) {
                result = new CheckedSubtract(result, parseMidPriority());
            } else {
                return result;
            }
        }
    }

    private Variable parseVariable() {
        StringBuilder name = new StringBuilder();

        name.append(ch);
        nextChar();

        while (ch != EOF && !Character.isWhitespace(ch)
                && ch != '+' && ch != '-' && ch != '*' && ch != '/'
                && ch != '(' && ch != ')') {
            name.append(ch);
            nextChar();
        }

        skipWhitespace();

        String variableName = name.toString();
        int id = variables.indexOf(variableName);

        if (id == -1) {
            throw new WrongVariableException(variableName, pos);
        }

        return new Variable(variableName, id);
    }

    private Const parseConst(boolean isNegative) {
        StringBuilder number = new StringBuilder();
        int start = isNegative ? pos - 1 : pos;

        if (isNegative) {
            number.append('-');
        }
        while (ch != EOF && Character.isDigit(ch)) {
            number.append(ch);
            nextChar();
        }

        skipWhitespace();

        try {
            return new Const(Integer.parseInt(number.toString()));
        } catch (NumberFormatException e) {
            throw new ConstOverflowException(number.toString(), start);
        }
    }

    private boolean parseWord(String word) {
        if (!expression.startsWith(word, pos)) {
            return false;
        }

        int after = pos + word.length();

        if (after < expression.length()
                && Character.isLetterOrDigit(expression.charAt(after))) {
            return false;
        }

        pos = after;
        updateChar();
        skipWhitespace();

        return true;
    }
}