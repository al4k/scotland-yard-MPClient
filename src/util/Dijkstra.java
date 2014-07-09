package util;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.*;

public class Dijkstra {
	private Graph graph;
	
	/**
	 * Constructor
	 * @param graph
	 */
	public Dijkstra(Graph graph) 
	{
		this.graph = graph;
	}
	
	/**
	 * Function that computes the output
	 */
	
	public int[][] computeDistanceMatrix()
	{
		int nodeNum = graph.nodeNumber();
		int [][] distanceMatrix = new int[nodeNum][nodeNum];
		
		
		for(int i = 0; i < nodeNum; i++)
		{
			
			Map<String, Integer> distances = doDijkstra(i);
			
			for(String key : distances.keySet())
			{
				//System.out.println(graph.find(i).name() + " " + graph.find(key).name() + " " + distances.get(key));
				distanceMatrix[i][Integer.parseInt(key)-1] = distances.get(key);
			}

			
		}
		
		
		return distanceMatrix;
	}
	
	private Map<String, Integer> doDijkstra(int startNode)
	{
		
		
		Map<String, Integer> nodeDistances = new HashMap<String, Integer>();
		Set<String> unvisitedNodes = new HashSet<String>();
		Map<String, Edge> previousNodes = new HashMap<String, Edge>();
		
		initialise(startNode, nodeDistances, unvisitedNodes, previousNodes);
		
		
		Node currentNode = this.graph.find(startNode);
		
		
		while(unvisitedNodes.size() > 0)
		{
			
			String nodeName = currentNode.name();
					
			// get the current distance
			int currentDistance = nodeDistances.get(nodeName);
			
			// get the connecting edges of the current node
			List<Edge> edges = graph.edges(nodeName);
			
			
			// loop through the neighbours
			for(Edge e : edges)
			{
				String connectingName = e.connectedTo(nodeName);
				
				
				
				// check to see if the node is visited
				if(!unvisitedNodes.contains(connectingName)) continue;
				
				
				// update the distance of the connecting node
				int newDistance = currentDistance;
				newDistance += 1;
				
				
				
				// check it against the current distance value
				if(nodeDistances.get(connectingName) > newDistance)
				{
					// set the previous connection
					previousNodes.put(connectingName, e);
					nodeDistances.put(connectingName, newDistance);
				}		
			}
			
			// remove the current node from the set of unvisited nodes
			unvisitedNodes.remove(nodeName);
			
			
			// get the node with the smallest distance that hasn't been visited
			int minDist = Integer.MAX_VALUE;
			String minNodeName = "";
			for(String key : unvisitedNodes)
			{
				if(nodeDistances.get(key) <= minDist)
				{
					minDist = nodeDistances.get(key);
					minNodeName = key;
				}
			}
			
			// update the current node
			currentNode = graph.find(minNodeName);		
		}
		
		return nodeDistances;
	}
	
	/**
	 * Function to initialise the algorithm
	 */
	private void initialise(int start, Map<String, Integer> nodeDistances, 
			Set<String> unvisitedNodes, Map<String, Edge> previousNodes)
	{
		String source = graph.find(start).name();

		List<Node> nodes = graph.nodes();
		for(Node n : nodes)
		{
			if(n.name().equals(source))
			{
				nodeDistances.put(n.name(), 0);
			}
			else
			{
				nodeDistances.put(n.name(), Integer.MAX_VALUE);
				unvisitedNodes.add(n.name());
			}	
		}
		
		previousNodes = new HashMap<String, Edge>();		
	}
	
	
}
