package cljtest.functional;

import base.Selector;
import base.TestCounter;
import cljtest.ClojureEngine;
import common.expression.ExprTester;
import common.expression.Dialect;
import common.expression.Language;
import common.expression.LanguageBuilder;

import java.util.Optional;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-functional-expressions">Clojure Functional Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class FunctionalTester {
    public static final Dialect PARSED = new Dialect("(variable \"%s\")", "(constant %s.0)", "({op} {args})", " ")
            .functional();
    public static final Dialect UNPARSED = new Dialect("%s", "%s.0", "({op} {args})", " ");

    private FunctionalTester() {
    }

    /* package-private*/ static Selector.Composite<LanguageBuilder> builder() {
        return LanguageBuilder.selector(
                FunctionalTester.class,
                mode -> mode >= 1,
                (builder, counter) -> tester(
                        counter,
                        builder.language(PARSED, UNPARSED),
                        Optional.empty(),
                        "parseFunction", "",
                        ExprTester.Generator.empty(),
                        ExprTester.Generator.empty()
                ),
                "easy", "hard"
        );
    }

    public static ExprTester<Object> tester(
            final TestCounter counter,
            final Language language,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<String> evaluate,
            final String parse,
            final String toString,
            final ExprTester.Generator<String> spoiler,
            final ExprTester.Generator<ExprTester.BadInput> corruptor
    ) {
        return new ExprTester<>(
                counter,
                ExprTester.RANDOM_TESTS / TestCounter.DENOMINATOR,
                new ClojureEngine("expression.clj", evaluate, parse, toString),
                language,
                !toString.isEmpty(),
                ExprTester.STANDARD_SPOILER.combine(spoiler),
                corruptor
        );
    }
}
