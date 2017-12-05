package hw4;

public class Successor extends AbstractExpression {

    Successor(AbstractExpression... expressions) {
        super(expressions);
    }

    public String toString() {
        if (expressions[0].getType() == 'f' || expressions[0].getType() == 's') {
            return expressions[0].toString() + "'";
        } else {
            return "(" + expressions[0].toString() + ")'";
        }

    }

    public char getType() {
        return 's';
    }
}