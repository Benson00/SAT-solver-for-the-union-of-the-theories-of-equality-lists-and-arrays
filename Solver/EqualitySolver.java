package Solver;
import java.util.Set;


/**
 * <p>The <code>EqualitySolver</code> class provides methods to determine the satisfiability 
 * of a formula in the theory of equality. It processes a formula to extract equality 
 * and disequality rules, builds a directed acyclic graph (DAG) representation of the formula, 
 * and applies congruence closure algorithm to evaluate satisfiability.</p>
 * 
 * <p>The result of the evaluation indicates whether the given formula is satisfiable 
 * in the theory of equality.</p>
 */
public class EqualitySolver implements TheorySolver{

    private boolean forbiddenListH = true;

    @Override
    public void setForbiddenListHToFalse(){
        this.forbiddenListH = false;
    }

    @Override
    public void setForbiddenListHToTrue(){
        this.forbiddenListH = true;
    }
    
    @Override
    /**
     * Return SAT if the formula is satisfiable in theory of equality. UNSAT otherwise.
     * @param formula the formula 
     * @return true if the formula is satisfiable in theory of equality, false otherwise.
     */
    public boolean solve(String formula){
        formula = SATUtils.dropQuantifier(formula);
        formula = SATUtils.rewritePredicate(formula);
        //initialize the dag
        Set<String> fnSet = SATUtils.extractSubterms(formula);
        Dag dag = new Dag(fnSet, formula);

        

        //learn rules in the formula
        Set<String> eRules = SATUtils.extractERules(formula);
        Set<String> dRules = SATUtils.extractDRules(formula);
        
        if(forbiddenListH){
            dag.setForbiddenList(dRules);
        }

        infEqualities(eRules, dag);
        //System.out.println(dag.toString());
        if(!dag.forbidden){
            if (checkRules(dRules, dag)){
                System.out.println("SAT");
                return true;
            }
            System.out.println("UNSAT");
            return false;
        }else{
            if (dag.forbiddenSat) {
                if (checkRules(dRules, dag)){
                    System.out.println("SAT");
                    return true;
                }
                System.out.println("UNSAT");
                return false;
            }else{
                System.out.println("UNSAT");
                return false;
            }
        }

    }
    
    
    /**
     * Checks if the disequalities are respected.
     * 
     * @param dRules the list of disequality rules to be checked
     * @param dag the directed acyclic graph (DAG) to validate against
     * @return true if all disequality rules are respected, false otherwise
     */
    private boolean checkRules(Set<String> dRules, Dag dag) {
        for(String rule : dRules){
            rule = rule.trim();
            String[] s = rule.split("!");
            int id1 = dag.getIdFromTerm(s[0]);
            int id2 = dag.getIdFromTerm(s[1]);
            System.out.println(dag.find(id1) == dag.find(id2));
            if (dag.find(id1) == dag.find(id2)) {
                return false; 
            }
        }
        return true;
    }
        
    /**
     * inference equalities, modify the dag
     * @param rules the rules
     * @param dag the dag
     */
    public void infEqualities(Set<String> rules, Dag dag){
        for(String rule : rules){
            rule = rule.trim();
            String[] s = rule.split("=");
            int id1 = dag.getIdFromTerm(s[0]);
            int id2 = dag.getIdFromTerm(s[1]);
            dag.merge(id1, id2);  
        }
    }


}
