package Solver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p> Represents a Directed Acyclic Graph (DAG) that consists of a list of nodes. </p>
 * Contains methods for the congruence closure algorithm.
 */
public class Dag implements Iterable<Node>{
    
    /**List of nodes in the DAG */
    private final List<Node> nodes; 

    private final Map<Integer, Set<Integer>> forbiddenList = new HashMap<Integer, Set<Integer>>();
    public boolean forbidden = false;
    public boolean forbiddenSat = true;

    public void setForbiddenList (Set<String> rules){
        this.forbidden = true;
        for(String rule : rules){
            rule = rule.trim();
            String[] s = rule.split("!");
            int id1 = this.getIdFromTerm(s[0]);
            int id2 = this.getIdFromTerm(s[1]);
            if (!forbiddenList.containsKey(id1)) {
                this.forbiddenList.put(id1, new HashSet<Integer>()); 
                this.forbiddenList.get(id1).add(id2); 
            }else{
                this.forbiddenList.get(id1).add(id2);
            }

            if (!forbiddenList.containsKey(id2)) {
                this.forbiddenList.put(id2, new HashSet<Integer>()); 
                this.forbiddenList.get(id2).add(id1);
            }else{
                this.forbiddenList.get(id2).add(id1);
            }  
        }
        for(Node n:nodes){
            if(!forbiddenList.containsKey(n.getId())){
                this.forbiddenList.put(n.getId(), new HashSet<Integer>()); 
            }
        }
    }


    /**
     * Constructor of the Dag from a subset of a formula and the formula.
     * @param fnSet subset of the formula
     * @param formula the formula
     */
    public Dag(final Set<String> fnSet, String formula) {
        this.nodes = new ArrayList<>(DAGBuilder.buildDAG(fnSet, formula));
    }

    /**
     * Build a dag from a list of nodes
     * @param nodes list of nodes
     */
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
     * add a node to the dag.
     * @param n the node
     */
    public void addNode(Node n){
        nodes.add(n);
    }

    public int countID(){
        return nodes.size()-1;
    }


    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public int getIdFromTerm(String term){
        
        for(Node node : nodes){
            if (term.trim().equals(node.getTerm().trim())) {
                
                return node.getId();
            }
        }
        throw new IllegalArgumentException("Invalid term: " + term);
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
     * Non recursive version of the find.
     * Get the class of the node from the id.
     * @param id the id
     * @return the class of the node
     */
    public int find(int id){
        /* Node n = this.nodes.get(id);
        if (n.getFind() == id) {
            return id;
        }else{
            return this.find(n.getFind());
        } */
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
     * The representative of the union class is the one with the largest ccpar. 
     * Updates the find field of all the terms in the class whose representative is not chosen 
     * as representative of the union class.
        <pre>
            let union i1 i2 =
            let n1 = node (find i1) in
            let n2 = node (find i2) in
            n1.find <- n2.find;
            n2.ccpar <- n1.ccpar ∪ n2.ccpar;
            n1.ccpar <- ∅
        </pre> 
     * @param id1 id of first node
     * @param id2 id of second node     
     */
    public void union(final int id1, final int id2){
        System.out.println("UNION: " + id1 + " " + id2);
        Node n1 = getNode(this.find(id1));
        Node n2 = getNode(this.find(id2));
        if (n1.getCcpar().size() < n2.getCcpar().size()){
            
            
            n1.setFind(n2.getFind());
            for(Node n : this.nodes){
                int t = n.getFind();
                if (t == n1.getId()) {
                    n.setFind(n2.getFind());
                }
            }
            Set<Integer> set = n1.getCcpar();
            for (int i : set){
                n2.setCcpar(i);
            }
            n1.clearCcpar();
            
            if (forbidden) {
                Set<Integer> l = this.forbiddenList.get(id1);
                this.forbiddenList.get(id2).addAll(l);
                this.forbiddenList.get(id1).clear();
            }
            

        }else{
            n2.setFind(n1.getFind());
            
            for(Node n : this.nodes){
                int t = n.getFind();
                if (t == n2.getId()) {
                    n.setFind(n1.getFind());
                }
            }
            Set<Integer> set = n2.getCcpar();
            for (int i : set){
                n1.setCcpar(i);
            }
            n2.clearCcpar();
            
            if (forbidden) {
                Set<Integer> l = this.forbiddenList.get(id2);
                this.forbiddenList.get(id1).addAll(l);
                this.forbiddenList.get(id2).clear();
            }
            
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
     *       If `find(i1) != find(i2)`, proceed:
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
        
        System.out.println("MERGING: " + id1 + " " + id2);
        // check forbidden list
        if (forbidden) {
            if (this.forbiddenList.containsKey(id1)){
                if(this.forbiddenList.get(id1).contains(id2)){
                    System.out.println("CIAOO");
                    this.forbiddenSat = false;
                    return;
                }
                
            }
            if (this.forbiddenList.containsKey(id2)){
                if (this.forbiddenList.get(id2).contains(id1)) {
                    this.forbiddenSat = false;
                    System.out.println("CIAOOO");
                    return;
                }
                
            }
        }
        if (find(id1) != find(id2)) {
            
            Set<Integer> Pi1 = Set.copyOf(this.ccpar(id1));
            Set<Integer> Pi2 = Set.copyOf(this.ccpar(id2));
            union(id1, id2);
            for (int t1 : Pi1) {
                for (int t2 : Pi2) {
                    if ((t1!=t2) && (find(t1) != find(t2)) && congruent(t1, t2)) {
                        merge(t1, t2);
                        if (!forbiddenSat) {
                           return; 
                        }
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
        
        for (int i : n1.getArgs()) {
            boolean b = false;
            for (int j : n2.getArgs()) {
                Node temp1 = getNode(i);
                Node temp2 = getNode(j);
                if(temp1.getFind() == temp2.getFind()) {
                    b = true;
                }
            }
            if (!b) return false;
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
        System.out.println("forbidden list: "+this.forbiddenList);
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

            // set ccpar
            for(Node node : nodes) {
                
                for (Node other : nodes){
                    for (int i = 0; i < other.getArgs().size(); i++) {
                        if(node.getId() == other.getArgs().get(i)) {
                            node.setCcpar(other.getId());
                        }
                    }
                }
                
            }

            Dag dag = new Dag(nodes);          

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
            Node node = new Node(id, fn, argIds, term);
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

}
