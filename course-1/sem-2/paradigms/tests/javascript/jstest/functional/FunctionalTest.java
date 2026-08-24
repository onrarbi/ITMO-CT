package jstest.functional;

import base.Selector;
import base.TestCounter;
import common.expression.Dialect;
import common.expression.ExprTester;
import common.expression.Language;
import common.expression.LanguageBuilder;
import jstest.JSExpressionEngine;

import java.nio.file.Path;
import java.util.List;

import static common.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-functional-expressions">JavaScript Functional Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class FunctionalTest {
    public static final Dialect ARITHMETIC = new Dialect("variable('%s')", "cnst(%s)", "{op}({args})", ", ")
            .functional();
    public static final Dialect POLISH = new Dialect("%s", "%s", "{args} {op}", " ");
    private static final Path SCRIPT = Path.of("functionalExpression.js");

    private FunctionalTest() {
    }

    /* package-private */ static Selector.Composite<LanguageBuilder> selector() {
        return LanguageBuilder.selector(
                FunctionalTest.class,
                mode -> false,
                List.of("x"),
                (builder, counter) -> tester(counter, builder.language(ARITHMETIC, POLISH)),
                "easy", "hard"
        );
    }

    public static final Selector SELECTOR = selector()
            .variant("Base", ARITH)
            .variant("3637", VARIABLES, ONE, TWO, THREE, CLAMP, WRAP, ARG_MIN.fix(3), ARG_MAX.fix(3), ARG_MIN.fix(5), ARG_MAX.fix(5))
            .variant("3839", VARIABLES, ONE, TWO, THREE, CLAMP, SOFT_CLAMP, ARG_MIN.fix(3), ARG_MAX.fix(3), ARG_MIN.fix(5), ARG_MAX.fix(5))
            .variant("3435", VARIABLES, ONE, TWO, THREE, ATAN, ATAN2)
            .variant("3233", VARIABLES, ONE, TWO, THREE, SIN, COS)
            .selector();

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    public static ExprTester<Object> tester(final TestCounter counter, final Language language) {
        return tester(counter, language, counter.mode() >= 1, SCRIPT);
    }

    /* package-private */ static ExprTester<Object> tester(
            final TestCounter counter,
            final Language language,
            final boolean testParsing,
            final Path script
    ) {
        final JSExpressionEngine engine = new JSExpressionEngine(script, "", "parse", "toString");
        return new ExprTester<>(
                counter,
                ExprTester.RANDOM_TESTS / TestCounter.DENOMINATOR,
                engine,
                language,
                false,
                testParsing ? ExprTester.STANDARD_SPOILER : ExprTester.Generator.empty(),
                ExprTester.Generator.empty()
        );
    }
}
