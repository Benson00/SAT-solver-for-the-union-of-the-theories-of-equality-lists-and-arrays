package Solver;

public class Main {
    public static void main(String[] args) {
        String formula = "i1 = j & i1 ! i2 & select(a,j) = v1 & select(store(store(a,i1,v1),i2,v2),j) ! select(a,j)";
        ArraySolver AS = new ArraySolver();
        //AS.setForbiddenListHToFalse();
        AS.solve(formula);
    }
}
