package hw5;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String s1= "0";
        String s2 = "0";

        String inputFile = "input.txt";
        String outputFile = "output.txt";

        if (args.length != 0) {
            if (args.length == 2) {
                inputFile = args[0];
                outputFile = args[1];
            } else {
                System.out.println("Usage: java hw5.Main inputFile outputFile");
                return;
            }
        }

        Scanner in = new Scanner(new File(inputFile));
        Writer out = new PrintWriter(outputFile);

        int a = in.nextInt();
        int b = in.nextInt();
        for (int i = 0; i < a; i++) {
            s1 += "'";
        }
        String s = s1;
        for (int i = 0; i < b; i++) {
            s += "'";
            s2 += "'";
        }
        BufferedReader reader = new BufferedReader(new FileReader("first.txt"));
        out.write("|-" + s1 + "+" + s2 + "=" + s + "\n");
        s2 = "0";
        String s3 = s1;
        while ((s = reader.readLine()) != null) {
            out.write(s.replaceAll("x", s1) + "\n");
        }
        for (int i = 0; i < b; i++) {
            reader = new BufferedReader(new FileReader("second.txt"));
            while ((s = reader.readLine()) != null) {
                out.write(s.replaceAll("x", s1).replaceAll("y", s2).replaceAll("z", s3) + "\n");
            }
            s2 += "'";
            s3 += "'";
        }
        out.close();
    }
}
