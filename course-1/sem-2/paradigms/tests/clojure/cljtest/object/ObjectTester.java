package cljtest.object;

import base.Selector;
import base.TestCounter;
import cljtest.functional.FunctionalTester;
import common.expression.*;

import java.util.Optional;

import static cljtest.functional.FunctionalTester.UNPARSED;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-object-expressions">Clojure Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ObjectTester {
    public static final Dialect PARSED = new Dialect(
            "(Variable \"%s\")",
            "(Constant %s.0)",
                    (op, args) -> {
                        final String argS = String.join(" ", args);
                        return op.name().startsWith(":")
                                ? "(" + argS + " " + op.name().substring(1) + ")"
                                : "(" + op.name() + " " + argS + ")";
                    }
            );
    private static final Diff DIFF = new Diff(1, new Dialect("\"%s\"", "%s", "({op} {args})", " "));

    private ObjectTester() {
    }

    /* package-private*/ static Selector.Composite<LanguageBuilder> builder() {
        return LanguageBuilder.selector(
                ObjectTester.class,
                mode -> mode >= 1,
                (builder, counter) -> {
                    final Language language = builder.language(PARSED, UNPARSED);
                    return tester(
                            counter,
                            language,
                            counter.mode() >= 1,
                            "parseObject", "toString",
                            ExprTester.Generator.empty(),
                            builder.getCorruptor()
                    );
                },
                "easy", "hard"
        );
    }

    public static ExprTester<Object> tester(
            final TestCounter counter,
            final Language language,
            final boolean testDiff,
            final String parse,
            final String toString,
            final ExprTester.Generator<String> spoiler,
            final ExprTester.Generator<ExprTester.BadInput> corruptor
    ) {
        final ExprTester<Object> tester = FunctionalTester.tester(
                counter,
                language,
                Optional.of("evaluate"),
                parse,
                toString,
                spoiler,
                corruptor
        );
        if (testDiff) {
            DIFF.diff(tester, true);
        }
        return tester;
    }
}
