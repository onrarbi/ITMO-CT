package expression;

public class Low extends UnaryOperation {
    public Low(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int value) {
        if (value == 0) {
            return 0;
        }

        int bit = 1;

        while ((value & bit) == 0) {
            bit <<= 1;
        }

        return bit;
    }

    @Override
    protected String getOperator() {
        return "low";
    }
}
