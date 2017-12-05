package hw2;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static ArrayList<AbstractExpression> expressions = new ArrayList<>();
    private static ArrayList<String> strings = new ArrayList<>();
    private static AbstractExpression axioms[] = new AbstractExpression[11];
    private static ArrayList<String> assumptions = new ArrayList<>();
    private static String alpha;
    private static Map<String, String> map = new HashMap<>();


    public static void main(String[] args) throws IOException {
        readAxioms();
        expressions.add(null);
        strings.add(null);

        String inputFile = "input.txt";
        String outputFile = "output.txt";

        if (args.length != 0) {
            if (args.length == 2) {
                inputFile = args[0];
                outputFile = args[1];
            } else {
                System.out.println("Usage: java hw2.Main inputFile outputFile");
                return;
            }
        }

        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        Writer out = new PrintWriter(outputFile);

        String s = in.readLine().replaceAll("\\s", "");
        readAssumptions(s, out);

        int i = 0;
        while ((s = in.readLine()) != null) {
            i++;
            expressions.add(ExpressionParser.parse(s));
            strings.add(expressions.get(i).toString());

            int a = axiomCheck(expressions.get(i));
            if (a == 0) {
                a = assumptionCheck(i);
            }
            if (a != 0) {
                out.write(getAxiomProof(strings.get(i)));
            } else if (strings.get(i).equals(alpha)) {
                out.write(getAlphaProof());
            } else {
                Pair<Integer, Integer> pair = mPCheck(i);
                out.write(getMPProof(strings.get(pair.getKey()), strings.get(i)));
            }
        }
        out.close();
    }

    private static String getAxiomProof(String expr) {
        String s;
        AbstractExpression axiom = ExpressionParser.parse("(a)->(b)->(a)".replaceAll("a", expr).replaceAll("b", alpha));
        s = expr + "\n";
        s += axiom.toString() + "\n";
        s += axiom.expression2.toString() + "\n";
        return s;
    }

    private static String getAlphaProof() {
        String s;
        s = ExpressionParser.parse("(a)->(b)->(a)".replaceAll("[a,b]", alpha)).toString() + "\n";
        s += ExpressionParser.parse("(a)->(b)->(a)".replaceAll("a", alpha).replaceAll("b", "(" + alpha + ")->(" + alpha + ")")).toString() + "\n";
        AbstractExpression axiom = ExpressionParser.parse("((a)->(b))->((a)->(b)->(c))->(a)->(c)".replaceAll("[a,c]", alpha).replaceAll("b", "(" + alpha + ")->(" + alpha + ")"));
        s += axiom.toString() + "\n";
        s += axiom.expression2.toString() + "\n";
        s += axiom.expression2.expression2.toString() + "\n";
        return s;
    }

    private static String getMPProof(String expr1, String expr2) {
        String s;
        AbstractExpression axiom = ExpressionParser.parse("((a)->(b))->((a)->(b)->(c))->(a)->(c)".replaceAll("a", alpha).replaceAll("b", expr1).replaceAll("c", expr2));
        s = axiom.toString() + "\n";
        s += axiom.expression2.toString() + "\n";
        s += axiom.expression2.expression2.toString() + "\n";
        return s;
    }

    private static int axiomCheck(AbstractExpression expr) {
        for (int i = 1; i <= 10; i++) {
            map.clear();
            if (compare(axioms[i], expr)) {
                return i;
            }
        }
        return 0;
    }

    private static boolean compare(AbstractExpression axiom, AbstractExpression expr) {
        if (axiom.getType() == 'v') {
            if (map.containsKey(axiom.toString())) {
                return (map.get(axiom.toString()).equals(expr.toString()));
            } else {
                map.put(axiom.toString(), expr.toString());
                return true;
            }
        } else if (axiom.getType() == 'n' && expr.getType() == 'n') {
            return compare(axiom.expression1, expr.expression1);
        } else {
            return axiom.getType() == expr.getType() && compare(axiom.expression1, expr.expression1) && compare(axiom.expression2, expr.expression2);
        }
    }

    private static int assumptionCheck(int expr) {
        for (String expression : assumptions) {
            if (expression.equals(strings.get(expr))) {
                return 1;
            }
        }
        return 0;
    }

    private static Pair<Integer, Integer> mPCheck(int expr) {
        for (int i = expr - 1; i > 0; i--) {
            if (expressions.get(i).getType() == 'i') {
                if (expressions.get(i).expression2.toString().equals(strings.get(expr))) {
                    for (int j = expr - 1; j > 0; j--) {
                        if (expressions.get(i).expression1.toString().equals(strings.get(j))) {
                            return (new Pair<>(j, i));
                        }
                    }
                }

            }
        }
        return new Pair<>(0, 0);
    }

    private static void readAxioms() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("axioms.txt"));
        String s;
        for (int i = 1; i <= 10; i++) {
            s = in.readLine();
            axioms[i] = ExpressionParser.parse(s);
        }
    }

    private static void readAssumptions(String s, Writer out) throws IOException {
        AbstractExpression assumption;
        while (s.contains(",")) {
            assumption = ExpressionParser.parse(s.substring(0, s.indexOf(',')));
            assumptions.add(assumption.toString());
            out.write(assumption.toString());
            s = s.substring(s.indexOf(',') + 1);
            if (s.contains(",")) {
                out.write(",");
            }
        }
        assumption = ExpressionParser.parse(s.substring(0, s.indexOf("|-")));
        alpha = assumption.toString();
        if (assumption.getType() == 'i') {
            out.write("|-(" + alpha + ")->");
        } else {
            out.write("|-" + alpha + "->");
        }
        out.write(ExpressionParser.parse(s.substring(s.indexOf("|-") + 2)).toString() + "\n");
    }
}
