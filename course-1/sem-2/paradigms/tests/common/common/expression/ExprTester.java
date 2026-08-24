package common.expression;

import base.Asserts;
import base.ExtendedRandom;
import base.TestCounter;
import base.Tester;
import common.Engine;
import common.EngineException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Expressions tester.
 *
 * @author Niyaz Nigmatullin
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExprTester<E> extends Tester {
    public static final int N = 128;
    public static final double EPS = 1e-3;
    public static final int RANDOM_TESTS = 444;

    private final int randomTests;
    /*package*/ final Engine<E> engine;
    /*package*/ final Language language;
    private final List<Runnable> stages = new ArrayList<>();
    private final boolean testToString;

    private final Generator<String> spoiler;
    private final Generator<BadInput> corruptor;

    public static final Generator<String> STANDARD_SPOILER = (input, expr, random, builder) -> builder
            .add(input)
            .add(addSpaces(input, random));

    public ExprTester(
            final TestCounter counter,
            final int randomTests,
            final Engine<E> engine,
            final Language language,
            final boolean testToString,
            final Generator<String> spoiler,
            final Generator<BadInput> corruptor
    ) {
        super(counter);
        this.randomTests = randomTests;
        this.engine = engine;
        this.language = language;
        this.testToString = testToString;
        this.spoiler = spoiler;
        this.corruptor = corruptor;
    }

    private static final Predicate<String> UNSAFE = Pattern.compile("[-\\p{Alnum}+*/.=&|^<>◀▶◁▷≤≥?⁰-⁹₀-₉:]").asPredicate();

    private static boolean safe(final char ch) {
        return !UNSAFE.test("" + ch);
    }

    public static String addSpaces(final String expression, final ExtendedRandom random) {
        String spaced = expression;
        for (int n = StrictMath.min(10, 200 / expression.length()); n > 0;) {
            final int index = random.nextInt(spaced.length() + 1);
            final char c = index == 0 ? 0 : spaced.charAt(index - 1);
            final char nc = index == spaced.length() ? 0 : spaced.charAt(index);
            if ((safe(c) || safe(nc)) && c != '\'' && nc != '\'' && c != '"' && nc != '"') {
                spaced = spaced.substring(0, index) + " " + spaced.substring(index);
                n--;
            }
        }
        return spaced;
    }

    @Override
    public void test() {
        for (final Test test : language.getTests()) {
            try {
                test(test, prepared -> counter.scope(
                        "Testing: " + prepared,
                        () -> test.points().forEachOrdered(vars -> assertValue(
                                "original expression",
                                prepared,
                                vars,
                                test.evaluate(vars)
                        )))
                );
            } catch (final RuntimeException | AssertionError e) {
                throw new AssertionError("Error while testing " + test.parsed() + ": " + e.getMessage(), e);
            }
        }

        counter.scope("Random tests", () -> testRandom(randomTests));
        stages.forEach(Runnable::run);
    }

    public static int limit(final int variables) {
        return (int) Math.floor(Math.pow(N, 1.0 / variables));
    }

    private void test(final Test test, final Consumer<Engine.Result<E>> check) {
        final Consumer<Engine.Result<E>> fullCheck = parsed -> counter.test(() -> {
            check.accept(parsed);
            if (testToString) {
                counter.test(() -> engine.toString(parsed).assertEquals(test.toStr()));
            }
        });
        fullCheck.accept(engine.prepare(test.parsed()));
        spoiler.forEach(10, test, random(), input -> fullCheck.accept(parse(input)));
        corruptor.forEach(3, test, random(), input -> input.assertError(this::parse));
    }

    public Engine.Result<E> parse(final String expression) {
        return engine.parse(expression);
    }

    public void testRandom(final int n) {
        for (int i = 0; i < n; i++) {
            if (i % 100 == 0) {
                counter.format("Completed %3d out of %d%n", i, n);
            }
            final double[] vars = language.randomVars();

            final Test test = language.randomTest(i);
            final double answer = test.evaluate(vars);

            test(test, prepared -> assertValue("random expression", prepared, vars, answer));
        }
    }

    public void assertValue(final String context, final Engine.Result<E> prepared, final double[] vars, final double expected) {
        counter.test(() -> {
            final Engine.Result<Number> result = engine.evaluate(prepared, vars);
            Asserts.assertEquals("%n\tFor %s%s".formatted(context, result.context()), expected, result.value().doubleValue(), EPS);
        });
    }

    public static int mode(final String[] args, final Class<?> type, final String... modes) {
        if (args.length == 0) {
            System.err.println("ERROR: No arguments found");
        } else if (args.length > 1) {
            System.err.println("ERROR: Only one argument expected, " + args.length + " found");
        } else if (!Arrays.asList(modes).contains(args[0])) {
            System.err.println("ERROR: First argument should be one of: \"" + String.join("\", \"", modes) + "\", found: \"" + args[0] + "\"");
        } else {
            return Arrays.asList(modes).indexOf(args[0]);
        }
        System.err.println("Usage: java -ea " + type.getName() + " {" + String.join("|", modes) + "}");
        System.exit(1);
        throw new AssertionError("Return from System.exit");
    }

    public void addStage(final Runnable stage) {
        stages.add(stage);
    }

    public interface Func extends ToDoubleFunction<double[]> {
        @Override
        double applyAsDouble(double... args);
    }

    public record Test(Expr expr, String parsed, String unparsed, String toStr, List<Expr> variables) {
        public double evaluate(final double... vars) {
            return expr.evaluate(vars);
        }

        public Stream<double[]> points() {
            final int n = limit(variables.size());
            return IntStream.range(0, N).mapToObj(i -> IntStream.iterate(i, j -> j / n)
                    .map(j -> j % n)
                    .limit(variables.size())
                    .mapToDouble(j -> j)
                    .toArray());
        }
    }

    public record BadInput(String prefix, String comment, String suffix) {
        public String assertError(final Function<String, Engine.Result<?>> parse) {
            try {
                final Engine.Result<?> parsed = parse.apply(prefix + suffix);
                throw new AssertionError("Parsing error expected for '%s%s%s', got %s"
                        .formatted(prefix, comment, suffix, parsed.value()));
            } catch (final EngineException e) {
                return e.getCause().getMessage();
            }
        }
    }

    public interface Generator<T> {
        void generate(String input, Expr expr, ExtendedRandom random, Stream.Builder<? super T> builder);

        static <T> Generator<T> empty() {
            return (i, e, r, b) -> {};
        }

        default Generator<T> combine(final Generator<? extends T> that) {
            return (i, e, r, b) -> {
                this.generate(i, e, r, b);
                that.generate(i, e, r, b);
            };
        }

        default void forEach(final int limit, final Test test, final ExtendedRandom random, final Consumer<? super T> consumer) {
            final Stream.Builder<T> builder = Stream.builder();
            generate(test.unparsed(), test.expr(), random, builder);
            builder.build()
                    .sorted(Comparator.comparingInt(Object::hashCode))
                    .limit(limit)
                    .forEach(consumer);
        }
    }
}
