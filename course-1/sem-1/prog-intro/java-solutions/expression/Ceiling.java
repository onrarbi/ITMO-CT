package expression;

public class Ceiling extends UnaryOperation {
    public Ceiling(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int value) {
        int mod = value % 1000;

        if (mod == 0) {
            return value;
        }

        if (value > 0) {
            return value + 1000 - mod;
        }

        return value - mod;
    }

    @Override
    protected String getOperator() {
        return "ceiling";
    }
}
