package hw2;

public class Implication extends AbstractExpression {

    Implication(AbstractExpression expression1, AbstractExpression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    public String toString() {
        if (expression1.getType() == 'i') {
            return "(" + expression1.toString() + ")->" + expression2.toString();
        } else {
            return expression1.toString() + "->" + expression2.toString();
        }
    }

    public char getType() {
        return 'i';
    }

}