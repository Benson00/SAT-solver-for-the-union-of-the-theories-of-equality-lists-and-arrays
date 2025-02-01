package Solver;

public interface TheorySolver {
    
    public boolean solve(String formula);

    public void setForbiddenListHToFalse();
    
    public void setForbiddenListHToTrue();

}
