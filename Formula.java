public class Formula {
    String operator;
    Formula f1;
    Formula f2;


    public Formula(String operator, Formula f1, Formula f2) {
        this.operator = operator;
        this.f1 = f1;
        this.f2 = f2;
    }

    public Formula(String operator) {
        this.operator = operator;
    }


    public static void main(String[] args) {
        Formula formula = new Formula("->", new Formula("->", new Formula("A"), new Formula("C")), new Formula("B"));
    }

}
