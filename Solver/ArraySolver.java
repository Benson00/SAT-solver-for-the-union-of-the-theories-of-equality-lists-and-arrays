package Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArraySolver {

    private boolean forbiddenListH = true;

    public void setForbiddenListHToFalse(){
        this.forbiddenListH = false;
    }

    public void setForbiddenListHToTrue(){
        this.forbiddenListH = true;
    }


    public void rewriteFormula(String formula, List<String> formulas){
        
        if (!formula.contains("store")) {
            formula = rewriteNoStore(formula);
            formulas.add(formula);
            return;
        }else{
            String[] parts = formula.split("\\s*&\\s*|\\s*OR\\s*|\\s*=\\s*|\\s*!\\s*");
            String F1 ="";
            String F2 ="";
            for(String part : parts){
                part = part.trim();
                //System.out.println(part);
                if (!part.contains("select(store")) {
                    continue;
                }else{ 

                    String[] temp = part.split(",");
                    String val3 = temp[temp.length - 1].replaceAll("\\(", "").replaceAll("\\)","");
                    String val2 = temp[temp.length - 2].replaceAll("\\(", "").replaceAll("\\)","");
                    String val1 = temp[temp.length - 3].replaceAll("\\(", "").replaceAll("\\)","");
                    

                    String val0 ="select(";
                    temp = part.split("\\(");
                    int cont = 0;
                    // Usa indexOf per trovare le occorrenze
                    int count = 0;
                    int index = 0;
                    String word = "store";
                    boolean b = false;
                    while ((index = part.indexOf(word, index)) != -1) {
                        count++;
                        index += word.length(); // Spostati dopo l'ultima occorrenza trovata
                    }

                    //System.out.println("Occorrenze di 'store': " + count);
                    count--;
                    
                    for(int i=0; i<temp.length; i++){
                        if (b) {
                            if (temp[i].contains("store")) {
                                temp[i] = temp[i]+"(";
                                val0 += temp[i];
                            }else{
                                
                                for(int j=0; j<temp[i].length(); j++){
                                    if (temp[i].charAt(j) == ')') {
                                        count--;
                                    }
                                    if (count == 0) {
                                        val0 += temp[i].charAt(j);
                                        break;
                                    }else{
                                        val0 += temp[i].charAt(j);
                                    }
                                    
                                }
                            }
                            
                        }else{
                            if (temp[i].contains("store")){
                                cont++;
                                b = true;
                            } 
                            if (cont > 1) {
                                if (temp[i].equals("store")) {
                                    temp[i] = temp[i]+"(";
                                }
                                val0 += temp[i];
    
                            }
                        }
                        
                    }

                    val0 += ","+val3+")";
                
                    //System.out.println("val0:"+val0);

                    
                    //System.out.println("Val3:"+val3+" val2:"+val2+" val1:"+val1);
                    //case 1
                    F1 = val1+"="+val3;
                    F2 = val1+"!"+val3;
                    String[] s = formula.split("&");
                    for(String p : s) {
                        p = p.trim();
                        if(p.contains("select(store")){
                            String[] result = p.split("[!=]");
                            String val4 = result[1].trim();
                            //System.out.println("val4:"+val4);
                            String sep ="";
                            for (int i = 0; i < p.length(); i++){
                                if (p.charAt(i) == '=' || p.charAt(i) == '!') {
                                    sep += p.charAt(i);
                                }
                            }
                            F1 += " & " + val2 + sep + val4;
                            F2 += " & " + val0 + sep + val4;
                        }else{
                            F1 += " & " + p;
                            F2 += " & " + p;
                        }
                        
                    }
                    //CASE 2

                }
            }
            //System.out.println("F1:"+F1);
            //System.out.println("F2:"+F2);
            rewriteFormula(F1, formulas);
            rewriteFormula(F2, formulas);       
        }
        
    }

    /**
     * replace every term in the form select(array,index) in f_array(index).
     * @param formula formula in input
     * @return a new formula with the replacement
     */
    private String rewriteNoStore(String formula){
        formula = formula.replace("select", "f_");
        String regex = "f_\\(([^,]+),\\s*([^\\)]+)\\)";
        String replacement = "f_$1($2)";
        String output = formula.replaceAll(regex, replacement);
        //System.out.println(output);
        return output;
    }

    public boolean solve(String f){
        List<String> formulas = new ArrayList<String>();
        rewriteFormula(f, formulas); 
        for(String formula : formulas){
            if(solvePartial(formula)){
                System.out.println("SAT");
                return true;
            }else{
                continue;
            }
        }
        System.out.println("UNSAT");
        return false;
    }

    public boolean solvePartial(String formula){
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
                System.out.println(formula+": SAT");
                return true;
            }
            System.out.println(formula+": UNSAT");
            return false;
        }else{
            if (dag.forbiddenSat) {
                if (checkRules(dRules, dag)){
                    System.out.println(formula+": SAT");
                    return true;
                }
                System.out.println(formula+": UNSAT");
                return false;
            }else{
                System.out.println(formula+": UNSAT");
                return false;
            }
        }

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

    public void infEqualities(Set<String> rules, Dag dag){
        for(String rule : rules){
            rule = rule.trim();
            String[] s = rule.split("=");
            int id1 = dag.getIdFromTerm(s[0]);
            int id2 = dag.getIdFromTerm(s[1]);
            dag.merge(id1, id2);  
        }
    }



    public static void main(String[] args) {
        String formula = "i1 = j & i1 ! i2 & select(a,j) = v1 & select(store(store(a,i1,v1),i2,v2),j) ! select(a,j)";
        ArraySolver AS = new ArraySolver();
        //AS.setForbiddenListHToFalse();
        AS.solve(formula);
    }


}
