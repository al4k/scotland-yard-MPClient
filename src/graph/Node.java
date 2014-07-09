package graph;

/**
 * Class representing a node of the graph
 */
public class Node
{
    private String name;

    /**
     * Node constructor
     * @param n Name that will be given to the node
     */
    public Node(String n)
    {
        name = n;
    }

    /**
     * Function that gets the name that has been assigned to the node
     * @return The name of the node
     */
    public String name()
    {
        return name;
    }
}