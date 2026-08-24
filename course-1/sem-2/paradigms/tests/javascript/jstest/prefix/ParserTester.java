package jstest.prefix;

import base.Functional;
import base.Selector;
import common.expression.Dialect;
import common.expression.ExprTester;
import common.expression.Language;
import common.expression.LanguageBuilder;
import jstest.object.ObjectTester;

import java.util.stream.IntStream;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#js-expression-parsing">JavaScript Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTester {
    public static final Dialect PREFIX = new Dialect("%s", "%s", "({op} {args})", " ");

    private ParserTester() {
    }

    public static Selector.Composite<LanguageBuilder> selector(
            final Class<?> owner,
            final String toString,
            final String parse,
            final Dialect unparsed,
            final String... parsingTests
    ) {
        assert parsingTests.length % 2 == 0;

        return LanguageBuilder.selector(owner, mode -> true, (builder, counter) -> {
            final String insertions = builder.variant().hasVarargs() ? "abc()+*/@ABC" : "xyz()+*/@ABC";
            final Language language = builder.language(ObjectTester.OBJECT, unparsed);
            final ExprTester<Object> tester = ObjectTester.tester(
                    counter,
                    language,
                    toString,
                    parse,
                    (input, expr, random, build) -> build.add(removeSpaces(input)),
                    corrupt(insertions)
            );
            tester.addStage(() -> Functional.forEachPair(
                    parsingTests,
                    (input, context) -> printParsingError(tester, input, context)
            ));
            return tester;
        }, "", "easy", "hard");
    }

    private static String removeSpaces(final String expression) {
        return expression.replace(" (", "(").replace(") ", ")");
    }

    private static void printParsingError(final ExprTester<?> test, final String description, final String input) {
        final String message = new ExprTester.BadInput(input, "", "").assertError(test::parse);
        final int index = message.lastIndexOf("in <eval>");

        System.err.format("%-15s | %-25s: %s%n", input, description, message.substring(0, index > 0 ? index : message.length()));
    }

    private static ExprTester.Generator<ExprTester.BadInput> corrupt(final String insertions) {
        return (input, expr, random, builder) -> IntStream.range(0, 1 + Math.min(10, 200 / input.length())).boxed()
                .forEach(i -> {
                    final int index = random.nextInt(input.length());
                    final char c = input.charAt(index);
                    if (!Character.isDigit(c) && !Character.isWhitespace(c) && "-hxyz".indexOf(c) == -1) {
                        builder.add(new ExprTester.BadInput(
                                input.substring(0, index),
                                "<SYMBOL REMOVED>",
                                input.substring(index + 1)
                        ));
                    }
                    final char newC = insertions.charAt(random.nextInt(insertions.length()));
                    if (!Character.isDigit(c) && c != '-') {
                        builder.add(new ExprTester.BadInput(
                                input.substring(0, index),
                                "<SYMBOL INSERTED -->",
                                newC + input.substring(index)
                        ));
                    }
                });
    }
}
