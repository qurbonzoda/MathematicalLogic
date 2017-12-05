package hw4;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Stack;

class Node {
    String function;
    String predicate;
    Character quantifier;
    boolean equality;
    AbstractExpression multiply;
    AbstractExpression term;
    AbstractExpression unary;
    AbstractExpression element;
    AbstractExpression conjunct;
    AbstractExpression disjunct;
    AbstractExpression expression;
    ArrayList<AbstractExpression> expressions;
    ArrayList<Pair<Character, AbstractExpression>> quantifiers;
    ArrayList<Integer> negations;
    Stack<AbstractExpression> stack;
    int n;
    Node prev;

    Node(Node prev) {
        equality = false;
        this.prev = prev;
        expressions = new ArrayList<>();
        quantifiers = new ArrayList<>();
        negations = new ArrayList<>();
        stack = new Stack<>();
        n = 0;
    }
}
