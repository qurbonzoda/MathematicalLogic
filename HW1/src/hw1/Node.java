package hw1;

import java.util.Stack;

class Node {
    AbstractExpression element;
	AbstractExpression conjunct;
	AbstractExpression disjunct;
	AbstractExpression expression;
	Stack<AbstractExpression> stack;
	int n;
    Node prev;

    Node(Node prev) {
        element = null;
		conjunct = null;
		disjunct = null;
		expression = null;
		this.prev = prev;
		stack = new Stack<>();
		n = 0;
    }
}
