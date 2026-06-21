package expression;

import java.util.List;
import java.util.Objects;

public class Variable extends AbstractExpression {
    private final String name;
    private final int id;


    public Variable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Variable(int id) {
        this("$" + id, id);
    }

    public Variable(String name) {
        this.name = name;
        this.id = switch (name) {
            case "x", "$0" -> 0;
            case "y", "$1" -> 1;
            case "z", "$2" -> 2;
            default -> throw new IllegalArgumentException("Unknown variable: " + name);
        };
    }

    @Override
    public int evaluate(List<Integer> variables) {
        if (id >= 0 && id < variables.size()) {
            return variables.get(id);
        }

        throw new IllegalArgumentException("Wrong index: " + name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable var)) {
            return false;
        }
        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}