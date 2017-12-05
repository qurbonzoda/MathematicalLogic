package hw3;

import java.io.*;
import java.util.*;

public class Main {
    private static AbstractExpression expression;
    private static String[] variables;
    private static Writer tempOut;
    private static Set<String> set = new HashSet<>();
    private static Map<String, Boolean> map = new HashMap<>();

    public static void main(String[] args) throws IOException {

        String inputFile = "input.txt";
        String outputFile = "output.txt";

        if (args.length != 0) {
            if (args.length == 2) {
                inputFile = args[0];
                outputFile = args[1];
            } else {
                System.out.println("Usage: java hw3.Main inputFile outputFile");
                return;
            }
        }

        BufferedReader in = new BufferedReader(new FileReader(inputFile));;
        Writer out = new PrintWriter(outputFile);

        String s = in.readLine();
        expression = ExpressionParser.parse(s);

        variables = getVariablesNames(expression);

        int a = checkTautology();
        if (a != -1) {
            out.write("Высказывание ложно при ");
            for (int i = 0; i < variables.length; i++) {
                out.write(variables[i] + "=" + (a / (1 << i) % 2 == 1 ? "И" : "Л"));
                if (i < variables.length - 1) {
                    out.write(", ");
                }
            }
        } else {
            ArrayList<AbstractExpression> expressions = new ArrayList<>();
            BufferedReader tempIn;
            out.write("|-" + expression.toString() + "\n");
            for (int i = 0; i < (1 << variables.length); i++) {
                tempOut = new PrintWriter("temp0.txt");
                map.clear();
                for (int j = 0; j < variables.length; j++) {
                    if ((i / (1 << j) % 2) == 0) {
                        tempOut.write("!");
                    }
                    tempOut.write(variables[j]);
                    if (j < variables.length - 1) {
                        tempOut.write(",");
                    }
                }
                tempOut.write("|-" + expression.toString() + "\n");
                for (int j = 0; j < variables.length; j++) {
                    map.put(variables[j], (i / (1 << j) % 2) == 1);
                    if ((i / (1 << j) % 2) == 0) {
                        tempOut.write("!");
                    }
                    tempOut.write(variables[j] + "\n");
                }
                proof(map, expression);
                tempOut.close();
                for (int j = 0; j < variables.length; j++) {
                    Deduction.deduction("temp" + j + ".txt", "temp" + (j + 1) + ".txt");
                }
                tempIn = new BufferedReader(new FileReader("temp" + variables.length + ".txt"));
                s = tempIn.readLine();
                expressions.add(ExpressionParser.parse(s.substring(2)));
                while ((s = tempIn.readLine()) != null) {
                    out.write(s + "\n");
                }
                tempIn.close();
            }
            ArrayList<AbstractExpression> expressions1 = expressions;
            AbstractExpression axiom;
            for (int j = 0; j < variables.length; j++) {
                expressions = expressions1;
                expressions1 = new ArrayList<>();
                tempIn = new BufferedReader(new FileReader("proofs/A.txt"));
                while ((s = tempIn.readLine()) != null) {
                    out.write(s.replaceAll("A", variables[j]) + "\n");
                }
                for (int i = 0; i < (1 << variables.length - j); i++) {
                    if (expressions.get(i).expression1.getType() == 'v') {
                        axiom = ExpressionParser.parse("((a)->(c))->((b)->(c))->(a)|(b)->(c)".replaceAll("a", variables[j]).replaceAll("b", "!" + variables[j]).replaceAll("c", expressions.get(i).expression2.toString()));
                        out.write(axiom.toString() + "\n");
                        out.write(axiom.expression2.toString() + "\n");
                        out.write(axiom.expression2.expression2.toString() + "\n");
                        out.write(expressions.get(i).expression2.toString() + "\n");
                        expressions1.add(expressions.get(i).expression2);
                    }
                }
            }
        }
        out.close();
        for (int i = 0; i <= variables.length; i++) {
            new File("temp" + i + ".txt").deleteOnExit();
        }
    }

    private static String[] getVariablesNames(AbstractExpression expr) {
        if (expr.getType() == 'v') {
            set.add(expr.toString());
        } else if (expr.getType() == 'n') {
            getVariablesNames(expr.expression1);
        } else {
            getVariablesNames(expr.expression1);
            getVariablesNames(expr.expression2);
        }

        return set.toArray(new String[set.size()]);
    }

    private static int checkTautology() {
        for (int i = 0; i < (1 << variables.length); i++) {
            map.clear();
            for (int j = 0; j < variables.length; j++) {
                map.put(variables[j], (i / (1 << j) % 2) == 1);
            }
            if (!expression.evaluate(map)) {
                return i;
            }
        }
        return -1;
    }

    private static void proof(Map<String, Boolean> map, AbstractExpression expr) throws IOException{
        BufferedReader in;
        String s;
        if (expr.getType() == 'n') {
            proof(map, expr.expression1);
            if (expr.expression1.evaluate(map)) {
                in = new BufferedReader(new FileReader("proofs/n1.txt"));
                while ((s = in.readLine()) != null) {
                    tempOut.write(ExpressionParser.parse(s.replaceAll("A", expr.expression1.toString())).toString() + "\n");
                }
            }
        } else if(expr.getType() != 'v') {
            proof(map, expr.expression1);
            proof(map, expr.expression2);
            in = new BufferedReader(new FileReader("proofs/" + expr.getType() + (expr.expression1.evaluate(map) ? "1" : "0") + (expr.expression2.evaluate(map) ? "1" : "0") + ".txt"));
            while ((s = in.readLine()) != null) {
                tempOut.write(ExpressionParser.parse(s.replaceAll("B", "b").replaceAll("A", expr.expression1.toString()).replaceAll("b", expr.expression2.toString())).toString() + "\n");
            }
        }
    }
}
