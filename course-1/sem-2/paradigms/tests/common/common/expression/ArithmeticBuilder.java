package common.expression;

import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Basic arithmetics.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ArithmeticBuilder implements OperationsBuilder {
    private final BaseVariant variant;
    private final F neg;
    private final F add;
    private final F sub;
    private final F mul;
    private final F div;

    public ArithmeticBuilder(final boolean varargs, final List<String> variables) {
        variant = new BaseVariant(varargs);
        variables.forEach(this::variable);

        final Expr.Op negate = Expr.Op.op("negate");
        variant.unary("negate", negate, a -> -a);

        //noinspection Convert2MethodRef
        add = f(variant.infix("+", 100, (a, b) -> a + b), 2);
        sub = f(variant.infix("-", 100, (a, b) -> a - b), 2);
        mul = f(variant.infix("*", 200, (a, b) -> a * b), 2);
        div = f(variant.infix("/", 200, (a, b) -> a / b), 2);
        neg = f(negate, 1);
        
        basicTests();
    }

    public void basicTests() {
        final List<F> ops = List.of(neg, add, sub, mul, div);
        variant.tests(() -> Stream.of(
                Stream.of(variant.c()),
                variant.getVariables().stream(),
                ops.stream().map(F::c),
                ops.stream().map(F::v),
                ops.stream().map(F::r),
                ops.stream().map(F::r),
                Stream.of(
                    div.f(neg.r(), r()),
                    div.f(r(), mul.r()),
                    add.f(add.f(mul.r(), mul.r()), mul.r()),
                    sub.f(add.f(mul.r(), mul.f(r(), mul.f(r(), mul.r()))), mul.r())
                )
        ).flatMap(Function.identity()));
    }

    @Override
    public void constant(final String name, final String alias, final double value) {
        alias(name, alias);
        final ExprTester.Func expr = vars -> value;
        variant.nullary(name, expr);
        final Expr constant = Expr.nullary(name, expr);
        variant.tests(() -> Stream.of(
                neg.f(constant),
                add.f(constant, r()),
                sub.f(r(), constant),
                mul.f(r(), constant),
                div.f(constant, r())
        ));
    }

    @Override
    public void unary(final String id, final String alias, final DoubleUnaryOperator f) {
        final Expr.Op op = Expr.Op.op(id);
        variant.unary(id, op, f);
        variant.alias(id, alias);
        unaryTests(op);
    }

    @Override
    public void brackets(final String left, final String right, final String alias, final DoubleUnaryOperator f) {
        final String id = left + right;
        final Expr.Op op = new Expr.Op(id, "", left, right);
        variant.unary(id, op, f);
        variant.alias(id, alias);
        unaryTests(op);
    }

    private void unaryTests(final Expr.Op op) {
        final F f = f(op, 1);
        variant.tests(() -> Stream.of(
                f.c(),
                f.v(),
                f.f(sub.r()),
                f.f(add.r()),
                f.f(div.f(f.r(), add.r())),
                add.f(f.f(f.f(add.r())), mul.f(r(), mul.f(r(), f.r())))
        ));
    }

    @Override
    public void binary(final String name, final String alias, final DoubleBinaryOperator f) {
        final Expr.Op op = Expr.Op.op(name);
        variant.binary(name, op, f);
        variant.alias(name, alias);
        binaryTests(op);
    }

    private void binaryTests(final Expr.Op op) {
        final F f = f(op, 2);
        variant.tests(() -> Stream.of(
                f.c(),
                f.v(),
                f.r(),
                f.f(neg.r(), add.r()),
                f.f(sub.r(), neg.r()),
                f.f(neg.r(), f.r()),
                f.f(f.r(), neg.r())
        ));
    }
    
    private record F(Expr.Op op, int arity, BaseVariant variant) {
        public Expr f(final Expr... args) {
            assert arity < 0 || arity == args.length;
            return variant.f(op, args);
        }

        public Expr v() {
            return g(variant::v);
        }

        public Expr c() {
            return g(variant::c);
        }

        public Expr r() {
            return g(variant::r);
        }

        private Expr g(final Supplier<Expr> g) {
            return f(Stream.generate(g).limit(arity).toArray(Expr[]::new));
        }
    }
    
    private F f(final Expr.Op op, final int arity) {
        return new F(op, arity, variant);
    }

    private Expr r() {
        return variant.r();
    }

    private Expr f(final Expr.Op op, final Expr... args) {
        return variant.f(op, args);
    }

    @Override
    public void infix(final String name, final String alias, final int priority, final DoubleBinaryOperator f) {
        final Expr.Op op = variant.infix(name, priority, f);
        variant.alias(name, alias);
        binaryTests(op);
    }


    @Override
    public void fixed(
            final String name,
            final String alias,
            final int arity,
            final ExprTester.Func f
    ) {
        final Expr.Op op = Expr.Op.op(name);
        variant.fixed(name, op, arity, f);
        variant.alias(name, alias);

        if (arity == 1) {
            unaryTests(op);
        } else if (arity == 2) {
            binaryTests(op);
        } else if (arity == 3) {
            final F ff = f(op, 3);
            variant.tests(() -> {
                final Expr e1 = ff.c();
                final Expr e2 = ff.v();
                final Expr e3 = ff.f(add.r(), sub.r(), mul.r());
                return Stream.of(
                        ff.f(variant.c(), r(), r()),
                        ff.f(r(), variant.c(), r()),
                        ff.f(r(), r(), variant.c()),
                        ff.f(variant.v(), mul.v(), mul.v()),
                        ff.f(mul.v(), variant.v(), mul.v()),
                        ff.f(mul.v(), r(), mul.v()),
                        ff.r(),
                        e1,
                        e2,
                        e3,
                        ff.f(e1, e2, e3)
                );
            });
        } else if (arity == 4) {
            final F ff = f(op, 4);
            variant.tests(() -> {
                final Expr e1 = ff.c();
                final Expr e2 = ff.v();
                final Expr e3 = ff.r();
                final Expr e4 = ff.f(add.r(), sub.r(), mul.r(), div.r());
                return Stream.of(
                        ff.r(),
                        ff.r(),
                        ff.r(),
                        e1,
                        e2,
                        e3,
                        e4,
                        ff.f(e1, e2, e3, e4)
                );
            });
        } else {
            variant.tests(() -> Stream.concat(
                    Stream.of(
                            f(op, arity, variant::c),
                            f(op, arity, variant::v)
                    ),
                    IntStream.range(0, 10).mapToObj(i -> f(op, arity, variant::r))
            ));
        }
    }

    private Expr f(final Expr.Op op, final int arity, final Supplier<Expr> generator) {
        return f(op, Stream.generate(generator).limit(arity).toArray(Expr[]::new));
    }

    @Override
    public void any(
            final String name,
            final String alias,
            final AnyOp op
    ) {
        final Expr.Op eop = Expr.Op.op(name);
        variant.any(name, eop, op);
        variant.alias(name, alias);

        if (variant.hasVarargs()) {
            variant.tests(() -> Stream.<List<Expr>>of(
                    List.of(),
                    List.of(r()),
                    List.of(r(), r()),
                    List.of(r(), r(), r()),
                    List.of(r(), r(), r(), r()),
                    List.of(r(), r(), r(), r(), r()),
                    List.of(add.r(), r()),
                    List.of(r(), r(), sub.r())
            ).filter(op.arity()).map(args -> args.toArray(Expr[]::new)).map(f(eop, -1)::f));
        }

        variant.tests(() -> IntStream.rangeClosed(op.min(), op.max())
                        .mapToObj(i -> f(eop, variant.hasVarargs() ? i : op.fixed(), variant::r)));
    }

    @Override
    public void variable(final String name) {
        variant.variable(name, variant.getVariables().size());
    }

    @Override
    public void alias(final String name, final String alias) {
        variant.alias(name, alias);
    }

    @Override
    public void remove(final String... names) {
        variant.remove(names);
    }

    @Override
    public BaseVariant variant() {
        return variant;
    }
}
