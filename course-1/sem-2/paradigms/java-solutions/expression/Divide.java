package expression;

public class Divide extends BinaryOperation {
    public Divide(ListExpression first, ListExpression second) {
        super(first, second);
    }

    @Override
    protected int action(int first, int second) {
        if (second == 0){
            throw new ArithmeticException("Division by 0");
        }

        return first / second;
    }

    @Override
    protected String getOperator() {
        return "/";
    }
}
