import java.util.ArrayList;
import java.util.List;
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
     * Constructs a DAG with a deep copy of the provided list of nodes.
     * Each node in the provided list is copied, ensuring the DAG has independent
     * nodes from the original list.
     *
     * @param nodes the list of nodes to initialize the DAG with
     */
    public Dag(final List<Node> nodes) {
        this.nodes = new ArrayList<>();
        for (Node node : nodes) {
            this.nodes.add(new Node(node)); // Deep copy each node
        }
    }

    /**
     * Constructs an empty DAG with no nodes.
     */
    public Dag() {
        this.nodes = new ArrayList<>();
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

}
