package prtest;

import alice.tuprolog.Term;
import base.Asserts;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Prolog rule.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Rule {
    private final String name;
    private final Object[] args;
    private final int[] indices;

    private Rule(final String name, final int arity) {
        this(name, new Object[arity], IntStream.range(0, arity));
    }

    private Rule(final String name, final Object[] args, final IntStream indices) {
        this.name = name;
        this.args = args;
        this.indices = indices.toArray();
    }

    public String getName() {
        return name;
    }

    public static Rule pred(final String name, final int arity) {
        return new Rule(name, arity);
    }

    public static Rule func(final String name, final int arity) {
        return pred(name, arity + 1).func();
    }

    public Rule bind(final int index, final Object value) {
        assert index < indices.length;
        args[indices[index]] = value;
        return new Rule(name, args.clone(), Arrays.stream(indices).filter(i -> i != indices[index]));
    }

    public Rule func() {
        return func(indices.length - 1);
    }

    public Rule func(final int index) {
        return bind(index, PrologScript.V);
    }

    public Term apply(final Object... args) {
        Asserts.assertEquals("arity", indices.length, args.length);

        for (int i = 0; i < indices.length; i++) {
            this.args[indices[i]] = args[i];
        }

        return Value.struct(name, this.args).toTerm();
    }

    public String toString() {
        return getName() + "/" + args.length;
    }
}
