package hw4;

public class Function extends AbstractExpression {

    private String name;

    Function(String name, AbstractExpression... expressions) {
        super(expressions);
        this.name = name;
    }

    public String toString() {
        String s = name;
        if (expressions.length > 0) {
            s += "(";
            for (int i = 0; i < expressions.length; i++) {
                s += expressions[i].toString();
                if (i < expressions.length - 1) {
                    s += ",";
                }
            }
            s += ")";
        }
        return s;
    }

    public char getType() {
        return 'f';
    }
}