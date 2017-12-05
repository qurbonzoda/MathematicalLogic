package hw1;

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
                System.out.println("Usage: java Main inputFile outputFile");
                return;
            }
        }

        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        Writer out = new PrintWriter(outputFile);

        String s = in.readLine().replaceAll("\\s", "");
        readAssumptions(s);
        writeAssumptions(out);

        int i = 0;
        while ((s = in.readLine()) != null) {
            i++;
            expressions.add(ExpressionParser.parse(s));
            strings.add(expressions.get(i).toString());
            out.write("(" + i + ") " + strings.get(i) + " (");
            int a = axiomCheck(expressions.get(i));
            if (a != 0) {
                out.write("Сх. акс. " + a + ")\n");
            } else {
                a = assumptionCheck(i);
                if (a != 0) {
                    out.write("Предп. " + a + ")\n");
                } else {
                    Pair<Integer, Integer> pair = mPCheck(i);
                    if (pair.getKey() != 0) {
                        out.write("М.Р. " + pair.getKey() + ", " + pair.getValue() + ")\n");
                    } else {
                        expressions.set(i, new Variable(""));
                        strings.set(i, "");
                        out.write("Не доказано)\n");
                    }
                }
            }
        }
        out.close();
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
        for (int i = 1; i < assumptions.size(); i++) {
            if (assumptions.get(i).equals(strings.get(expr))) {
                return i;
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

    private static void readAssumptions(String s) {
        assumptions.add(null);
        while (s.contains(",")) {
            assumptions.add(ExpressionParser.parse(s.substring(0, s.indexOf(','))).toString());
            s = s.substring(s.indexOf(',') + 1);
        }
        if (s.indexOf("|-") > 0) {
            assumptions.add(ExpressionParser.parse(s.substring(0, s.indexOf("|-"))).toString());
        }
        s = s.substring(s.indexOf("|-") + 2);
        alpha = ExpressionParser.parse(s).toString();
    }

    private static void writeAssumptions(Writer out) throws IOException {
        for (int j = 1; j < assumptions.size(); j++) {
            out.write(assumptions.get(j));
            if (j < assumptions.size() - 1) {
                out.write(",");
            }
        }
        out.write("|-" + alpha + "\n");
    }
}
