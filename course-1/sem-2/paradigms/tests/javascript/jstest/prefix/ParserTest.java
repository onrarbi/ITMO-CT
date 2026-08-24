package jstest.prefix;

import base.Selector;
import common.expression.Operation;

import static common.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-expression-parsing">JavaScript Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final Operation PARENTHESES = parentheses("(", ")", "{", "}", "[", "]", "<", ">");

    public static final Selector SELECTOR = ParserTester.selector(
                    ParserTest.class,
                    "prefix", "parsePrefix", ParserTester.PREFIX
            )
            .variant("Base", ARITH)
            .variant("3637", PARENTHESES, any(3, SUM_EXP, LSE))
            .variant("3839", PARENTHESES, any(3, SUM_EXP, LME))
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
