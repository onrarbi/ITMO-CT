package common.expression;

import base.Selector;
import base.TestCounter;
import base.Tester;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntPredicate;

/**
 * Expression test builder.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class LanguageBuilder {
    public final OperationsBuilder ops;
    private List<int[]> simplifications;
    private Function<Expr.Cata<String>, Expr.Cata<String>> toStr = Function.identity();
    private ExprTester.Generator<ExprTester.BadInput> corruptor = ExprTester.Generator.empty();

    public LanguageBuilder(final boolean testMulti, final List<String> variables) {
        ops = new ArithmeticBuilder(testMulti, variables);
    }

    public static Selector.Composite<LanguageBuilder> selector(
            final Class<?> owner,
            final IntPredicate testMulti,
            final List<String> variables,
            final BiFunction<LanguageBuilder, TestCounter, Tester> tester,
            final String... modes
    ) {
        return Selector.composite(
                owner,
                counter -> new LanguageBuilder(testMulti.test(counter.mode()), variables),
                (builder, counter) -> tester.apply(builder, counter).test(),
                modes
        );
    }

    public static Selector.Composite<LanguageBuilder> selector(
            final Class<?> owner,
            final IntPredicate testMulti,
            final BiFunction<LanguageBuilder, TestCounter, Tester> tester,
            final String... modes
    ) {
        return selector(owner, testMulti, List.of("x", "y", "z"), tester, modes);
    }

    public Variant variant() {
        return ops.variant();
    }

    public Language language(final Dialect parsed, final Dialect unparsed) {
        final BaseVariant variant = ops.variant();
        return new Language(parsed.renamed(variant::resolve), unparsed.updated(toStr::apply), unparsed, variant, simplifications);
    }

    public void toStr(final Function<Expr.Cata<String>, Expr.Cata<String>> updater) {
        toStr = updater.compose(toStr);
    }

    public Function<Expr.Cata<String>, Expr.Cata<String>> getToStr() {
        return toStr;
    }

    public void setSimplifications(final List<int[]> simplifications) {
        this.simplifications = simplifications;
    }

    public void addCorruptor(final ExprTester.Generator<ExprTester.BadInput> corruptor) {
        this.corruptor = this.corruptor.combine(corruptor);
    }

    public ExprTester.Generator<ExprTester.BadInput> getCorruptor() {
        return corruptor;
    }
}
