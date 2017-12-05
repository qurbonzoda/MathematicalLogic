package hw4;

import javafx.util.Pair;

class ExpressionParser {
    private static Node node;

    static AbstractExpression parse(String s) {
        node = new Node(null);
        int i = 0;
        int j;
        s = s + " ";
        while (i < s.length()) {
            if (s.charAt(i) >= 'a' && s.charAt(i) <= 'z') {
                j = i++;
                while (s.charAt(i) >= 'a' && s.charAt(i) <= 'z' || s.charAt(i) >= '0' && s.charAt(i) <= '9') i++;
                node.function = s.substring(j, i);
                checkQuantifier();
            } else if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') {
                j = i++;
                while (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z' || s.charAt(i) >= '0' && s.charAt(i) <= '9') i++;
                node.predicate = s.substring(j, i);
            } else {
                switch (s.charAt(i)) {
                    case '\'':
                        makeElement();
                        node.element = new Successor(node.element);
                        break;
                    case '0':
                        node.element = new Function("0");
                        break;
                    case '*':
                        makeMultiply();
                        break;
                    case '+':
                        makeTerm();
                        break;
                    case '=':
                        makeTerm();
                        node.unary = node.term;
                        node.term = null;
                        node.equality = true;
                        break;
                    case '@':
                        node.quantifier = '@';
                        break;
                    case '?':
                        node.quantifier = '?';
                        break;
                    case ',':
                        makeTerm();
                        node.expressions.add(node.term);
                        node.term = null;
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
                        if (node.prev.predicate != null || node.prev.function != null) {
                            makeTerm();
                            node.expressions.add(node.term);
                            if (node.prev.predicate != null) {
                                node.prev.unary = new Predicate(node.prev.predicate, node.expressions.toArray(new AbstractExpression[node.expressions.size()]));
                                node.prev.predicate = null;
                            } else {
                                node.prev.element = new Function(node.prev.function, node.expressions.toArray(new AbstractExpression[node.expressions.size()]));
                                node.prev.function = null;
                            }
                        } else if ((node.element != null || node.function != null) && !node.equality) {
                            makeTerm();
                            node.prev.element = node.term;
                        } else {
                            makeExpression();
                            node.prev.unary = node.expression;
                        }
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
        if (node.function != null) {
            node.element = new Function(node.function);
            node.function = null;
        }
    }

    private static void makeMultiply() {
        makeElement();
        if (node.multiply == null) {
            node.multiply = node.element;
        } else {
            node.multiply = new Multiplication(node.multiply, node.element);
        }
        node.element = null;
    }

    private static void makeTerm() {
        makeMultiply();
        if (node.term == null) {
            node.term = node.multiply;
        } else {
            node.term = new Addition(node.term, node.multiply);
        }
        node.multiply = null;
    }

    private static void checkQuantifier() {
        if (node.quantifier != null) {
            node.quantifiers.add(new Pair<>(node.quantifier, new Function(node.function)));
            node.negations.add(node.n);
            node.n = 0;
            node.quantifier = null;
            node.function = null;
        }
    }

    private static void makeUnary() {
        if (node.equality) {
            makeTerm();
            node.unary = new Equality(node.unary, node.term);
            node.term = null;
            node.equality = false;
        }
        if (node.predicate != null) {
            node.unary = new Predicate(node.predicate);
            node.predicate = null;
        }
        while (node.n > 0) {
            node.unary = new Negation(node.unary);
            node.n--;
        }
        int n;
        while (node.quantifiers.size() > 0) {
            if (node.quantifiers.get(node.quantifiers.size() - 1).getKey() == '@') {
                node.unary = new Universal(node.quantifiers.get(node.quantifiers.size() - 1).getValue(), node.unary);
            } else {
                node.unary = new Existential(node.quantifiers.get(node.quantifiers.size() - 1).getValue(), node.unary);
            }
            n = node.negations.get(node.negations.size() - 1);
            while (n > 0) {
                node.unary = new Negation(node.unary);
                n--;
            }
            node.quantifiers.remove(node.quantifiers.size() - 1);
            node.negations.remove(node.negations.size() - 1);
        }
    }

    private static void makeConjunct() {
        makeUnary();
        if (node.conjunct == null) {
            node.conjunct = node.unary;
        } else {
            node.conjunct = new Conjunction(node.conjunct, node.unary);
        }
        node.unary = null;
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