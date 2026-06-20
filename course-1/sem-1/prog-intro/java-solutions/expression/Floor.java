package expression;

public class Floor extends UnaryOperation {
    public Floor(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int value) {
        return Math.floorDiv(value, 1000) * 1000;
    }

    @Override
    protected String getOperator() {
        return "floor";
    }
}
