package cljtest.parsing;

import base.ExtendedRandom;
import base.Selector;
import common.expression.BaseVariant;
import common.expression.Operation;

import java.util.function.BiConsumer;

import static common.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-expression-parsing">Clojure Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {

    private static final Operation VARIABLES = builder -> {
        final BaseVariant variant = builder.ops.variant();
        final ExtendedRandom random = variant.random();
        final BiConsumer<Character, Integer> var = (name, index) -> variant.variable(
                (random.nextBoolean() ? name : Character.toUpperCase(name)) + random.randomString("xyzXYZ"),
                index
        );
        for (int i = 0; i < 10; i++) {
            var.accept('x', 0);
            var.accept('y', 1);
            var.accept('z', 2);
        }
    };

    private static final Selector SELECTOR = ParserTester.builder()
            .variant("Base",    NARY_ARITH)
            .variant("3637",    VARIABLES, FLOOR, CEILING)
            .variant("3839",    VARIABLES, FLOOR, CEILING, BIT_IMPL, BIT_IFF)
            .variant("3233",    VARIABLES, SQUARE, CUBE)
            .variant("3435",    VARIABLES,  BIT_NOT, BIT_AND, BIT_OR, BIT_XOR)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
