import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Define a sample formula
        String formula = "f(a,f(f(j,k),c)) = h(c) & w(a) ! f(a,b,c,tonnarelli) & y";
            
        // Extract function names (fnSet) from the formula using a hypothetical SubtermExtractor
        Set<String> fnSet = SATUtils.extractSubterms(formula);
        System.out.println(fnSet);
        // Initialize the DAG using the set of functions and the formula
        Dag dag = new Dag(fnSet, formula);
    
        System.out.println("FORMULA: "+formula);
        System.out.println("Subset of formula's terms: "+fnSet);
        // Print the constructed DAG
        System.out.println("Constructed DAG:");
        System.out.println(dag);
    }

}
