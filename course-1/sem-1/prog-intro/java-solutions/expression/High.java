package expression;

public class High extends UnaryOperation {
    public High(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int value) {
        if (value == 0) {
            return 0;
        }

        int bit = Integer.MIN_VALUE;

        while ((value & bit) == 0) {
            bit >>>= 1;
        }

        return bit;
    }

    @Override
    protected String getOperator() {
        return "high";
    }
}
