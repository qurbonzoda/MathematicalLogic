package hw3;

import java.util.Map;

public abstract class AbstractExpression {
    AbstractExpression expression1;

    AbstractExpression expression2;

    public abstract String toString();

    public abstract char getType();

    public abstract boolean evaluate(Map<String, Boolean> map);
}