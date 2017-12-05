package hw1;

public class Variable extends AbstractExpression {
	private String name;
	
	Variable(String name) {
		this.name = name;
	}
		
	public String toString() {
		return name;
	}

	public char getType() {
		return 'v';
	}
}