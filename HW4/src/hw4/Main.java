package hw4;

import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Main {
    private static ArrayList<AbstractExpression> expressions = new ArrayList<>();
    private static ArrayList<String> strings = new ArrayList<>();
    private static AbstractExpression axioms[] = new AbstractExpression[11];
    private static String arithmeticAxioms[] = new String[9];
    private static ArrayList<String> assumptions = new ArrayList<>();
    private static AbstractExpression alpha = null;
    private static String theta;
    private static Writer out;
    private static int error;
    private static String errorExpr;
    private static String errorFormula;
    private static String errorVariable;


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
                System.out.println("Usage: java hw4.Main inputFile outputFile");
                return;
            }
        }

        BufferedReader in = new BufferedReader(new FileReader(inputFile));;
        out = new PrintWriter(outputFile);

        String s = in.readLine().replaceAll("\\s", "");
        s = readAssumptions(s);
        ArrayList<Set<String>> freeAssumptionsVariables = new ArrayList<>();
        if (alpha != null) {
            freeAssumptionsVariables.add(getFreeAssumptionsVariables(alpha, new HashSet<>(), new HashSet<>()));
            for (int j = 1; j < assumptions.size(); j++) {
                freeAssumptionsVariables.add(getFreeAssumptionsVariables(ExpressionParser.parse(assumptions.get(j)), new HashSet<>(), new HashSet<>()));
            }

        }
        for (int j = 1; j < assumptions.size(); j++) {
            out.write(assumptions.get(j));
            if (j < assumptions.size() - 1) {
                out.write(",");
            }
        }
        if (alpha != null) {
            out.write("|-");
            if (alpha.getType() == 'i') {
                out.write("(" + alpha.toString() + ")");
            } else {
                out.write(alpha.toString());
            }
            out.write("->" + s + "\n");
        } else {
            out.write("|-" + s + "\n");
        }
        int i = 0;
        while ((s = in.readLine()) != null) {
            error = 0;
            i++;
            expressions.add(ExpressionParser.parse(s));
            strings.add(expressions.get(i).toString());
            if (axiomCheck(expressions.get(i)) && error != 1 || assumptionCheck(strings.get(i))) {
                if (alpha == null) {
                    out.write(strings.get(i) + "\n");
                } else {
                    getAxiomProof(strings.get(i));
                }
            } else if (alpha != null && strings.get(i).equals(alpha.toString())) {
                getAlphaProof();
            } else {
                int a;
                if (expressions.get(i).getType() == 'i') {
                    a = predicateRulesCheck(i, freeAssumptionsVariables);
                } else {
                    a = 0;
                }
                if (a > 0 && error < 2) {
                    if (alpha == null) {
                        out.write(strings.get(i) + "\n");
                    } else {
                        getPredicateRulesProof(expressions.get(i).get(0), expressions.get(i).get(1), a);
                    }
                } else {
                    Pair<Integer, Integer> pair = mPCheck(i);
                    if (pair.getKey() != 0) {
                        if (alpha == null) {
                            out.write(strings.get(i) + "\n");
                        } else {
                            getMPProof(strings.get(pair.getKey()), strings.get(i));
                        }
                    } else {
                        out.close();
                        out = new PrintWriter(args.length == 2 ? args[1] : "output.txt");
                        out.write("Вывод некорректен начиная с формулы номер " + i);
                        if (error == 1) {
                            out.write(": терм " + errorExpr + " не свободен для подстановки в формулу " + errorFormula + " вместо переменной " + errorVariable);
                        } else if (error == 2) {
                            out.write(": переменная " + errorVariable + " входит свободно в формулу " + errorFormula);
                        } else if (error == 3) {
                            out.write(": используется правило с квантором по переменной " + errorVariable + ", входящей свободно в допущение " + errorExpr);
                        }
                        out.write(".");
                        break;
                    }
                }
            }
        }
        out.close();
    }

    private static boolean axiomCheck(AbstractExpression expr) {
        for (int i = 1; i <= 10; i++) {
            if (compare1(axioms[i], expr, new HashMap<>())) {
                return true;
            }
        }
        if (predicateAxiomCheck(expr)) {
            if (arithmeticAxiomCheck(expr)) {
                error = 0;
            } else if (error == 1) {
                if (expr.get(0).getType() == 'u') {
                    errorVariable = expr.get(0).get(0).toString();
                    errorFormula = expr.get(0).get(1).toString();
                } else {
                    errorVariable = expr.get(1).get(0).toString();
                    errorFormula = expr.get(1).get(1).toString();
                }
            }
            return true;
        } else {
            error = 0;
            return arithmeticAxiomCheck(expr);
        }
    }

    private static boolean compare1(AbstractExpression axiom, AbstractExpression expr, Map<String, String> map) {
        if (axiom.getType() == 'p') {
            if (map.containsKey(axiom.toString())) {
                return (map.get(axiom.toString()).equals(expr.toString()));
            } else {
                map.put(axiom.toString(), expr.toString());
                return true;
            }
        } else if (axiom.getType() == 'n' && expr.getType() == 'n') {
            return compare1(axiom.get(0), expr.get(0), map);
        } else {
            return axiom.getType() == expr.getType() && compare1(axiom.get(0), expr.get(0), map) && compare1(axiom.get(1), expr.get(1), map);
        }
    }

    private static boolean predicateAxiomCheck(AbstractExpression expr) {
        if (expr.getType() == 'i') {
            theta = null;
            if (expr.get(0).getType() == 'u') {
                if (compare2(expr.get(0).get(0).toString(), new HashSet<>(), expr.get(0).get(1), expr.get(1), new HashSet<>())) {
                    return true;
                }
            }
            if (expr.get(1).getType() == 'e') {
                return compare2(expr.get(1).get(0).toString(), new HashSet<>(), expr.get(1).get(1), expr.get(0), new HashSet<>());
            }
        }
        return false;
    }

    private static boolean compare2(String var, Set<String> thetaVariables, AbstractExpression expr1, AbstractExpression expr2, Set<String> boundVariables) {
        if (expr1.toString().equals(var)) {
            if (boundVariables.contains(var)) {
                return expr2.toString().equals(var);
            }
            if (theta == null) {
                theta = expr2.toString();
                thetaVariables = getVariables(expr2, thetaVariables);
                for (String variable : thetaVariables) {
                    if (boundVariables.contains(variable)) {
                        error = 1;
                        errorExpr = theta;
                        break;
                    }
                }
                return true;
            } else if (theta.equals(expr2.toString())) {
                if (error == 0) {
                    for (String variable : thetaVariables) {
                        if (boundVariables.contains(variable)) {
                            error = 1;
                            errorExpr = theta;
                            break;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else if (expr1.getType() == expr2.getType()) {
            if (expr2.getType() == 'u' || expr2.getType() == 'e') {
                if (boundVariables.contains(expr2.get(0).toString())) {
                    return expr1.get(0).toString().equals(expr2.get(0).toString()) && compare2(var, thetaVariables, expr1.get(1), expr2.get(1), boundVariables);
                } else {
                    boundVariables.add(expr2.get(0).toString());
                    boolean b = expr1.get(0).toString().equals(expr2.get(0).toString()) && compare2(var, thetaVariables, expr1.get(1), expr2.get(1), boundVariables);
                    boundVariables.remove(expr2.get(0).toString());
                    return b;
                }
            }
            if (expr1.getType() == 'p' || expr1.getType() == 'f') {
                if (expr1.expressions.length == 0 && expr2.expressions.length == 0) {
                    return expr1.toString().equals(expr2.toString());
                } else if (expr1.expressions.length == expr2.expressions.length) {
                    if (!expr1.toString().substring(0, expr1.toString().indexOf('(')).equals(expr2.toString().substring(0, expr2.toString().indexOf('(')))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            for (int i = 0; i < expr1.expressions.length; i++) {
                if (!compare2(var, thetaVariables, expr1.get(i), expr2.get(i), boundVariables)) {
                    return false;
                }
            }
            return true;
        } else return false;
    }

    private static Set<String> getVariables(AbstractExpression expr, Set<String> variables) {
        if (expr.getType() == 'f' && expr.expressions.length == 0) {
            variables.add(expr.toString());
        } else {
            for (AbstractExpression expression : expr.expressions) {
                getVariables(expression, variables);
            }
        }
        return variables;
    }

    private static boolean arithmeticAxiomCheck(AbstractExpression expr) {
        for (int i = 1; i <= 8; i++) {
            if (expr.toString().equals(arithmeticAxioms[i])) {
                return true;
            }
        }
        if (expr.getType() == 'i' && expr.get(0).getType() == 'c' && expr.get(0).get(1).getType() == 'u' && expr.get(0).get(1).get(1).getType() == 'i') {
            AbstractExpression expression = expr.get(0).get(1);
            if (!expr.get(1).toString().equals(expression.get(1).get(0).toString())) {
                return false;
            }
            theta = "0";
            if (compare2(expression.get(0).toString(), new HashSet<>(), expr.get(1), expr.get(0).get(0), new HashSet<>())) {
                theta = expression.get(0).toString() + "'";
                return compare2(expression.get(0).toString(), new HashSet<>(), expr.get(1), expression.get(1).get(1), new HashSet<>());
            } else return false;
        } else {
            return false;
        }
    }

    private static boolean assumptionCheck(String expr) {
        for (int i = 1; i < assumptions.size(); i++) {
            if (assumptions.get(i).equals(expr)) {
                return true;
            }
        }
        return false;
    }

    private static int predicateRulesCheck(int expr, ArrayList<Set<String>> freeAssumptionsVariables) {
        AbstractExpression expr1;
        AbstractExpression expr2;
        String var;
        boolean b;
        boolean c = false;
        String s;
        int number;
        if (expressions.get(expr).get(1).getType() == 'u') {
            number = 1;
            expr1 = expressions.get(expr).get(0);
            expr2 = expressions.get(expr).get(1).get(1);
            var = expressions.get(expr).get(1).get(0).toString();
            b = checkBound(var, expr1, new HashSet<>());
        } else if (expressions.get(expr).get(0).getType() == 'e') {
            number = 2;
            expr1 = expressions.get(expr).get(0).get(1);
            expr2 = expressions.get(expr).get(1);
            var = expressions.get(expr).get(0).get(0).toString();
            b = checkBound(var, expr2, new HashSet<>());
        } else {
            return 0;
        }
        if (expr1.getType() == 'i') {
            s = "(" + expr1.toString() + ")";
        } else {
            s = expr1.toString();
        }
        s += "->" + expr2.toString();
        for (int i = expr - 1; i > 0; i--) {
            if (s.equals(strings.get(i))) {
                c = true;
                break;
            }
        }
        if (c) {
            if (!b) {
                error = 2;
                errorVariable = var;
                if (number == 1) {
                    errorFormula = expr1.toString();
                } else {
                    errorFormula = expr2.toString();
                }
            } else {
                if (freeAssumptionsVariables.get(0).contains(var)) {
                    error = 3;
                    errorVariable = var;
                    errorExpr = alpha.toString();
                }/* else {
                    for (int i = 1; i < freeAssumptionsVariables.size(); i++) {
                        if (freeAssumptionsVariables.get(i).contains(var)) {
                            error = 3;
                            errorVariable = var;
                            errorExpr = assumptions.get(i);
                            break;
                        }
                    }
                }*/
            }
            return number;
        } else {
            return 0;
        }
    }

    private static boolean checkBound(String var, AbstractExpression expr, Set<String> boundVariables) {
        if (expr.getType() == 'u' || expr.getType() == 'e') {
            if (boundVariables.contains(expr.get(0).toString())) {
                return checkBound(var, expr.get(1), boundVariables);
            } else {
                boundVariables.add(expr.get(0).toString());
                boolean b = checkBound(var, expr.get(1), boundVariables);
                boundVariables.remove(expr.get(0).toString());
                return b;
            }
        } else if (expr.toString().equals(var)) {
            return boundVariables.contains(var);
        } else {
            for (AbstractExpression expression : expr.expressions) {
                if (!checkBound(var, expression, boundVariables)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static Pair<Integer, Integer> mPCheck(int expr) {
        for (int i = expr - 1; i > 0; i--) {
            if (expressions.get(i).getType() == 'i') {
                if (expressions.get(i).get(1).toString().equals(strings.get(expr))) {
                    for (int j = expr - 1; j > 0; j--) {
                        if (expressions.get(i).get(0).toString().equals(strings.get(j))) {
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
        in = new BufferedReader(new FileReader("arithmeticAxioms.txt"));
        for (int i = 1; i <= 8; i++) {
            arithmeticAxioms[i] = in.readLine();
        }
    }

    private static String readAssumptions(String s) {
        assumptions.add(null);
        if (s.indexOf("|-") > 0) {
            int k = s.indexOf("|-");
            int b = 0;
            for (int i = 0; i < k; i++) {
                if (s.charAt(i) == '(') {
                    b++;
                } else if (s.charAt(i) == ')') {
                    b--;
                } else if (s.charAt(i) == ',' && b == 0) {
                    assumptions.add(ExpressionParser.parse(s.substring(0, i)).toString());
                    s = s.substring(i + 1);
                    k -= i + 1;
                    i = -1;
                }
            }
            alpha = ExpressionParser.parse(s.substring(0, s.indexOf("|-")));
        }
        s = s.substring(s.indexOf("|-") + 2);
        return ExpressionParser.parse(s).toString();
    }

    private static Set<String> getFreeAssumptionsVariables(AbstractExpression expr, Set<String> boundVariables, Set<String> freeVariables) {
        if (expr.getType() == 'f' && expr.expressions.length == 0 && !boundVariables.contains(expr.toString())) {
            freeVariables.add(expr.toString());
        } else if (expr.getType() == 'u' || expr.getType() == 'e') {
            if (boundVariables.contains(expr.get(0).toString())) {
                getFreeAssumptionsVariables(expr.get(1), boundVariables, freeVariables);
            } else {
                boundVariables.add(expr.get(0).toString());
                getFreeAssumptionsVariables(expr.get(1), boundVariables, freeVariables);
                boundVariables.remove(expr.get(0).toString());
            }
        } else {
            for (AbstractExpression expression : expr.expressions) {
                getFreeAssumptionsVariables(expression, boundVariables, freeVariables);
            }
        }
        return freeVariables;
    }

    private static void getAxiomProof(String expr) throws IOException {
        AbstractExpression axiom = ExpressionParser.parse("(#)->($)->(#)".replaceAll("#", expr).replaceAll("\\$", alpha.toString()));
        out.write(expr + "\n");
        out.write(axiom.toString() + "\n");
        out.write(axiom.get(1).toString() + "\n");
    }

    private static void getAlphaProof() throws IOException {
        out.write(ExpressionParser.parse("(#)->($)->(#)".replaceAll("[#,$]", alpha.toString())).toString() + "\n");
        out.write(ExpressionParser.parse("(#)->($)->(#)".replaceAll("#", alpha.toString()).replaceAll("\\$", "(" + alpha.toString() + ")->(" + alpha.toString() + ")")).toString() + "\n");
        AbstractExpression axiom = ExpressionParser.parse("((#)->($))->((#)->($)->(%))->(#)->(%)".replaceAll("[#,%]", alpha.toString()).replaceAll("\\$", "(" + alpha.toString() + ")->(" + alpha.toString() + ")"));
        out.write(axiom.toString() + "\n");
        out.write(axiom.get(1).toString() + "\n");
        out.write(axiom.get(1).get(1).toString() + "\n");
    }

    private static void getPredicateRulesProof(AbstractExpression expr1, AbstractExpression expr2, int number) throws IOException {
        String s;
        String a = alpha.toString();
        String s1;
        String s2;
        if (number == 1) {
            s1 = expr1.toString();
            s2 = expr2.get(1).toString();
        } else {
            s1 = expr1.get(1).toString();
            s2 = expr2.toString();
        }
        BufferedReader in = new BufferedReader(new FileReader("predicate" + number + ".txt"));
        while ((s = in.readLine()) != null) {
            out.write(ExpressionParser.parse(s.replaceAll("#", a).replaceAll("\\$", s1).replaceAll("%", s2)).toString() + "\n");
        }
        in = new BufferedReader(new FileReader("predicate" + number + "'.txt"));
        s1 = expr1.toString();
        s2 = expr2.toString();
        while ((s = in.readLine()) != null) {
            out.write(ExpressionParser.parse(s.replaceAll("#", a).replaceAll("\\$", s1).replaceAll("%", s2)).toString() + "\n");
        }
    }

    private static void getMPProof(String expr1, String expr2) throws IOException {
        AbstractExpression axiom = ExpressionParser.parse("((#)->($))->((#)->($)->(%))->(#)->(%)".replaceAll("#", alpha.toString()).replaceAll("\\$", expr1).replaceAll("%", expr2));
        out.write(axiom.toString() + "\n");
        out.write(axiom.get(1).toString() + "\n");
        out.write(axiom.get(1).get(1).toString() + "\n");
    }
}
