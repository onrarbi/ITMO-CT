package expression.generic;

public class GenericSubtract<T> extends GenericBinaryOperation<T> {

    public GenericSubtract(GenericExpression<T> first, GenericExpression<T> second, Operation<T> operation) {
        super(first, second, operation);
    }

    @Override
    protected T action(T first, T second) {
        return operation.subtract(first, second);
    }

    @Override
    protected String getOperator() {
        return "-";
    }
}
