package Solver;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListSolver {

    
    private boolean forbiddenListH = true;

    public void setForbiddenListHToFalse(){
        this.forbiddenListH = false;
    }

    public void setForbiddenListHToTrue(){
        this.forbiddenListH = true;
    }

    
    /**
     * rewrite the formula with the substitution of -atom with cons
     * @param formula the initial formula
     * @return the new formula
     */
    private String rewriteFormula(String formula){
        int cont = 0;
        formula = formula.trim();
        StringBuilder newFormula = new StringBuilder("");
        
        String[] s = formula.split("&");
        for (String part : s){
            part = part.trim();
            String term = "";
            if (part.startsWith("-atom")) {
                for(int i = 0; i < part.length(); i++){
                    if (part.charAt(i) == '(') {
                        String val = "";
                        for(int j = i+1; j < part.length(); j++){
                            if (part.charAt(j) == ')') {
                                break;
                            }
                            val+=part.charAt(j);
                        }
                        term = val+"=";
                    }
                }
                term += "cons(u"+cont;
                cont++;
                term+=",u"+cont+")";
                cont++;
            }else{
                term = part;
            }
            newFormula.append(term).append("&");
        }

        newFormula.deleteCharAt(newFormula.length()-1);
        return newFormula.toString(); 
    }

    private List<String> saveAtoms(String formula) {
        List<String> result = new ArrayList<>();
        String[] parts = formula.split("&");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("atom")) {
                // term extraction 
                int start = part.indexOf('(');
                int end = part.indexOf(')');
                if (start != -1 && end != -1) {
                    String term = part.substring(start + 1, end).trim();
                    result.add(term);
                }
            }
        }
        return result;
    }

    public boolean solve(String formula){
        formula = SATUtils.dropQuantifier(formula);
        formula = SATUtils.rewritePredicate(formula);
        System.out.println(formula);
        //atoms
        String originalFormula = formula;
        

        // rewrite the formula
        formula = this.rewriteFormula(formula);
        
        //extract and build the dag
        Set<String> fnSet = SATUtils.extractSubterms(formula);
        Dag dag = new Dag(fnSet, formula);

        /* For each node n such that n.fn = cons,
            • add car(n) to the DAG and merge car(n) n.args[1];
            • add cdr(n) to the DAG and merge cdr(n) n.args[2] 
        */
        int id = dag.countID();
        int size = dag.countID();
        for (int i = 0; i < size; i++){
            Node n = dag.getNode(i);
            if (n.getFn().equals("cons")) {

                id++;                
                Node newnode = new Node(id, "car", List.of(n.getId()), "car("+n.getTerm()+")");
                n.setCcpar(id);
                dag.addNode(newnode);
                //merge
                dag.merge(newnode.getId(), n.getArgs().get(0));


                id++;
                Node newnode2 = new Node(id, "cdr", List.of(n.getId()), "cdr("+n.getTerm()+")");
                n.setCcpar(id);
                dag.addNode(newnode2);
                //merge
                dag.merge(newnode2.getId(), n.getArgs().get(1));
                
            }
        }

        Set<String> eRules = SATUtils.extractERules(formula);
        Set<String> dRules = SATUtils.extractDRules(formula);
        if(forbiddenListH){
            dag.setForbiddenList(dRules);
        }
        infEqualities(eRules, dag);
        //System.out.println(dag);
        if(!dag.forbidden){
            if (!checkRules(dRules, dag)){
                return false;
            }
        }else{
            if (!dag.forbiddenSat) {
                return false;
            }else{
                if (!checkRules(dRules, dag)){
                    return false;
                }
            }
        }
        //For i ∈ {1,...,ℓ} if ∃v. find v = find ui ∧ v.fn = cons, return unsatisfiable by axiom (atom).
        List<String> atoms = saveAtoms(originalFormula);
        for(String atom : atoms) {
            
            int idV = dag.getIdFromTerm(atom);
            //System.out.println(idV);
            for(Node n : dag){
                
                if (((dag.find(idV) == dag.find(n.getId()))) && (n.getFn().equals("cons"))) {
                    //System.out.println("QUIIII");
                    return false;
                }
            }
        }
        //System.out.println(dag);
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

}
