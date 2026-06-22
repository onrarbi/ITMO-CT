package expression.generic;

public class GenericMultiply<T> extends GenericBinaryOperation<T> {

    public GenericMultiply(GenericExpression<T> first, GenericExpression<T> second, Operation<T> operation) {
        super(first, second, operation);
    }

    @Override
    protected T action(T first, T second) {
        return operation.multiply(first, second);
    }

    @Override
    protected String getOperator() {
        return "*";
    }
}
