package expression;

public class CubeRoot extends UnaryOperation {
    public CubeRoot(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int expression) {
        return (int) Math.cbrt(expression);
    }

    @Override
    protected String getOperator() {
        return "∛";
    }
}
