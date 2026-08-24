package common.expression;

import base.ExtendedRandom;
import common.expression.ExprTester.Func;

import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Base expressions variant.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseVariant implements Variant {
    private static final int MAX_C = 1_000;
    private static final Expr ZERO = c(0);

    private final ExtendedRandom random = new ExtendedRandom(getClass());
    private final boolean varargs;

    private final StringMap<Operator> operators = new StringMap<>();
    private final StringMap<Expr> nullary = new StringMap<>();
    private final StringMap<Expr> variables = new StringMap<>();
    private final Map<String, String> aliases = new HashMap<>();

    private final Map<String, Integer> priorities = new HashMap<>();

    public final List<Supplier<Stream<Expr>>> tests = new ArrayList<>();

    public BaseVariant(final boolean varargs) {
        this.varargs = varargs;
    }

    public List<Expr> getTests() {
        return tests.stream().flatMap(Supplier::get).toList();
    }

    public Expr randomTest(final int size) {
        return generate(size / 10 + 2);
    }

    private Expr generate(final int depth) {
        return depth > 0 ? generateOp(depth) : r();
    }

    public Expr r() {
        if (random.nextBoolean()) {
            return variables.random(random);
        } else if (nullary.isEmpty() || random.nextBoolean()){
            return c();
        } else {
            return nullary.random(random);
        }
    }

    public Expr c() {
        return random.nextBoolean() ? ZERO : c(random.nextInt(-MAX_C, MAX_C));
    }

    public Expr v() {
        return random().randomItem(variables.values().toArray(Expr[]::new));
    }

    protected Expr generateOp(final int depth) {
        if (random.nextInt(6) == 0 || operators.isEmpty()) {
            return generateP(depth);
        } else {
            final Operator operator = operators.random(random);
            final Expr[] args = Stream.generate(() -> generateP(depth))
                    .limit(random.nextInt(operator.minArity, operator.maxArity))
                    .toArray(Expr[]::new);
            return f(operator.op, args);
        }
    }

    protected Expr generateP(final int depth) {
        return generate(random.nextInt(depth));
    }

    public void tests(final Supplier<Stream<Expr>> tests) {
        this.tests.add(tests);
    }

    public void fixed(final String name, final Expr.Op op, final int arity, final Func f) {
        op(name, op, arity, arity, f);
    }

    public void op(final String id, final Expr.Op op, final int minArity, final int maxArity, final Func f) {
        assert !operators.contains(id) : "Duplicate op %s".formatted(id);
        operators.put(id, new Operator(op, minArity, maxArity, f));
    }

    public void remove(final String... names) {
        //noinspection SlowAbstractSetRemoveAll
        operators.values.keySet().removeAll(Arrays.asList(names));
    }

    public void any(final String id, final Expr.Op eop, final AnyOp op) {
        if (varargs) {
            op(id, eop, op.min(), op.max(), op.f());
        } else {
            op(id, eop, op.fixed(), op.fixed(), op.f());
        }
    }

    public void unary(final String id, final Expr.Op op, final DoubleUnaryOperator answer) {
        fixed(id, op, 1, args -> answer.applyAsDouble(args[0]));
    }

    public void binary(final String name, final Expr.Op op, final DoubleBinaryOperator answer) {
        fixed(name, op, 2, args -> answer.applyAsDouble(args[0], args[1]));
    }

    public Expr.Op infix(final String name, final int priority, final DoubleBinaryOperator answer) {
        final Expr.Op op = Expr.Op.op(name);
        binary(name, op, answer);
        priorities.put(name, priority);
        return op;
    }

    public void nullary(final String name, final Func f) {
        nullary.put(name, Expr.nullary(name, f));
    }

    public Expr f(final Expr.Op op, final Expr... args) {
        final Func operator = operators.get(op.id());
        Objects.requireNonNull(operator, "Unknown operation " + op.id());
        return new Expr(
                vars -> operator.applyAsDouble(Stream.of(args).mapToDouble(arg -> arg.evaluate(vars)).toArray()),
                cata -> cata.operation(op, Stream.of(args).map(arg -> arg.cata(cata)).toList())
        );
    }

    protected Expr n(final String name) {
        return nullary.get(name);
    }

    public static Expr c(final int value) {
        return Expr.constant(value);
    }

    public Expr variable(final String name, final int index) {
        final Expr variable = Expr.variable(name, index);
        variables.put(name, variable);
        return variable;
    }

    public List<Expr> getVariables() {
        return List.copyOf(variables.values());
    }

    @Override
    public ExtendedRandom random() {
        return random;
    }

    @Override
    public boolean hasVarargs() {
        return varargs;
    }

    @Override
    public Integer getPriority(final String op) {
        return priorities.get(op);
    }

    private record Operator(Expr.Op op, int minArity, int maxArity, Func f) implements Func {
        private Operator {
            assert 0 <= minArity && minArity <= maxArity;
        }

        @Override
        public double applyAsDouble(final double[] args) {
            return Arrays.stream(args).allMatch(Double::isFinite) ? f.applyAsDouble(args) : Double.NaN;
        }
    }

    private static class StringMap<T> {
        private final List<String> names = new ArrayList<>();
        private final Map<String, T> values = new HashMap<>();

        public T get(final String name) {
            return values.get(name);
        }

        public T random(final ExtendedRandom random) {
            return get(random.randomItem(names));
        }

        private boolean isEmpty() {
            return values.isEmpty();
        }

        private void put(final String name, final T value) {
            names.add(name);
            values.put(name, value);
        }

        private Collection<T> values() {
            return values.values();
        }

        public boolean contains(final String name) {
            return values.containsKey(name);
        }
    }

    public void alias(final String name, final String alias) {
        aliases.put(name, alias);
    }

    public String resolve(final String id, final String name) {
        return aliases.getOrDefault(id, name);
    }
}
