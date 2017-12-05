package hw4;

public class Disjunction extends AbstractExpression {

    Disjunction(AbstractExpression... expressions) {
        super(expressions);
    }

    public String toString() {
        String s;
        if (expressions[0].getType() == 'i') {
            s = "(" + expressions[0].toString() + ")|";
        } else {
            s = expressions[0].toString() + "|";
        }
        if (expressions[1].getType() == 'd' || expressions[1].getType() == 'i') {
            s += "(" + expressions[1].toString() + ")";
        } else {
            s += expressions[1].toString();
        }
        return s;
    }

    public char getType() {
        return 'd';
    }
}