package expression;

public class Multiply extends BinaryOperation {
    public Multiply(ListExpression first, ListExpression second) {
        super(first, second);
    }

    @Override
    protected int action(int first, int second) {
        return first * second;
    }

    @Override
    protected String getOperator() {
        return "*";
    }
}
