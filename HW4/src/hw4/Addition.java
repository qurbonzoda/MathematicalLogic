package hw4;

public class Addition extends AbstractExpression {

    Addition(AbstractExpression... expressions) {
        super(expressions);
    }

    public String toString() {
        if (expressions[1].getType() == 'a') {
            return expressions[0].toString() + "+(" + expressions[1].toString() + ")";
        } else {
            return expressions[0].toString() + "+" + expressions[1].toString();
        }
    }

    public char getType() {
        return 'a';
    }
}