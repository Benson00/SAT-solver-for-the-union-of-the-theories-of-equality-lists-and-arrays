import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in a Directed Acyclic Graph (DAG) used for the congruence closure algorithm.
 * This structure efficiently represents terms and their relationships within the subterm set.
 *
 * <p>Each node has the following structure:
 * <pre>
 * type node = {
 *      id : int,              // Unique identifier for the node
 *      fn : string,           // Function name associated with the node
 *      args : list of id,     // List of argument IDs representing subterms
 *      mutable find : id,     // ID of the representative node for the congruence class
 *      mutable ccpar : set of id  // Set of parent node IDs in the DAG
 * }
 * </pre>
 *
 * <p>The <code>find</code> field holds the ID of the representative node of the congruence class
 * to which this node belongs. Initially, each node <code>n</code> is its own representative,
 * meaning <code>n.find = n</code>.
 *
 * <p>The <code>ccpar</code> field represents the set of parent nodes for this node, helping
 * track parent relationships within the DAG structure.
 */
public class Node {

    private final int id;             // Unique identifier for the node
    private final String fn;          // Function name associated with the node
    private final List<Integer> args; // List of argument IDs representing subterms
    private int find;                 // ID of the representative node for the congruence class
    private Set<Integer> ccpar;       // Set of parent node IDs in the DAG
    private final String term;

    /**
     * Constructs a Node with the specified id, function name, and arguments.
     * Initializes the find field to be the node's own ID and ccpar as an empty set.
     *
     * @param id the unique identifier for the node
     * @param fn the function name associated with the node
     * @param args the list of argument IDs for this node
     */
    public Node(int id, String fn, List<Integer> args, String term) {
        this.id = id;
        this.fn = fn;
        this.args = args;
        this.find = id;               // Initially, the node is its own representative
        this.ccpar = new HashSet<>();  // Initialize an empty set for parent nodes
        this.term = term.trim(); 
    }

    /**
     * Copy constructor that creates a new Node with the same properties as the given node.
     * 
     * @param other the node to copy
     */
    public Node(Node other) {
        this.term = other.term;
        this.id = other.id;
        this.fn = other.fn;
        this.args = other.args; // Shallow copy, since args is immutable in this context
        this.find = other.find;
        this.ccpar = new HashSet<>(other.ccpar); // Deep copy to avoid shared state
    }

    /**
     * Gets the unique identifier of the node.
     * 
     * @return the ID of the node
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the function name associated with the node.
     * 
     * @return the function name
     */
    public String getFn() {
        return fn;
    }

    /**
     * Gets the list of argument IDs for this node.
     * 
     * @return the list of argument IDs
     */
    public List<Integer> getArgs() {
        return args;
    }

    /**
     * Gets the representative node ID of the congruence class.
     * 
     * @return the ID of the representative node
     */
    public int getFind() {
        return find;
    }

    /**
     * Set the representative node ID for the congruence class.
     * 
     * @param find the ID of the new representative node
     */
    public void setFind(int find) {
        this.find = find;
    }

    /**
     * Gets the set of parent node IDs in the DAG.
     * 
     * @return the set of parent IDs
     */
    public Set<Integer> getCcpar() {
        return ccpar;
    }

    public void clearCcpar(){
        this.ccpar.clear();
    }


    /**
     * modify the ccpar value adding the id in input
     * @param id the id of the father
     */
    public void setCcpar(int id) {
        this.ccpar.add(id);
    }


    /**
     * Adds a parent node ID to the set of parent nodes.
     * 
     * @param parentId the ID of the parent node to add
     */
    public void addParent(int parentId) {
        ccpar.add(parentId);
    }

    public String getTerm(){
        return this.term;
    }

    /**
     * Removes a parent node ID from the set of parent nodes.
     * 
     * @param parentId the ID of the parent node to remove
     */
    public void removeParent(int parentId) {
        ccpar.remove(parentId);
    }

    public void addCcpar(Set<Integer> cp2){
        for (int i : cp2){
            this.ccpar.add(i);
        }
    }

    /**
     * Return the len of the args
     * @return the len of the args
     */
    public int lenArgs(){
        return this.args.size();
    }

    /**
     * Checks if this node has a specific parent ID.
     * 
     * @param parentId the ID of the parent to check for
     * @return true if the parent ID is in the set of parents, false otherwise
     */
    public boolean hasParent(int parentId) {
        return ccpar.contains(parentId);
    }

    @Override
    public String toString() {
        return "Node{" +
            "\n\tid=" + id +
            "\n\tfn='" + fn + '\'' +
            "\n\targs=" + args +
            "\n\tfind=" + find +
            "\n\tccpar=" + ccpar +
            "\n\tterm=" + term +
            "\n}";
    }

}
