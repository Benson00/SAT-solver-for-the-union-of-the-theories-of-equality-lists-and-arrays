import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubtermExtractor {

  
    /**
     * extract the subterms in the formula
     * @param formula the formula
     * @return a set of subterms
     */
    public static Set<String> extractSubterms(String formula) {
        // Removing logic operators
        formula = formula.replaceAll("\\s*&\\s*|\\s*OR\\s*", ";");
        formula = formula.trim();
        
        List<String> terms = Arrays.asList(formula.split(";"));
        System.out.println("TERMS: "+terms);
        Set<String> subterms = new HashSet<>();
        for (String term : terms) {
            for(int i = 0; i <term.length(); i++) {
                parse(term, subterms);
            }
        }
        return subterms;
    }


    private static void parse(String term, Set<String> subterms){
        StringBuilder newTerm = new StringBuilder("");
        int numPar = 0;
        int numClose = 0;
        boolean atom = true;
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


      public static void main(String[] args) {
        Set<String> subterms = new HashSet<String>();
        System.out.println("SUBSET: "+subterms);
        parse("func(a,g(t(j,k),c))", subterms);
        System.out.println(subterms);
        subterms = new HashSet<String>();
        parse("f(f2(f3(f4(f5(a,b),c))))", subterms);
        System.out.println(subterms);
        subterms = new HashSet<String>();
        parse("scolapasta(a,dasd(tonno(j,k),c))", subterms);
        System.out.println(subterms);  

        String formula = "scolapasta(a,dasd(tonno(j,k),c)) & h(c) OR w(a) OR gggg(a,b,c,tonnarelli)";
        Set<String> fnSet = SubtermExtractor.extractSubterms(formula);
        System.out.println("FINAL SET: "+fnSet);

    }
}
