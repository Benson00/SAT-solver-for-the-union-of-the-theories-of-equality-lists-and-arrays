import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Define a sample formula
        String formula = "scolapasta(a,dasd(b,c)) & h(c) OR w(a) OR gggg(a,b,c,tonnarelli)";
            
        // Extract function names (fnSet) from the formula using a hypothetical SubtermExtractor
        Set<String> fnSet = SubtermExtractor.extractSubterms(formula);
    
        // Initialize the DAG using the set of functions and the formula
        Dag dag = new Dag(fnSet, formula);
    
        // Print the constructed DAG
        System.out.println("Constructed DAG:");
        System.out.println(dag);
    }

}
