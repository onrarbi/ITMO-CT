package expression;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);

        ListExpression expression = new Add(
                new Subtract(
                        new Multiply(new Variable(0), new Variable(0)),
                        new Multiply(new Const(2), new Variable(0))
                ),
                new Const(1)
        );

        System.out.println(expression.evaluate(List.of(x)));
    }
}