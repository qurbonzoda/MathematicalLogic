package hw3;

import java.util.Map;

public class Disjunction extends AbstractExpression {

	Disjunction(AbstractExpression expression1, AbstractExpression expression2) {
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	public String toString() {
		String s;
		if (expression1.getType() == 'i') {
			s = "(" + expression1.toString() + ")|";
		} else {
			s = expression1.toString() + "|";
		}
		if (expression2.getType() == 'd' || expression2.getType() == 'i') {
			s += "(" + expression2.toString() + ")";
		} else {
			s += expression2.toString();
		}
		return s;
	}

    public char getType() {
        return 'd';
    }

	public boolean evaluate(Map<String, Boolean> map) {
		return expression1.evaluate(map) || expression2.evaluate(map);
	}
}