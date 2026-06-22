package expression.generic;

import java.util.List;
import java.util.Objects;

public abstract class GenericBinaryOperation<T> implements GenericExpression<T> {
    protected final GenericExpression<T> first;
    protected final GenericExpression<T> second;
    protected final Operation<T> operation;

    public GenericBinaryOperation(GenericExpression<T> first, GenericExpression<T> second, Operation<T> operation) {
        this.first = first;
        this.second = second;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "(" + first + " " + getOperator() + " " + second + ")";
    }

    @Override
    public T evaluate(List<T> variables) {
        return action(first.evaluate(variables), second.evaluate(variables));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericBinaryOperation<?> op)) {
            return false;
        }
        return this.getClass() == op.getClass() && first.equals(op.first) && second.equals(op.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, getClass());
    }

    protected abstract T action(T first, T second);

    protected abstract String getOperator();

}
