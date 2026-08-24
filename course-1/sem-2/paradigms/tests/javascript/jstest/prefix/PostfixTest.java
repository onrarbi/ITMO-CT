package jstest.prefix;

import base.Selector;
import common.expression.Dialect;
import common.expression.Operation;

import static common.expression.Operations.*;

/**
 * Tests for Postfix* variants of
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-expression-parsing">JavaScript Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class PostfixTest {
    private static final Operation PARENTHESES = parentheses("(", ")", "{", "}", "[", "]", "<", ">");

    public static final Selector SELECTOR = ParserTester.selector(
                    PostfixTest.class,
                    "postfix", "parsePostfix", new Dialect("%s", "%s", "({args} {op})", " "),
                    "Empty input", "",
                    "Unknown variable", "a",
                    "Invalid number", "-a",
                    "Missing )", "(z (x y +) *",
                    "Missing (", "z (x y +) *)",
                    "Unknown operation", "( x y @@)",
                    "Excessive info", "(x y +) x",
                    "Empty op", "()",
                    "Invalid unary (0 args)", "(negate)",
                    "Invalid unary (2 args)", "(x y negate)",
                    "Invalid binary (0 args)", "(+)",
                    "Invalid binary (1 args)", "(x +)",
                    "Invalid binary (3 args)", "(x y z +)",
                    "Variable op (0 args)", "(x)",
                    "Variable op (1 args)", "(1 x)",
                    "Variable op (2 args)", "(1 2 x)",
                    "Const op (0 args)", "(0)",
                    "Const op (1 args)", "(0 1)",
                    "Const op (2 args)", "(0 1 2)"
            )
            .variant("Base", ARITH)
            .variant("3637", PARENTHESES, any(3, SUM_EXP, LSE))
            .variant("3839", PARENTHESES, any(3, SUM_EXP, LME))
            .selector();

    private PostfixTest() {
    }

    public static void main(final String... args) {
        Selector.aggregate(ParserTest.SELECTOR, SELECTOR).main(args);
    }
}
