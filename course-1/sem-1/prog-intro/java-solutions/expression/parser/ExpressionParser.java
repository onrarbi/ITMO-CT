package expression.parser;

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
            throw new IllegalArgumentException("Unexpected end of expression: " + ch);
        }

        return parseLastPriority();
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
            throw new IllegalArgumentException("Expected '" + c + "', but got '" + ch + "'");
        }
    }

    private ListExpression parseFirstPriority() {
        skipWhitespace();

        if (take('-')) {
            if (Character.isDigit(ch)) {
                return parseConst(true);
            }

            return new Negate(parseFirstPriority());
        }

        if (parseWord("floor")) {
            return new Floor(parseFirstPriority());
        }

        if (parseWord("ceiling")) {
            return new Ceiling(parseFirstPriority());
        }

        if (Character.isDigit(ch)) {
            return parseConst(false);
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
                result = new Multiply(result, parseFirstPriority());
            } else if (take('/')) {
                result = new Divide(result, parseFirstPriority());
            } else {
                return result;
            }
        }
    }

    private ListExpression parseLastPriority() {
        ListExpression result = parseMidPriority();

        while (true) {
            if (take('+')) {
                result = new Add(result, parseMidPriority());
            } else if (take('-')) {
                result = new Subtract(result, parseMidPriority());
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
            throw new IllegalArgumentException("Unknown variable: " + variableName);
        }

        return new Variable(variableName, id);
    }

    private Const parseConst(boolean isNegative) {
        StringBuilder number = new StringBuilder();

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
            throw new IllegalArgumentException("Invalid constant: " + number);
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