import java.util.Set;

public class EqualitySolver {
    



    /**
     * Return SAT if the formula is satisfiable in theory of equality. UNSAT otherwise.
     */
    public boolean solve(String formula){
        
        //initialize the dag
        Set<String> fnSet = SATUtils.extractSubterms(formula);
        Dag dag = new Dag(fnSet, formula);

        //learn rules in the formula
        Set<String> eRules = SATUtils.extractERules(formula);
        Set<String> dRules = SATUtils.extractDRules(formula);
        infEqualities(eRules, dag);
        System.out.println(dag);
        if (checkRules(dRules, dag)){
            System.out.println("SAT");
            return false;
        }
        System.out.println("UNSAT");
        return true;
    }
    
        
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
     * modify dag
     * @param rules
     * @param dag
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
