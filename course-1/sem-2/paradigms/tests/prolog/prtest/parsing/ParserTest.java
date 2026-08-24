package prtest.parsing;

import base.Selector;
import common.expression.Dialect;

import static common.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-expression-parsing">Prolog Expression Parser</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    public static final Selector SELECTOR = ParserTester.builder(
                    new ParserTester.Mode("postfix", "({args} {op})", " "),
                    new ParserTester.Mode("infix", new Dialect(
                            "%s",
                            "%s.0",
                            (op, args) -> switch (args.size()) {
                                case 1 -> op.name() + " " + args.get(0);
                                case 2 -> "(" + args.get(0) + " " + op.name() + " " + args.get(1) + ")";
                                case 3 -> {
                                    final String[] pts = op.name().split(",", 2);
                                    yield "(%s %s %s %s %s)".formatted(args.get(0), pts[0], args.get(1), pts[1], args.get(2));
                                }
                                default -> throw new AssertionError("Unsupported op " + op.name() + "/" + args.size());
                            }
                    )),
                    null
            )
            .variant("Base", ARITH)
            .variant("3637", EXP, LN, INFIX_POW, INFIX_LOG)
            .variant("3839", EXP, LN, INFIX_POW, INFIX_LOG, REBASE)
            .variant("3435")
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
