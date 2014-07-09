package ai;

import java.util.*;

import util.Dijkstra;
import ai.Board.PlayerInfo;
import game.TextOutput;
import graph.*;

public class FreedomDistanceBoardScorer extends BoardScorerBase {

	private int[][] distanceMatrix;
	boolean distanceComputed = false;
	double distanceWeighting = 20.0;
	
	@Override
	public void score(Board board) 
	{
		HashMap<Integer, PlayerInfo> players = board.playerInfo();
		Graph graph = board.graph();
		int mrXId = board.mrXId();
		
		if(!distanceComputed)
		{
			Dijkstra dijkstra = new Dijkstra(graph);
			distanceMatrix = dijkstra.computeDistanceMatrix();
			distanceComputed = true;
			
		}
		
		
		// get freedom value and distance
		int minDistance = getClosestDetectiveDistance(players, mrXId);
	
		
		
		int freedom = getFreedomScore(graph, players, mrXId);		
		double score = ((double) minDistance * distanceWeighting) + (double) freedom;
		
		
		// check for the game over condition
		if(board.isDetectiveWin())
			score = Integer.MIN_VALUE;
		
		
		if(board.isMrXWin())
			score = Integer.MAX_VALUE;
		
		
		
		
		board.setScore(score);	
	}
	
	
	
	
	
	public void setDistanceWeighting(double weight)
	{
		this.distanceWeighting = weight;
	}
	
	
	public int getFreedomScore(Graph graph, HashMap<Integer,PlayerInfo> players, int mrXId)
	{
		// get number of connecting edges
		int mrXLocation = players.get(mrXId).position;
		String strLocation = Integer.toString(mrXLocation);
		List<Edge> edges = graph.edges(graph.find(strLocation).name());
		
		int possibleMoves = 0;
		for(Edge e : edges)
		{
			String connection = e.connectedTo(strLocation);
			
			// check if it is occupied
			boolean occupied = false;
			for(int key : players.keySet())
			{
				if(key != mrXId)
				{
					int detectiveLocation = players.get(key).position;
					String strDetectivePosition = Integer.toString(detectiveLocation);
					if(strDetectivePosition.equals(connection))
						occupied = true;
				}
				
			}
			
			if(!occupied)
				possibleMoves++;
			
		}
		
		return possibleMoves;
	}
	
	public int getClosestDetectiveDistance(HashMap<Integer,PlayerInfo> players, int mrXId)
	{
		int mrXlocation = players.get(mrXId).position;
		
		int minDistance = Integer.MAX_VALUE;
		for(int key : players.keySet())
		{
			if(key != mrXId)
			{
				int detectiveLocation = players.get(key).position;
				int distance = distanceMatrix[mrXlocation-1][detectiveLocation-1];
				
				//System.out.println("Distance: " + key + " " + detectiveLocation + " " + distance);
				
				if(distance < minDistance)
				{
					//System.out.println("Distance: " + distance);
					minDistance = distance;
				}
			}
		}
		
		return minDistance;
	}
}
