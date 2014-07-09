package graph;
import java.io.*;
import java.util.*;

/**
 * Class to help read a graph from a file
 */
public class Reader
{
    private Graph graph;

    /**
     * Function to obtain the loaded graph
     * @return The loaded graph. Will be null if the graph 
     * has not been loaded
     */
    public Graph graph()
    {
    	return graph;
    }
    

    /**
     * Function that does the actual reading of the graph.
     * @param filename The filename of the file that contains the graph
     * @throws IOException
     */
    public void read(String filename) throws IOException
    {
    	// initialise the graph
    	graph = new Graph();
    	
    	// load the file
        File file = new File(filename);
        Scanner in = new Scanner(file);
        
        // get the top line
        
        String[] topLine = in.nextLine().split(" ");
        
        
        int numberOfNodes = Integer.parseInt(topLine[0]);
        

        
        // read the nodes
        for(int i = 0; i < numberOfNodes; i++)
        {
        	String nodeName = in.nextLine();
        	Node n = new Node(nodeName);
        	graph.add(n);
        }
        
        
        while (in.hasNextLine())
        {
            String line = in.nextLine();
            
            String[] names = line.split(" ");
            String id1 = names[0];
            String id2 = names[1];
            double weight = Double.parseDouble(names[2]);
            Edge.EdgeType type;
            if(names[3].equals("Taxi"))
            {
            	type = Edge.EdgeType.Taxi;
            }
            else if(names[3].equals("Underground"))
            {
            	type = Edge.EdgeType.Underground;
            }
            else 
            {
            	type = Edge.EdgeType.Bus;
            }
            
            // create the edge
            Edge edge = new Edge(id1, id2, weight, type);
            graph.add(edge);
            
            
        }
        
        in.close();
    }
    
    /**
     * Convenience function to quickly load a graph without having to instantiate the object
     * saves a bit of code writing
     * @param filename
     * @return A loaded graph if it loaded OK. Otherwise null
     * @throws IOException
     */
    public static Graph quickRead(String filename) throws IOException
    {
    	Reader  reader = new Reader();
    	reader.read(filename);
    	return reader.graph();
    }

   
}