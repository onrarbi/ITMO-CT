package expression.generic;

public class GenericDivide<T> extends GenericBinaryOperation<T> {

    public GenericDivide(GenericExpression<T> first, GenericExpression<T> second, Operation<T> operation) {
        super(first, second, operation);
    }

    @Override
    protected T action(T first, T second) {
        return operation.divide(first, second);
    }

    @Override
    protected String getOperator() {
        return "/";
    }
}
