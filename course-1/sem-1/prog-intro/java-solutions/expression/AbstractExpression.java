package expression;

import java.util.List;

public abstract class AbstractExpression implements Expression, TripleExpression, ListExpression {
    @Override
    public int evaluate(int x) {
        return evaluate(List.of(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return evaluate(List.of(x, y, z));
    }
}