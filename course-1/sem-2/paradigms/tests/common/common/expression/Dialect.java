package common.expression;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * Expression dialect.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Dialect {
    private final Expr.Cata<String> cata;

    private Dialect(final Expr.Cata<String> cata) {
        this.cata = cata;
    }

    public Dialect(final String variable, final String constant, final BiFunction<Expr.Op, List<String>, String> nary) {
        this(new Expr.Cata<>(variable::formatted, constant::formatted, name -> name, nary));
    }

    public Dialect(final String variable, final String constant, final String operation, final String separator) {
        this(variable, constant, operation(operation, separator));
    }

    public static BiFunction<Expr.Op, List<String>, String> operation(final String template, final String separator) {
        return (op, args) -> template.replace("{op}", op.name()).replace("{args}", String.join(separator, args));
    }

    public Dialect renamed(final BiFunction<String, String, String> renamer) {
        return updated(cata -> cata.withOperation(nary -> (op, args) -> nary.apply(
                op.rename(renamer.apply(op.id(), op.name())), args)));
    }

    public Dialect updated(final UnaryOperator<Expr.Cata<String>> updater) {
        return new Dialect(updater.apply(cata));
    }

    public String render(final Expr expr) {
        return expr.cata(cata);
    }

    public String meta(final String name, final String... args) {
        return cata.operation(Expr.Op.op(name), List.of(args));
    }

    public Dialect functional() {
        return renamed(Dialect::toFunctional);
    }

    private static String toFunctional(final String id, final String name) {
        return name.chars().allMatch(Character::isUpperCase)
            ? name.toLowerCase()
            : Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
