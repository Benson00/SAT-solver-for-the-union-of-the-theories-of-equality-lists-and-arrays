import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubtermExtractor {

    private static void main(String[] args) {
        String formula = "f(a,g(b,c)) & h(c) OR w(a)";
        Set<String> subterms = extractSubterms(formula);
        System.out.println("SUBSET: "+subterms);

        formula = "f(a,dasd(b,c)) & h(c) OR w(a) OR gggg(a,b,c,tonnarelli)";
        subterms = extractSubterms(formula);
        System.out.println("SUBSET: "+subterms);
    }

    public static Set<String> extractSubterms(String formula) {
        // Removing logic operators
        formula = formula.replaceAll("\\s*&\\s*|\\s*OR\\s*", ";");
        List<String> terms = Arrays.asList(formula.split(";"));
        Set<String> subterms = new HashSet<>();
        for (String term : terms){
            subterms.add(term);
        }
        //System.out.println("SUBSET:"+subterms);
        for (String term : terms) {
            for(int i = 0; i <term.length(); i++) {
                if (term.charAt(i) == '(') {
                    term = term.substring(i+1, term.length()-1);
                    //System.out.println(term);
                    parseTerm(term, subterms);
                }
            }
            
        }
        return subterms;
    }

    private static void parseTerm(String term, Set<String> subterms) {
        int len = term.length();
        int openParenCount = 0;
        StringBuilder currentTerm = new StringBuilder();

        for (int i = 0; i < len; i++) {
            char ch = term.charAt(i);

            if (ch == '(') {
                // Se troviamo una parentesi, iniziamo una nuova espressione
                openParenCount++;
                currentTerm.append(ch);
            } else if (ch == ')') {
                // Se troviamo una parentesi chiusa, finiamo un'espressione
                openParenCount--;
                currentTerm.append(ch);

                if (openParenCount == 0) {
                    // Se abbiamo chiuso tutte le parentesi, un subterm è completo
                    subterms.add(currentTerm.toString());
                    currentTerm.setLength(0); // Pulisci il buffer
                }
            } else if (openParenCount > 0 || ch != ',') {
                // Se siamo dentro parentesi o il carattere non è una virgola, accumuliamo il subterm
                currentTerm.append(ch);
            } else if (ch == ',') {
                // Quando troviamo una virgola, significa che un subterm è stato separato
                if (currentTerm.length() > 0) {
                    subterms.add(currentTerm.toString().trim());
                    currentTerm.setLength(0); // Pulisci il buffer
                }
            }
        }

        // Se rimane qualcosa nel buffer, aggiungiamolo come subterm finale
        if (currentTerm.length() > 0) {
            subterms.add(currentTerm.toString().trim());
        }

    }
}
