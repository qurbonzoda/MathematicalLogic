package hw4;

public class Existential extends AbstractExpression {

    Existential(AbstractExpression... expressions) {
        super(expressions);
    }

    public String toString() {
        if (expressions[1].getType() == 'p' || expressions[1].getType() == 'u' || expressions[1].getType() == 'e' || expressions[1].getType() == 'n') {
            return "?" + expressions[0].toString() + expressions[1].toString();
        } else {
            return "?" + expressions[0].toString() + "(" + expressions[1].toString() + ")";
        }
    }

    public char getType() {
        return 'e';
    }
}