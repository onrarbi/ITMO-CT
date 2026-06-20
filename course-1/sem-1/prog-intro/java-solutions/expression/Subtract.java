package expression;

public class Subtract extends BinaryOperation {

    public Subtract(ListExpression first, ListExpression second) {
        super(first, second);
    }

    @Override
    protected int action(int first, int second) {
        return first - second;
    }

    @Override
    protected String getOperator() {
        return "-";
    }
}
