package expression;

public class Negate extends UnaryOperation{
    public Negate(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int expression) {
        return -expression;
    }

    @Override
    protected String getOperator() {
        return "-";
    }
}
