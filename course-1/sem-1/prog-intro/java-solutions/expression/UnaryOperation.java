package expression;

import java.util.List;
import java.util.Objects;

public abstract class UnaryOperation extends AbstractExpression {
    protected final ListExpression expression;

    public UnaryOperation(ListExpression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return getOperator() + '(' + expression + ')';
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return action(expression.evaluate(variables));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnaryOperation op)) {
            return false;
        }
        return this.getClass() == op.getClass() && expression.equals(op.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, getClass());
    }

    protected abstract int action(int expression);

    protected abstract String getOperator();
}
