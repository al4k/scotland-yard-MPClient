package state;

import game.TextOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


import graph.*;



public class Map {
	
	private static  String MAP_FILENAME   = "resources/images/map.jpg";
	private static  String POS_FILENAME   = "resources/pos.txt";
	private static String GRAPH_FILENAME = "resources/graph.txt";
	
	private Graph graph;
	private HashMap<String, Point> nodePositionMap = new HashMap<String, Point>();
	
	public Map(String mapFilename, String posFilename, String graphFilename)
	{
		MAP_FILENAME = mapFilename;
		POS_FILENAME = posFilename;
		GRAPH_FILENAME = graphFilename;
	}
	
	public Map()
	{
		
	}
	
	
	public class Move
	{
		public Move(Initialisable.TicketType type, int location)
		{
			this.type = type;
			this.location = location;
		}
		
		public Initialisable.TicketType type;
		public int location;
	}
	
	
	public List<Move> getPossibleMoves(int location)
	{
		List<Move> possibleMoves = new ArrayList<Move>();
		String locationName = Integer.toString(location);
		List<Edge> edges = graph.edges(locationName);
		
		for(Edge e: edges)
		{
			int connectingNode = Integer.parseInt(e.connectedTo(locationName));
			Move move = new Move(convertEdgeTypeToTicketType(e.type()), connectingNode);
			possibleMoves.add(move);
		}
		
		return possibleMoves;
	}
	
	public void loadAssests() throws IOException
	{
		loadPosFile();
		loadGraph();
		TextOutput.printDebug("Game Map Assets Loaded\n");
	}
	
	public void loadPosFile() throws IOException
	{
		if(POS_FILENAME.isEmpty())
		{
			TextOutput.printError("Node Position Filename is empty\n");
			System.exit(1);
		}
		
		NodePositionLoader positionLoader = new NodePositionLoader();
		positionLoader.read(POS_FILENAME);
		nodePositionMap = positionLoader.getNodePositionMap();	
	}
	
	
	public boolean areNodesConnected(int nodeId1, int nodeId2)
	{
		String nodeLocationName1 = Integer.toString(nodeId1);
		String nodeLocationName2 = Integer.toString(nodeId2);
		
		List<Edge> node1Edges = graph.edges(nodeLocationName1);
		
		for(Edge e : node1Edges)
		{
			if(e.connectedTo(nodeLocationName1).equals(nodeLocationName2)) return true;
		}
		return false;
	}
	
	
	public List<Initialisable.TicketType> getNodeConnectionTypesList(int location, int target)
	{
		List<Initialisable.TicketType> outputs = new ArrayList<Initialisable.TicketType>();
		
		String currentLocationName = Integer.toString(location);
		String targetLocationName   = Integer.toString(target);

		List<Edge> connectingEdges = graph.edges(Integer.toString(location));
		
		// collect all the types of connecting edges 
		for(Edge e: connectingEdges)
		{
			if(e.connectedTo(currentLocationName).equals(targetLocationName))
			{
				outputs.add(convertEdgeTypeToTicketType(e.type()));
			}
		}	

		return outputs;	
	}
	
	public Initialisable.TicketType convertEdgeTypeToTicketType(Edge.EdgeType edgeType)
	{
		Initialisable.TicketType ticketType = null;

		switch(edgeType)
		{
		case Underground: 
			ticketType = Initialisable.TicketType.Underground;
			break;
		case Taxi: 
			ticketType = Initialisable.TicketType.Taxi;
			break;
		case Bus: 
			ticketType = Initialisable.TicketType.Bus;
			break;
		default:
			break;
		}
		
		return ticketType;
	}
	
	public boolean isMovePossible(int location, int target, Initialisable.TicketType type)
	{
		String outputStump = String.format("Move %d -> %d is not possible: " , location, target);
		if(!areNodesConnected(location, target))
		{
			TextOutput.printDebug(outputStump + "nodes are not connected\n");
			return false;
		}
		
		// secret move is any connection
		if(type == Initialisable.TicketType.SecretMove) return true;
		
		List<Initialisable.TicketType> connections = getNodeConnectionTypesList(location, target);	
		if(!connections.contains(type))
		{
			TextOutput.printDebug(outputStump + "no " + type + " connection between nodes\n");
			return false;
		}
		
		return true;
	}
	
	
	public int getNearestNodeToLocation(Point p)
	{
		
		String bestNodeName = "";
		double minDist = Double.MAX_VALUE;
		for(String key : nodePositionMap.keySet())
		{
	
			Point comp = nodePositionMap.get(key);
			double dist = p.distanceTo(comp);
			
			if(dist < minDist)
			{
				minDist = dist;
				bestNodeName = key;
			}
		}
		
		
		
		return Integer.parseInt(bestNodeName);
	}
	

	public void loadGraph() throws IOException
	{
		Reader graphReader = new Reader();
		graphReader.read(GRAPH_FILENAME);
		graph = graphReader.graph();
	}

	public String getMapFilename()
	{
		if(MAP_FILENAME.isEmpty())
		{
			TextOutput.printError("Filename Has Not been set\n");
			System.exit(1);
		}
		return MAP_FILENAME;
	}
	 
	 public Point getNodeLocation(String nodeName)
	 {
		 return nodePositionMap.get(nodeName);
	 }
	 
	 
	 public Graph getGraph()
	 {
		 return graph;
	 }
	

}
