package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a graph. This class is based around a
 * List of nodes and a List of edges. The nodes are very simple 
 * classes that only contain the name of the node. The edges are 
 * more important as they define the structure of the graph. 
 */
public class Graph
{
    private List<Node> nodes;
    private List<Edge> edges;

    /**
     * Graph constructor. This initialises the lists that 
     * will hold the nodes and edges
     */
    public Graph()
    {
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
    }

    /**
     * Function that returns the list of nodes from the graph
     * @return The list of nodes
     */
    public List<Node> nodes()
    {
        return nodes;
    }
    
    /**
     * Function that returns the list of edges from the graph
     * @return The list of edges
     */
	public List<Edge> edges() 
	{
		return edges;
	}

	/**
	 * Function to find a node in the graph given the nodes name. 
	 * This function will search through the list of nodes and check
	 * each of their names. If it finds a matching node, it will be 
	 * returned. If not, it will return null.
	 * @param name The name of the node that you wish to find
	 * @return The found node or null
	 */
    public Node find(String name)
    {
        for (Node n : nodes)
        {
            if (n.name().equals(name)) return n;
        }
        return null;
    }
    
    /**
     * Finds a node given an id. The id represents the position of the
     * node in the list of nodes
     * If the id is out of bounds, null is returned
     * @param index The index of the node
     * @return The desired node or null
     */
    public Node find(int index)
    {
    	if(index < 0 || index >= nodeNumber())
    	{
    		return null;
    	}
    	return nodes.get(index);
    }
    
    /**
     * Returns the number of nodes in the graph
     * @return The number of nodes in the graph
     */
    public int nodeNumber()
    {
    	return nodes.size();
    }
    
    /**
     * Function to add a new node to the graph
     * @param node The node you wish to add
     */
    public void add(Node node)
    {
        nodes.add(node);
    }
    
    /**
     * Function to add a new edge to the graph
     * @param edge The edge you wish to add
     */
    public void add(Edge edge)
    {
    	edges.add(edge);
    }
    
    
    /**
     * Function to get the set of edges that blong to a given node
     * @param nodeName The name of the node
     * @return List of edges
     */
    public List<Edge> edges(String nodeName)
    {
    	List<Edge> output = new ArrayList<Edge>();
    	
    	for(Edge e : edges)
    	{
    		if(e.connectsNode(nodeName))
    		{
    			output.add(e);
    		}
    	}
    	
    	return output;
    }




}
