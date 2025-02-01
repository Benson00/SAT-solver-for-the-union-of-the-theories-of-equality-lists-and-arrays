package Solver;

public class Solver {
    
    TheorySolver theory;

    public void setTheory(TheorySolver theory) {
        this.theory = theory;
    }

    public boolean solve(String formula){
        return theory.solve(formula);
    }

}
