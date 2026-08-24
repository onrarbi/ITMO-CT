package jstest.object;

import base.TestCounter;
import common.expression.ExprTester;
import common.expression.Dialect;
import common.expression.Diff;
import common.expression.Language;
import jstest.JSExpressionEngine;

import java.nio.file.Path;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-object-expressions">JavaScript Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ObjectTester {
    public static final Dialect OBJECT = new Dialect("new Variable('%s')", "new Const(%s)", "new {op}({args})", ", ");

    private static final Diff DIFF = new Diff(2, new Dialect(
            "'%s'", "%s",
            (op, args) -> "%s.%s(%s)".formatted(args.get(0), op.name(), String.join(", ", args.subList(1, args.size())))
    ));

    private ObjectTester() {
    }

    public static ExprTester<Object> tester(
            final TestCounter counter,
            final Language language,
            final String toString,
            final String parse,
            final ExprTester.Generator<String> spoiler,
            final ExprTester.Generator<ExprTester.BadInput> corruptor
    ) {
        final ExprTester<Object> tester = new ExprTester<>(
                counter,
                ExprTester.RANDOM_TESTS / TestCounter.DENOMINATOR,
                new JSExpressionEngine(Path.of("objectExpression.js"), ".evaluate", parse, toString),
                language,
                true,
                ExprTester.STANDARD_SPOILER.combine(spoiler),
                corruptor
        );
        if (counter.mode() >= 2) {
            DIFF.diff(tester, true);
        }
        if (counter.mode() >= 3) {
            DIFF.simplify(tester);
        }
        return tester;
    }
}
