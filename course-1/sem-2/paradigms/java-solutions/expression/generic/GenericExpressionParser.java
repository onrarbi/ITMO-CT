package expression.generic;

import java.util.List;
import java.math.BigInteger;

public class GenericExpressionParser<T> {
    private static final char EOF = 0;
    private String expression;
    private char ch;
    private int pos;
    private Operation<T> operation;

    public GenericExpression<T> parse(String expression, List<String> variables, Operation<T> operation) {
        this.expression = expression;
        this.pos = 0;
        this.operation = operation;

        ch = (pos < expression.length()) ? expression.charAt(pos) : EOF;
        skipWhitespace();

        return lastPriority();
    }

    private void skipWhitespace() {
        while(pos < expression.length() && Character.isWhitespace(expression.charAt(pos))) {
            pos++;
        }

        ch = (pos < expression.length()) ? expression.charAt(pos) : EOF;
    }

    private void takeChar() {
        if (pos >= expression.length()) {
            ch = EOF;

            return;
        }

        pos++;
        ch = (pos < expression.length()) ? expression.charAt(pos) : EOF;
    }

    protected boolean take(char c) {
        if (test(c)) {
            takeChar();
            skipWhitespace();

            return true;
        }

        return false;
    }

    protected boolean test(char c) {
        return ch == c;
    }

    protected void expect(char c) {
        if (!take(c)) {
            throw new IllegalArgumentException("Expected " + c + " but got " + ch);
        }
    }

    private GenericExpression<T> firstPriority() {
        skipWhitespace();

        if (test('-')) {
            takeChar();
            skipWhitespace();
            if (Character.isDigit(ch)) {
                return parseConst(true);
            }

            return new GenericNegate<>(firstPriority(), operation);
        }

        if (Character.isDigit(ch)) {
            return parseConst(false);
        }

        if (test('$') || Character.isLetter(ch)) {
            return parseVariable();
        }

        if (test('(')) {
            takeChar();
            GenericExpression<T> result = lastPriority();
            expect(')');

            return result;
        }

        throw new IllegalArgumentException("Unexpected character '" + ch);
    }

    private GenericExpression<T> midPriority() {
        GenericExpression<T> result = firstPriority();

        while (ch == '*' || ch == '/') {
            if (take('*')) {
                result = new GenericMultiply<>(result, firstPriority(), operation);
            } else if (take('/')) {
                result = new GenericDivide<>(result, firstPriority(), operation);
            }
        }

        return result;
    }

    private GenericExpression<T> lastPriority() {
        GenericExpression<T> result = midPriority();

        while (ch == '+' || ch == '-') {
            if (take('+')) {
                result = new GenericAdd<>(result, midPriority(), operation);
            } else if (take('-')) {
                result = new GenericSubtract<>(result, midPriority(), operation);
            }
        }

        return result;
    }

    private GenericVariable<T> parseVariable() {
        StringBuilder var = new StringBuilder();

        if (Character.isLetter(ch)) {
            while (Character.isLetterOrDigit(ch)) {
                var.append(ch);
                takeChar();
            }
        } else if (ch == '$') {
            takeChar();

            while(Character.isDigit(ch)) {
                var.append(ch);
                takeChar();
            }
        } else {
            throw new IllegalArgumentException("Unexpected variable: '" + ch + "'");
        }
        String name = var.toString();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Illegal variable");
        }
        int id;
        switch (name) {
            case "x" -> id = 0;
            case "y" -> id = 1;
            case "z" -> id = 2;
            default -> throw new IllegalArgumentException("Unknown variable: " + name);
        }

        skipWhitespace();

        return new GenericVariable<>(id);
    }

    private GenericConst<T> parseConst(boolean isNegative) {
        StringBuilder constant = new StringBuilder();

        if (isNegative) {
            constant.append('-');
        }

        while (ch != EOF && Character.isDigit(ch)) {
            constant.append(ch);
            takeChar();
        }

        skipWhitespace();
        int intValue = Integer.parseInt(constant.toString());

        return new GenericConst<>(operation.valueOf(intValue));
    }
}
