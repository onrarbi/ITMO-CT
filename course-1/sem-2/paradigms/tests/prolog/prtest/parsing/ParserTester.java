package prtest.parsing;

import base.TestCounter;
import common.expression.Dialect;
import common.expression.ExprTester;
import common.expression.LanguageBuilder;
import prtest.PrologEngine;
import prtest.Rule;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-expression-parsing">Prolog Expression Parser</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTester {
    private static final Function<String, String> PREFIXER = name -> Character.isLetter(name.charAt(0)) ? "op_" + name : name;

    private ParserTester() {
    }

    public static final Dialect PARSED = new Dialect("variable('%s')", "const(%s.0)", "operation({op}, {args})", ", ")
            .renamed((id, name) -> PREFIXER.apply(name).toLowerCase());

    /* package-private*/ static base.Selector.Composite<LanguageBuilder> builder(final Mode... modes) {
        return LanguageBuilder.selector(
                ParserTest.class,
                mode -> false,
                (builder, counter) -> {
                    final Mode mode = modes[counter.mode()];
                    return new ExprTester<>(
                            counter,
                            50 / TestCounter.DENOMINATOR,
                            new PrologEngine(
                                    Path.of("expression.pl"),
                                    Rule.func("evaluate", 2),
                                    Rule.pred(mode.parse(), 2)
                            ),
                            builder.language(PARSED, mode.unparsed),
                            true,
                            ExprTester.STANDARD_SPOILER,
                            builder.getCorruptor()
                    );
                },
                Arrays.stream(modes).filter(Objects::nonNull).map(Mode::name).toArray(String[]::new)
        );
    }

    public record Mode(String name, Dialect unparsed) {
        Mode(final String name, final String template, final String separator) {
                this(
                        name,
                        new Dialect(
                                "%s", "%s.0", (op, args) ->
                                Dialect.operation(template, separator).apply(op.rename(op.name().replace(",", "")), args)
                        )
                );
            }

            public String parse() {
                return name + "_str";
            }
        }
}
