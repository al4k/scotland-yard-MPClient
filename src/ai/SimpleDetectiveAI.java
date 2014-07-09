package ai;

import java.util.HashMap;

import game.TextOutput;
import state.Initialisable;
import util.Dijkstra;
import graph.*;

import java.util.*;

import ai.Board.PlayerInfo;

public class SimpleDetectiveAI extends AI {

	int [][] distanceMatrix;
	boolean distanceComputed = false;

	@Override
	public Move getMove(int playerId) {


		Graph graph = aiReadable.getGraph();

		if(!distanceComputed)
		{
			Dijkstra dijkstra = new Dijkstra(graph);
			distanceMatrix = dijkstra.computeDistanceMatrix();
			distanceComputed = true;
			TextOutput.printDebug("Distance Matrix Computed\n");
		}


		// set the players
		HashMap<Integer, Board.PlayerInfo> players = new HashMap<Integer, Board.PlayerInfo>();

		for(int id : aiReadable.getDetectiveIdList())
		{
			players.put(id, getPlayerInfo(id));
		}

		//System.out.println(playerId);
		for(int id : aiReadable.getMrXIdList())
		{
			players.put(id, getPlayerInfo(id));
		}

		Board.PlayerInfo mrXInfo = players.get(aiReadable.getMrXIdList().get(0));
		
		

		// get the set of possible moves for the detective
		int currentLocation = players.get(playerId).position;
		String strPos = Integer.toString(currentLocation);
		
		
		Move bestMove = null;
		int minDistanceToX = Integer.MAX_VALUE;
		
		List<Edge> edges = graph.edges(strPos);
		for(Edge e : edges)
		{
			
			// check the move is possible
			if(!isMovePossible(players, players.get(playerId), e))
				continue;
			
			
			String otherNode = e.connectedTo(strPos);
			int otherPos = Integer.parseInt(otherNode);
			
			
			
			int distance = distanceMatrix[mrXInfo.position-1][otherPos-1];
			
			if(distance < minDistanceToX)
			{
				bestMove = new Move(convertEdgeTypeToTicketType(e.type()), otherPos);
				//System.out.println(distance);
				minDistanceToX = distance;	
			}
			
			
			
		}
		
		
		
		if(bestMove == null)
		{
			bestMove = new Move(Initialisable.TicketType.Bus, currentLocation);
		}
		

		return bestMove;
	}
	
	
	
	public boolean isMovePossible(HashMap<Integer, PlayerInfo> players, PlayerInfo player, Edge e)
	{
		if(!hasEnoughTickets(player, e))
			return false;
		
		if(locationIsOccupied(players, player, e))
		{
			//System.out.println("Location occupoed");
			return false;
		}
			
		
		return true;
	}
	
	
	
	public boolean locationIsOccupied(HashMap<Integer, PlayerInfo> players, 
			PlayerInfo player, Edge e)
	{
		int otherLocation = Integer.parseInt(
				e.connectedTo(Integer.toString(player.position)));
		
		for(int key : players.keySet())
		{
			PlayerInfo other = players.get(key);
			int location = other.position;
			
			// check for same player
			if(player.playerId == other.playerId) 
				continue;
			
			if(other.playerId == aiReadable.getMrXIdList().get(0))
				continue;	
				
			if(location == otherLocation)
				return true;
			
		}
		
		return false;
		
	}
	
	public boolean hasEnoughTickets(PlayerInfo player, Edge e)
	{
		Initialisable.TicketType type = convertEdgeTypeToTicketType(e.type());
		if(player.tickets.get(type) > 0)
			return true;
		else
			return false;		
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

	
	/**
	 * Function to get the current info of the player
	 * @param playerId
	 * @return
	 */
	private Board.PlayerInfo getPlayerInfo(int playerId)
	{
		Board.PlayerInfo info = new Board.PlayerInfo();
		info.playerId = playerId;
		
		info.position = aiReadable.getNodeId(playerId);
		info.tickets.put(Initialisable.TicketType.Bus, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.Bus, playerId));
		info.tickets.put(Initialisable.TicketType.Taxi, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.Taxi, playerId));
		info.tickets.put(Initialisable.TicketType.Underground, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.Underground, playerId));
		info.tickets.put(Initialisable.TicketType.SecretMove, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId));
		info.tickets.put(Initialisable.TicketType.DoubleMove, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.DoubleMove, playerId));
		
		//System.out.println(info.position);
		
		
		return info;
	}
	
	
}
