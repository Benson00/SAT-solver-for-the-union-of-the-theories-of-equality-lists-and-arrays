import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p> Represents a Directed Acyclic Graph (DAG) that consists of a list of nodes. </p>
 * 
 * <p> This class supports initialization with an existing list of nodes, where each
 * node is copied to maintain independent state. </p>
 */
public class Dag {
    
    /**List of nodes in the DAG */
    private final List<Node> nodes; 

    /**
     * Constructor of the Dag from a subset of a formula and the formula.
     * @param fnSet subset of the formula
     * @param formula the formula
     */
    public Dag(final Set<String> fnSet, String formula) {
        this.nodes = new ArrayList<>(DAGBuilder.buildDAG(fnSet, formula));
    }

    private Dag(final List<Node> nodes) {
        this.nodes = nodes;
        for (Node node : nodes) {
            for(Node node2 : nodes){
                if (node.getId() != node2.getId()) {
                    if (node2.getArgs().contains(node.getId())) {
                        node.setCcpar(node2.getId());
                    }
                }
            }
        }
    }

    /**
     * get node from id
     * @param id the id of the node
     * @return the node 
     */
    public Node getNode(int id) {
        return this.nodes.get(id); 
    }

    /**
     * Get the class of the node from the id 
     * @param id the id
     * @return the class of the node
     */
    public int find(int id){
        return this.nodes.get(id).getFind();
    }

    /**
     * Get the parents of the node from the id
     * @param id the id
     * @return the parents of the node
     */
    public Set<Integer> ccpar(int id){
        Node node =  this.getNode(this.find(id));
        return node.getCcpar();
    }

    /**
     * perform the union of two equivalence classes. 
        <pre>
            let union i1 i2 =
            let n1 = node (find i1) in
            let n2 = node (find i2) in
            n1.find ← n2.find;
            n2.ccpar ← n1.ccpar ∪ n2.ccpar;
            n1.ccpar ← ∅
        </pre> 
     * @param id1 id of first node
     * @param id2 id of second node     
     */
    public void union(final int id1, final int id2){
        Node n1 = getNode(this.find(id1));
        Node n2 = getNode(this.find(id2));
        System.out.println(n1.getCcpar().size() + " " + n2.getCcpar().size());
        if (n1.getCcpar().size() < n2.getCcpar().size()){
            
            int temp = n1.getFind();
            n1.setFind(n2.getFind());
            for(Node n : this.nodes){
                int t = n.getFind();
                if (t == temp) {
                    n.setFind(n2.getFind());
                }
            }
            Set<Integer> set = n2.getCcpar();
            for (int i : set){
                n2.setCcpar(i);
            }
            n1.clearCcpar();
        }else{
            n2.setFind(n1.getFind());
            int temp = n2.getFind();
            
            for(Node n : this.nodes){
                int t = n.getFind();
                if (t == temp) {
                    n.setFind(n1.getFind());
                }
            }
            Set<Integer> set = n1.getCcpar();
            for (int i : set){
                n1.setCcpar(i);
            }
            n2.clearCcpar();
        }
    }
    

    /**
     * Performs a merge of the congruence classes of two nodes.
     *
     * The algorithm recursively merges the congruence classes of two nodes, `id1` and `id2`, 
     * if they are in the same equivalence class and meet the congruence condition. 
     * It processes all pairs of related terms from their parent sets (`ccpar`).
     *
     * <pre>
     * Algorithm:
     * 1. Define a recursive function merge(i1, i2):
     *       If `find(i1) == find(i2)`, proceed:
     *           Let Pi1 = ccpar(i1) (parent set of i1).
     *           Let Pi2 = ccpar(i2) (parent set of i2).
     *           Union i1 and i2 into the same equivalence class.
     *           For every pair (t1, t2) in the Cartesian product Pi1 × Pi2:
     *            - If `find(t1) == find(t2)` and `congruent(t1, t2)`:
     *              Call merge(t1, t2) recursively.
     * </pre>
     *
     * @param id1 the ID of the first node
     * @param id2 the ID of the second node
     */
    public void merge(final int id1, final int id2){
        if (find(id1) == find(id2)){} {
            Set<Integer> Pi1 = ccpar(id1);   
            Set<Integer> Pi2 = ccpar(id2); 
            //  pick the one with the largest ccpar set
            this.union(id2, id1);
            for (int i : Pi2) {
                for (int j : Pi1) {
                    if (i!=j) {
                        merge(id1, id2);
                    }                    
                }
            }

        }
    }

    /**
     * tests whethe two nodes are congruent
     * <pre>
            let congruent i1 i2 =
            let n1 = node i1 in
            let n2 = node i2 in
            n1.fn = n2.fn ∧ |n1.args| = |n2.args| ∧ ∀i∈{1,...,|n1.args|}. find n1.args[i] = find n2.args[i]
     * </pre>
     * @param id1 id of first node
     * @param id2 id of second node
     * @return <code>true</code> if they are congruent, <code>false</code> otherwise
     */
    public boolean congruent(final int id1, final int id2){
        Node n1 = getNode(id1);
        Node n2 = getNode(id2);
        if (!n1.getFn().equals(n2.getFn())) {
            return false;
        }
        if (n1.lenArgs() != n2.lenArgs()) {
            return false;
        }
        boolean b = false;
        for (int i : n1.getArgs()) {
            for (int j : n2.getArgs()) {
                Node temp1 = getNode(i);
                Node temp2 = getNode(j);
                if(temp1.getFind() == temp2.getFind()) {
                    b = true;
                }
            }
        }
        if (!b) {
            return false;
        }
        return true;
    } 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DAG Structure:\n");
        for (Node node : nodes) {
            sb.append(node).append("\n");
        }
        return sb.toString();
    }

    /**
     * Builds a Directed Acyclic Graph (DAG) from a set of function terms and a formula.
     */
    private class DAGBuilder {

        /**
         * Builds a DAG (list of nodes) from a set of function terms and a formula.
         *
         * @param fnSet   the set of function terms (e.g., {"f(a,g(b,c))", "g(b,c)", "a", "b", "c"})
         * @param formula the formula string
         * @return a list of nodes representing the DAG
         * @throws IllegalArgumentException if a function term is missing in the fnSet
         */
        public static List<Node> buildDAG(Set<String> fnSet, String formula) {
            Map<String, Integer> termToId = new HashMap<>(); // Map function term to unique ID
            List<Node> nodes = new ArrayList<>();
            int idCounter = 0;

            // Assign unique IDs to all terms in fnSet
            for (String term : fnSet) {
                termToId.put(term, idCounter++);
            }

            // Build nodes for each term in the fnSet
            for (String term : fnSet) {
                nodes.add(buildNode(term, termToId, fnSet));
            }

            for(Node node : nodes) {
                
                for (Node other : nodes){
                    for (int i = 0; i < other.getArgs().size(); i++) {
                        if(node.getId() == other.getArgs().get(i)) {
                            node.setCcpar(other.getId());
                        }
                    }
                }
                
            }

            return nodes;
        }

        /**
         * Builds a single Node from a term.
         *
         * @param term     the term string (e.g., "f(a,g(b,c))")
         * @param termToId map from term to unique ID
         * @param fnSet    the set of function terms
         * @return the Node representing the term
         * @throws IllegalArgumentException if a subterm is missing in fnSet
         */
        private static Node buildNode(String term, Map<String, Integer> termToId, Set<String> fnSet) {
            int id = termToId.get(term);

            // Extract function name and arguments
            String fn = extractFunctionName(term);
            List<String> args = extractArguments(term);

            // Map arguments to their corresponding IDs
            List<Integer> argIds = new ArrayList<>();
            for (String arg : args) {
                if (!termToId.containsKey(arg)) {
                    throw new IllegalArgumentException("Function " + arg + " not found in fnSet.");
                }
                argIds.add(termToId.get(arg));
            }

            // Initialize the node with the extracted information
            Node node = new Node(id, fn, argIds);
            return node;
        }

        /**
         * Extracts the function name from a term.
         *
         * @param term the term string (e.g., "f(a,g(b,c))")
         * @return the function name (e.g., "f")
         */
        private static String extractFunctionName(String term) {
            int parenIndex = term.indexOf('(');
            if (parenIndex == -1) {
                return term; // Term with no arguments (e.g., "a")
            }
            return term.substring(0, parenIndex);
        }

        /**
         * Extracts the arguments from a term.
         *
         * @param term the term string (e.g., "f(a,g(b,c))")
         * @return a list of argument strings (e.g., ["a", "g(b,c)"])
         */
        private static List<String> extractArguments(String term) {
            int parenIndex = term.indexOf('(');
            if (parenIndex == -1) {
                return Collections.emptyList(); // No arguments
            }

            String argString = term.substring(parenIndex + 1, term.lastIndexOf(')'));
            return splitArguments(argString);
        }

        /**
         * Splits a string of arguments into individual arguments, accounting for nested terms.
         *
         * @param argString the string of arguments (e.g., "a,g(b,c)")
         * @return a list of individual argument strings (e.g., ["a", "g(b,c)"])
         */
        private static List<String> splitArguments(String argString) {
            List<String> arguments = new ArrayList<>();
            int balance = 0;
            StringBuilder currentArg = new StringBuilder();

            for (char ch : argString.toCharArray()) {
                if (ch == ',' && balance == 0) {
                    arguments.add(currentArg.toString().trim());
                    currentArg.setLength(0);
                } else {
                    currentArg.append(ch);
                    if (ch == '(') {
                        balance++;
                    } else if (ch == ')') {
                        balance--;
                    }
                }
            }

            if (currentArg.length() > 0) {
                arguments.add(currentArg.toString().trim());
            }

            return arguments;
        }
    }


    public static void main(String[] args) {
        testUnion();
    }    














    // FUNZIONI DI TEST
    private static void testUnion(){
        System.out.println("**********************************************");
        List<Node> nodes = new ArrayList<Node>();
        Node t = new Node(0, "f", List.of(2, 1));
        nodes.add(t);
        t = new Node(1, "a", List.of());
        t.setFind(0);
        nodes.add(t);
        t = new Node(2, "b", List.of());
        t.setFind(0);
        nodes.add(t);
        t = new Node(3, "f", List.of(2,1));
        nodes.add(t);
        t = new Node(4, "u", List.of(3));
        nodes.add(t);
        nodes.get(1).addCcpar(Set.of(0,3));
        nodes.get(2).addCcpar(Set.of(0,3));
        nodes.get(3).addCcpar(Set.of(4));
        Dag dag = new Dag(nodes);
        System.out.println(dag);
        System.out.println("**********************************************");
        System.out.println("UNION TEST");
        dag.union(0, 3); 
        System.out.println(dag);
    }
}
