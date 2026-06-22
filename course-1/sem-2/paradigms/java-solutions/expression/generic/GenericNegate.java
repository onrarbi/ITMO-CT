package expression.generic;

public class GenericNegate<T> extends GenericUnaryOperation<T> {
    public GenericNegate(GenericExpression<T> expression, Operation<T> operation) {
        super(expression, operation);
    }

    @Override
    protected T action(T expression) {
        return operation.negate(expression);
    }

    @Override
    protected String getOperator() {
        return "-";
    }
}