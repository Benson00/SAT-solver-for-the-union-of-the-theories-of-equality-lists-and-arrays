import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for utility functions
 */
public class SATUtils {

  
    /**
     * extract the subterms in the formula using private function parse
     * @param formula the formula
     * @return a set of subterms
     */
    public static Set<String> extractSubterms(String formula) {
        // Removing logic operators
        formula = formula.replaceAll("\\s*&\\s*|\\s*OR\\s*|\\s*=\\s*|\\s*!\\s*", ";");
        formula = formula.trim();
        List<String> terms = Arrays.asList(formula.split(";"));
        Set<String> subterms = new HashSet<>();
        for (String term : terms) {
            if (!term.contains("(")) {
                subterms.add(term);
            }
            for(int i = 0; i <term.length(); i++) {
                parse(term, subterms);
            }
        }
        return subterms;
    }


    /**
     * private function for subterm extraction. modify subterms.
     * @param term the term to parse
     * @param subterms the subterms
     */
    private static void parse(String term, Set<String> subterms){
        StringBuilder newTerm = new StringBuilder("");
        int numPar = 0;
        int numClose = 0;
        boolean atom = true;
        if (term.length() == 1) {
            subterms.add(term);
        }
        for (int i = 0; i < term.length(); i++) {
            char currentTerm = term.charAt(i);  
            if (currentTerm == '(') {
                atom = false;
                numPar++;
                newTerm.append(currentTerm);
                parse(term.substring(i+1, term.length()), subterms);
            } else if (currentTerm == ')'){
                numClose++;
                if (numClose == numPar) {
                    newTerm.append(currentTerm);
                    subterms.add(newTerm.toString());
                }else if (numClose != numPar && atom) {
                    subterms.add(newTerm.toString());
                    break;
                }else {
                    newTerm.append(currentTerm);
                }   
            } else if (currentTerm == ','){
                if (numClose == numPar) {
                    // atom aggiungo il nuovo termine caso base
                    subterms.add(newTerm.toString());
                    break;
                }else{
                    // not atom appendo 
                    newTerm.append(currentTerm);
                    parse(term.substring(i+1, term.length()), subterms);
                }
            } else {
                newTerm.append(currentTerm);
            }
        }
    }




    /**
     * Extract equality rules from a formula in DNF
     * @param formula the formula
     * @return the rules
     */
    public static Set<String> extractERules(String formula) {
        Set<String> rules = new HashSet<String>();
        String[] s = formula.split("&");
        for (String rule : s){
            if (rule.contains("=")) {
                rules.add(rule);   
            }            
        }
        return rules;
    }

    /**
     * Extract disequality rules from a formula in DNF
     * @param formula the formula
     * @return the rules
     */
    public static Set<String> extractDRules(String formula) {
        Set<String> rules = new HashSet<String>();
        String[] s = formula.split("&");
        for (String rule : s){
            if (rule.contains("!")) {
                rules.add(rule);   
            }            
        }
        return rules;
    }
    
}
