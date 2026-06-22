package expression.generic;

public class GenericAdd<T> extends GenericBinaryOperation<T> {
    public GenericAdd(GenericExpression<T> first, GenericExpression<T> second, Operation<T> operation) {
        super(first, second, operation);
    }

    @Override
    protected T action(T first, T second) {
        return operation.add(first, second);
    }

    @Override
    protected String getOperator() {
        return "+";
    }
}
