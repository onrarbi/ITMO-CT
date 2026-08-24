package common.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

/**
 * Expression instance.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 * @param coCata  There are no forall generics in Java, so using Object as placeholder.
 */
public record Expr(ExprTester.Func answer, Function<Cata<Object>, Object> coCata) {
    public <T> T cata(final Cata<T> cata) {
        @SuppressWarnings("unchecked") final Function<Cata<T>, T> coCata = (Function<Cata<T>, T>) (Function<?, ?>) this.coCata;
        return coCata.apply(cata);
    }

    public double evaluate(final double... vars) {
        return answer.applyAsDouble(vars);
    }

    static Expr constant(final int value) {
        return new Expr(vars -> value, cata -> cata.constant(value));
    }

    static Expr variable(final String name, final int index) {
        return new Expr(vars -> vars[index], cata -> cata.variable(name));
    }

    static Expr nullary(final String name, final ExprTester.Func answer) {
        return new Expr(answer, cata -> cata.nullary(name));
    }

    /**
     * Expression catamorphism.
     *
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public record Cata<T>(
            Function<String, T> variable,
            IntFunction<T> constant,
            Function<String, T> nullary,
            BiFunction<Op, List<T>, T> operation
    ) {
        public T variable(final String name) {
            return variable.apply(name);
        }

        public T constant(final int value) {
            return constant.apply(value);
        }

        public T nullary(final String name) {
            return nullary.apply(name);
        }

        public T operation(final Op op, final List<T> args) {
            return operation.apply(op, args);
        }

        public Cata<T> withOperation(final UnaryOperator<BiFunction<Op, List<T>, T>> updater) {
            return new Cata<>(variable, constant, nullary, updater.apply(operation));
        }
    }

    public static final class Op {
        private final String id;
        private final String name;
        private final String before;
        private final String after;
        private final Map<String, Op> renames = new HashMap<>();

        public Op(final String id, final String name, final String before, final String after) {
            this.id = id;
            this.name = name;
            this.before = before;
            this.after = after;
        }

        static Op op(final String name) {
            return new Op(name, name, "", "");
        }

        public Op rename(final String newName) {
            return renames.computeIfAbsent(newName, n -> new Op(id, n, before, after));
        }

        public String enclose(final String inner) {
            return before + inner + after;
        }

        public String id() {
            return id;
        }

        public String name() {
            return name;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            final var that = (Op) obj;
            return Objects.equals(this.name, that.name) &&
                    Objects.equals(this.before, that.before) &&
                    Objects.equals(this.after, that.after);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, before, after);
        }

        @Override
        public String toString() {
            return "Op[id=%s, name=%s, before=%s, after=%s]".formatted(id, name, before, after);
        }

        public boolean enclosing() {
            return !before.isEmpty() && !after.isEmpty();
        }
    }
}
