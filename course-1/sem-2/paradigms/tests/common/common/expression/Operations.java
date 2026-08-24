package common.expression;

import base.Functional;
import base.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;

/**
 * Known expression operations.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public enum Operations {
    ;

    public static final Operation ARITH = builder -> {
        builder.ops.alias("negate", "Negate");
        builder.ops.alias("+", "Add");
        builder.ops.alias("-", "Subtract");
        builder.ops.alias("*", "Multiply");
        builder.ops.alias("/", "Divide");
    };

    public static final Operation NARY_ARITH = builder -> {
        builder.ops.remove("negate", "+", "-", "*", "/");

        builder.ops.unary("negate", "Negate", a -> -a);

        builder.ops.any("+", "Add", arith(0, Double::sum, 0));
        builder.ops.any("-", "Subtract", arith(0, (a, b) -> a - b, 1));
        builder.ops.any("*", "Multiply", arith(1, (a, b) -> a * b, 0));
        builder.ops.any("/", "Divide", arith(1, (a, b) -> a / b, 1));
    };


    // === Common

    public static Operation constant(final String name, final double value) {
        return constant(name, name, value);
    }

    public static Operation constant(final String name, final String alias, final double value) {
        return builder -> builder.ops.constant(name, alias, value);
    }

    public static Operation unary(final String name, final String alias, final DoubleUnaryOperator op) {
        return builder -> builder.ops.unary(name, alias, op);
    }

    public static Operation brackets(final String left, final String right, final String alias, final DoubleUnaryOperator op) {
        return builder -> builder.ops.brackets(left, right, alias, op);
    }


    public static Operation binary(final String name, final String alias, final DoubleBinaryOperator op) {
        return builder -> builder.ops.binary(name, alias, op);
    }

    public static AnyOp arith(final double zero, final DoubleBinaryOperator f, final int minArity) {
        final ExprTester.Func func = args -> args.length == 0 ? zero
                : args.length == 1 ? f.applyAsDouble(zero, args[0])
                        : Arrays.stream(args).reduce(f).orElseThrow();
        return new AnyOp(func, minArity, minArity + 5, 2);
    }


    // === More common

    public record Op(String name, String alias, int minArity, int maxArity, ExprTester.Func f) {
        public Operation fix(final int arity) {
            assert minArity <= arity && arity <= maxArity;
            final String patched =
                    name.contains("₀") ? name.replace("₀", fix(arity, '₀')) :
                    name.contains("0") ? name.replace("0", fix(arity, '0')) :
                    name + fix(arity, name.charAt(0) > 0xff ? '₀' : '0');
            return fixed(patched, alias + arity, arity, f);
        }

        private static String fix(final int arity, final char c) {
            return "" + (char) (c + arity);
        }

        public Operation any(final int fixedArity) {
            return checker -> checker.ops.any(name, alias, new AnyOp(f, minArity, maxArity, fixedArity));
        }
    }

    public static Op op(final String name, final String alias, final int minArity, final ExprTester.Func f) {
        return new Op(name, alias, minArity, minArity + 5, f);
    }

    public static Op op1(final String alias, final int minArity, final ExprTester.Func f) {
        return new Op(Character.toLowerCase(alias.charAt(0)) + alias.substring(1), alias, minArity, minArity + 5, f);
    }

    public static Op opS(final String name, final String alias, final int minArity, final ToDoubleFunction<DoubleStream> f) {
        return op(name, alias, minArity, args -> f.applyAsDouble(Arrays.stream(args)));
    }

    public static Op opO(final String name, final String alias, final int minArity, final Function<DoubleStream, OptionalDouble> f) {
        return opS(name, alias, minArity, f.andThen(OptionalDouble::orElseThrow)::apply);
    }

    public static Operation fixed(final String name, final String alias, final int arity, final ExprTester.Func f) {
        return builder -> builder.ops.fixed(name, alias, arity, f);
    }

    @SuppressWarnings("SameParameterValue")
    public static Operation range(final int min, final int max, final Op... ops) {
        final List<Operation> operations = IntStream.rangeClosed(min, max)
                .mapToObj(i -> Arrays.stream(ops).map(op -> op.fix(i)))
                .flatMap(Function.identity())
                .toList();
        return builder -> operations.forEach(op -> op.accept(builder));
    }

    @SuppressWarnings("SameParameterValue")
    public static Operation any(final int fixed, final Op... ops) {
        final List<Operation> operations = Arrays.stream(ops).map(op -> op.any(fixed)).toList();
        return builder -> operations.forEach(op -> op.accept(builder));
    }

    public static Operation infix(
            final String name,
            final String alias,
            final int priority,
            final DoubleBinaryOperator op
    ) {
        return checker -> checker.ops.infix(name, alias, priority, op);
    }


    // === Variables
    public static final Operation VARIABLES = builder ->
            Stream.of("y", "z").forEach(builder.ops::variable);


    // === OneTwo
    public static final Operation ONE = constant("one", 1);
    public static final Operation TWO = constant("two", 2);
    public static final Operation THREE = constant("three", 3);


    // === Clamp, wrap
    public static final Operation CLAMP = fixed("clamp", "Clamp", 3, args ->
            args[1] <= args[2] ? Math.min(Math.max(args[0], args[1]), args[2]) : Double.NaN);
    public static final Operation WRAP = fixed("wrap", "Wrap", 3, args ->
            args[1] < args[2]
                    ? args[0] - Math.floor((args[0] - args[1]) / (args[2] - args[1])) * (args[2] - args[1])
                    : Double.NaN);


    // === ArcTan
    public static final Operation ATAN = unary("atan", "ArcTan", Math::atan);
    public static final Operation ATAN2 = binary("atan2", "ArcTan2", Math::atan2);


    // === ArgMin, ArgMax

    private static Op arg(final String name, final String alias, final Function<DoubleStream, OptionalDouble> f) {
        return opO(
                name, "Arg" + alias, 1, args -> {
                    final double[] values = args.toArray();
                    return f.apply(Arrays.stream(values)).stream()
                            .flatMap(value -> IntStream.range(0, values.length)
                                    .filter(i -> values[i] == value).asDoubleStream())
                            .findFirst();
                }
        );
    }

    public static final Op ARG_MIN = arg("argMin", "Min", DoubleStream::min);
    public static final Op ARG_MAX = arg("argMax", "Max", DoubleStream::max);


    // === SoftClamp
    public static final Operation SOFT_CLAMP = fixed("softClamp", "SoftClamp", 4, args ->
            args[1] <= args[2] && args[3] > 0
                    ? args[1] + (args[2] - args[1]) / (1 + Math.exp(args[3] * ((args[2] + args[1]) / 2 - args[0])))
                    : Double.NaN);


    // === SinCos
    public static final Operation SIN = unary("sin", "Sin", Math::sin);
    public static final Operation COS = unary("cos", "Cos", Math::cos);


    // === Pow, Log
    public static final Operation POW = binary("pow", "Power", Math::pow);
    public static final Operation LOG = binary("log", "Log", (a, b) -> Math.log(Math.abs(b)) / Math.log(Math.abs(a)));


    // === Sum
    public static final Op SUM = opS("sum", "Sum", 0, DoubleStream::sum);


    // === Gauss
    public static double gauss(final double a, final double b, final double c, final double x) {
        final double q = (x - b) / c;
        return a * Math.exp(-q * q / 2);
    }

    public static final Operation GAUSS = fixed("gauss", "Gauss", 4, args -> gauss(args[0], args[1], args[2], args[3]));


    // === Avg
    public static final Op AVG = opO("avg", "Avg", 1, DoubleStream::average);


    // === SoftWrap
    public static final Operation SOFT_WRAP = fixed("softWrap", "SoftWrap", 4, args -> {
        if (args[1] >= args[2] || args[3] < 0) {
            return Double.NaN;
        }
        final double x = args[0];
        final double min = args[1];
        final double max = args[2];
        final double l = args[3];
        final double a = Math.PI * (x - min) / (max - min);
        return Math.asin(Math.cos(a) * Math.tanh(l * -Math.sin(a))) * (max - min) / Math.PI + (max + min) / 2;
    });


    // === SumExp
    public static double sumexp(final double[] args) {
        return Arrays.stream(args).map(Math::exp).sum();
    }

    public static final Op SUM_EXP = op1("SumExp", 0, Operations::sumexp);
    public static final Op LSE = op1("Lse", 1, args -> Math.log(sumexp(args)));


    // === LME
    public static final Op LME = op1("Lme", 1, args -> Math.log(sumexp(args) / args.length));


    // === MeanExp
    public static double meanexp(final double[] args) {
        return Arrays.stream(args).map(Math::exp).sum() / args.length;
    }

    public static final Op MEAN_EXP = op1("MeanExp", 0, Operations::meanexp);


    // === ArcTan12
    public static final Op ATAN_12 = new Op(
            "atan12",
            "ArcTan12",
            1,
            2,
            args -> args.length == 1 ? Math.atan(args[0]) : Math.atan2(args[0], args[1])
    );

    // === SoftMax

    public static final Op SOFT_MAX = op1("SoftMax", 1, args -> Math.exp(args[0]) / sumexp(args));


    // === Means

    public static Op mean(final String alias, final ToDoubleBiFunction<DoubleStream, Double> f) {
        return op1(alias, 1, args -> f.applyAsDouble(Arrays.stream(args), (double) args.length));
    }

    public static double product(final DoubleStream args) {
        return args.reduce(1, (a, b) -> a * b);
    }

    public static final Op ARITH_MEAN = mean("ArithMean", (args, n) -> args.sum() / n);
    public static final Op GEOM_MEAN = mean("GeomMean", (args, n) -> Math.pow(Math.abs(product(args)), 1 / n));
    public static final Op HARM_MEAN = mean("HarmMean", (args, n) -> n / args.map(a -> 1 / a).sum());

    // === ArcSinCos

    public static final Operation ASIN = unary("asin", "ArcSin", x -> Math.asin((x + 1) % 2 - 1));
    public static final Operation ACOS = unary("acos", "ArcCos", x -> Math.acos((x + 1) % 2 - 1));


    // === Parentheses
    public static Operation parentheses(final String... vars) {
        final List<Pair<String, String>> variants = Functional.toPairs(vars);
        return language -> {
            if (variants.size() > 1) {
                language.addCorruptor((input, expr, random, builder) -> {
                    for (final Pair<String, String> from : variants) {
                        final int index = input.lastIndexOf(from.first());
                        if (index >= 0) {
                            Pair<String, String> to = from;
                            while (Objects.equals(to.second(), from.second())) {
                                to = random.randomItem(variants);
                            }
                            final String head = input.substring(0, index);
                            final String tail = input.substring(index + from.first().length());
                            builder.add(new ExprTester.BadInput(head, "<UNMATCHED PARENTHESIS->", to.first() + tail));
                            builder.add(new ExprTester.BadInput(head, "<REMOVED PARENTHESES>", tail));
                        }
                        final int insIndex = random.nextInt(input.length());
                        builder.add(new ExprTester.BadInput(
                                input.substring(0, insIndex),
                                "<INSERTED PARENTHESIS-->",
                                (random.nextBoolean() ? from.first() : from.second()) + input.substring(insIndex)
                        ));
                    }
                });
            }
            language.toStr(cata -> cata.withOperation(operation -> (op, args) -> {
                final String inner = operation.apply(op, args);
                final int openIndex = inner.indexOf("(");
                final int closeIndex = inner.lastIndexOf(")");
                if (openIndex < 0 || closeIndex < 0) {
                    return inner;
                }
                final String prefix = inner.substring(0, openIndex);
                final String middle = inner.substring(openIndex + 1, closeIndex);
                final String suffix = inner.substring(closeIndex + 1);
                if (variants.stream().anyMatch(v -> prefix.contains(v.first()) || suffix.contains(v.second()))) {
                    return inner;
                }
                final Pair<String, String> variant = variants.get(Math.abs(inner.hashCode() % variants.size()));
                return prefix + variant.first() + middle + variant.second() + suffix;
            }));
        };
    }


    // === Floor, Ceiling
    public static final Operation FLOOR = brackets("⌊", "⌋", "Floor", Math::floor);
    public static final Operation CEILING = brackets("⌈", "⌉", "Ceiling", Math::ceil);


    // === Bit Implication and Iff
    public static final Operation BIT_IMPL = infix("=>",   "BitImpl", -60, bitwise((a, b) -> ~a | b));
    public static final Operation BIT_IFF = infix("<=>",  "BitIff",  50,  bitwise((a, b) -> ~(a ^ b)));

    public static DoubleBinaryOperator bitwise(final LongBinaryOperator op) {
        return (a, b) -> longBitsToDouble(op.applyAsLong(doubleToLongBits(a), doubleToLongBits(b)));
    }

    // === Bit operations
    public static final Operation BIT_NOT     = unary("~", "BitNot", a -> longBitsToDouble(~doubleToLongBits(a)));
    public static final Operation BIT_AND     = infix("&", "BitAnd",  90,  bitwise((a, b) -> a & b));
    public static final Operation BIT_OR      = infix("|", "BitOr",   80,  bitwise((a, b) -> a | b));
    public static final Operation BIT_XOR     = infix("^", "BitXor",  70,  bitwise((a, b) -> a ^ b));


    // === Infix PowLog
    public static final Operation INFIX_POW = infix("**", "IPow", -300, Math::pow);
    public static final Operation INFIX_LOG = infix("//", "ILog", -300, (a, b) -> Math.log(Math.abs(b)) / Math.log(Math.abs(a)));


    // === Infix Rebase
    public static final Operation REBASE = fixed("_,^", "Rebase", 3, args -> Math.log(Math.abs(Math.pow(args[1], args[2]))) / Math.log(Math.abs(args[0])));


    // === Exp, Ln
    public static final Operation EXP = unary("exp", "Exp", Math::exp);
    public static final Operation LN = unary("ln", "Ln", Math::log);

    // === Square, Cube
    public static final Operation SQUARE = unary("square", "Square", a -> a * a);
    public static final Operation CUBE = unary("cube", "Cube", a -> a * a * a);
}
