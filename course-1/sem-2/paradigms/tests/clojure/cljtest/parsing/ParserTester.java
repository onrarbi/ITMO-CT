package cljtest.parsing;

import base.Selector;
import cljtest.object.ObjectTester;
import common.expression.Dialect;
import common.expression.Expr;
import common.expression.ExprTester;
import common.expression.LanguageBuilder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Tester for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-expression-parsing">Clojure Expression Parsing</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTester {
    private static <T> String enclosed(
            final Expr.Op op,
            final List<T> args,
            final Function<T, String> unaryArg,
            final BiFunction<String, List<T>, String> binary
    ) {
        return op.enclose(switch (args.size()) {
            case 1 -> {
                final String arg = unaryArg.apply(args.get(0));
                yield op.name().isEmpty()
                        ? arg
                        : op.name().startsWith(":")
                                ? "(%s %s)".formatted(arg, op.name().substring(1))
                                : "%s %s".formatted(op.name(), arg);
            }
            case 2 -> binary.apply(op.name(), args);
            default -> throw new AssertionError("Unsupported op " + op.name() + "/" + args.size());
        });
    }


    private ParserTester() {
    }

    /* package-private*/ static Selector.Composite<LanguageBuilder> builder() {
        return LanguageBuilder.selector(
                ParserTester.class,
                mode -> false,
                (builder, counter) -> {
                    final Mode mode = counter.mode() == 1 ? Mode.INFIX : Mode.POSTFIX;
                    return ObjectTester.tester(
                            counter,
                            builder.language(ObjectTester.PARSED, mode.unparsed),
                            false,
                            mode.parse,
                            mode.toString,
                            mode == Mode.INFIX ? spoiler(builder) : ExprTester.Generator.empty(),
                            builder.getCorruptor()
                    );
                },
                "easy", "hard"
        );
    }

    private static ExprTester.Generator<String> spoiler(final LanguageBuilder builder) {
        final Expr.Cata<IntFunction<String>> cata = new Expr.Cata<>(
                name -> priority -> name,
                value -> priority -> "%s.0".formatted(value),
                name -> priority -> name,
                (op, args) -> priority -> enclosed(
                        op,
                        args,
                        a -> a.apply(op.enclosing() ? 0 : Integer.MAX_VALUE),
                        (name, as) -> {
                            final int p = builder.variant().getPriority(name);
                            final int local = Math.abs(p);
                            final String arg1 = as.get(0).apply(local + (p > 0 ? 0 : 1));
                            final String arg2 = as.get(1).apply(local + (p > 0 ? 1 : 0));
                            return (local < priority ? "(%s %s %s)" : "%s %s %s").formatted(arg1, name, arg2);
                        }
                )
        );
        return (ignore, expr, random, build) -> {
            final String expression = expr.cata(cata).apply(0);
            build.add(random.nextBoolean() ? ExprTester.addSpaces(expression, random) : expression);
        };
    }

    private enum Mode {
        POSTFIX("parseObjectPostfix", "toStringPostfix", new Dialect(
                "%s",
                "%s.0",
                (op, args) -> "(" + String.join(" ", args) + " "
                        + (op.name().startsWith(":") ? op.name().substring(1) : op.name()) + ")"
        )),
        INFIX("parseObjectInfix", "toStringInfix", new Dialect(
                "%s",
                "%s.0",
                (op, args) -> enclosed(
                        op,
                        args,
                        Function.identity(),
                        (name, as) -> "(%s %s %s)".formatted(as.get(0), name, as.get(1))
                )
        ));

        private final String parse;
        private final String toString;
        private final Dialect unparsed;

        Mode(final String parse, final String toString, final Dialect unparsed) {
            this.unparsed = unparsed;
            this.toString = toString;
            this.parse = parse;
        }
    }

}
