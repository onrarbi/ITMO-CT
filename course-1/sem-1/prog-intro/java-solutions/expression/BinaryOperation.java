package expression;

import java.util.List;
import java.util.Objects;

public abstract class BinaryOperation extends AbstractExpression {
    protected final ListExpression first;
    protected final ListExpression second;

    public BinaryOperation(ListExpression first, ListExpression second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + " " + getOperator() + " " + second + ")";
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return action(first.evaluate(variables), second.evaluate(variables));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BinaryOperation op)) {
            return false;
        }
        return this.getClass() == op.getClass() && first.equals(op.first) && second.equals(op.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, getClass());
    }

    protected abstract int action(int first, int second);

    protected abstract String getOperator();
}
