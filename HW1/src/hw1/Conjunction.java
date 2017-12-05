package hw1;

public class Conjunction extends AbstractExpression {

	Conjunction(AbstractExpression expression1, AbstractExpression expression2) {
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	public String toString() {
		String s;
		if (expression1.getType() == 'd' || expression1.getType() == 'i') {
			s = "(" + expression1.toString() + ")&";
		} else {
			s = expression1.toString() + "&";
		}
		if (expression2.getType() == 'v' || expression2.getType() == 'n') {
			s += expression2.toString();
		} else {
			s += "(" + expression2.toString() + ")";
		}
		return s;
	}

	public char getType() {
		return 'c';
	}
}