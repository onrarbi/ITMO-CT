package expression;

public class Module extends UnaryOperation {
    public Module(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int expression) {
        return Math.abs(expression);
    }

    @Override
    protected String getOperator() {
        return "‖";
    }

    @Override
    public String toString() {
        return "‖" + expression + "‖";
    }
}
