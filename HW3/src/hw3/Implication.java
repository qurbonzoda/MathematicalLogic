package hw3;

import java.util.Map;

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

	public boolean evaluate(Map<String, Boolean> map) {
		return !expression1.evaluate(map) || expression2.evaluate(map);
	}
}