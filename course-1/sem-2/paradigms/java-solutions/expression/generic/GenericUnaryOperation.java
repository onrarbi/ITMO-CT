package expression.generic;

import java.util.List;
import java.util.Objects;

public abstract class GenericUnaryOperation<T> implements GenericExpression<T> {
    protected final GenericExpression<T> expression;
    protected final Operation<T> operation;

    public GenericUnaryOperation(GenericExpression<T> expression, Operation<T> operation) {
        this.expression = expression;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return getOperator() + '(' + expression + ')';
    }

    @Override
    public T evaluate(List<T> variables) {
        return action(expression.evaluate(variables));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericUnaryOperation<?> op)) {
            return false;
        }

        return this.getClass() == op.getClass() && expression.equals(op.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, getClass());
    }

    protected abstract T action(T expression);

    protected abstract String getOperator();
}
