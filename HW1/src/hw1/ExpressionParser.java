package hw1;

class ExpressionParser {
	private static Node node;
	
	static AbstractExpression parse(String s) {
		node = new Node(null);
		int i = 0;
		s = s + " ";
		while (i < s.length()) {
			if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') {
				int j = i++;
				while (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z' || s.charAt(i) >= '0' && s.charAt(i) <= '9') i++;
				node.element = new Variable(s.substring(j, i));
			} else {
				switch (s.charAt(i)) {
					case '!':
						node.n++;
						break;
					case '&':
						makeConjunct();
						break;
					case '|':
						makeDisjunct();
						break;
					case '-':
						makeDisjunct();
						node.stack.push(node.disjunct);
						node.disjunct = null;
						i++;
						break;
					case '(':
						node = new Node(node);
						break;
					case ')':
						makeExpression();
						node.prev.element = node.expression;
						node = node.prev;
						break;
				}
				i++;
			}
		}
		makeExpression();
		return node.expression;
	}

	private static void makeElement() {
		while (node.n > 0) {
			node.element = new Negation(node.element);
			node.n--;
		}
	}

	private static void makeConjunct() {
		makeElement();
		if (node.conjunct == null) {
			node.conjunct = node.element;
		} else {
			node.conjunct = new Conjunction(node.conjunct, node.element);
		}
		node.element = null;
	}
	
	private static void makeDisjunct() {
		makeConjunct();
		if (node.disjunct == null) {
			node.disjunct = node.conjunct;
		} else {
			node.disjunct = new Disjunction(node.disjunct, node.conjunct);
		}
		node.conjunct = null;
	}
	
	private static void makeExpression() {
		makeDisjunct();
		node.expression = node.disjunct;
		while (!node.stack.empty()) {
			node.expression = new Implication(node.stack.pop(), node.expression);
		}
	}
}