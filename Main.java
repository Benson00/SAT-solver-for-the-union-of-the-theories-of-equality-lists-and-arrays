public class Main {

    public static void main(String[] args) {
        
        String formula = "f(a,b) = a & f(f(a,b),b) ! a";
        EqualitySolver solver = new EqualitySolver();
        solver.solve(formula);

        formula = "f(f(f(a))) = a & f(f(f(f(f(a))))) = a & f(a) ! a";
        solver.solve(formula);
        
    }

}
