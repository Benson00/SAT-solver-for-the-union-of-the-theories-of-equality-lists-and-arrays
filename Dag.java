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
        Node n = this.nodes.get(id);
        if (n.getFind() == id) {
            return id;
        } else {
            return this.find(n.getFind());
        }
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
     * perform the union of two equivalence classes
     * @param id1 id of first node
     * @param id2 id of second node     
     */
    public void union(final int id1, final int id2){
        // TO-DO
        return;
    }

    /**
     * perform merge of the congruence classes of two nodes
     * @param id1 id of first node
     * @param id2 id of second node
     */
    public void merge(final int id1, final int id2){
        // TO-DO
        return;
    }

    /**
     * tests whethe two nodes are congruent
     * @param id1 id of first node
     * @param id2 id of second node
     * @return <code>true</code> if they are congruent, <code>false</code> otherwise
     */
    public boolean congruent(final int id1, final int id2){
        // TO-DO
        return false;
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


    
}
