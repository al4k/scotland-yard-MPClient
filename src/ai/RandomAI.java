package ai;

import state.*;

import java.util.*;


import game.TextOutput;
import graph.*;

public class RandomAI extends AI {
	
	
	
	@Override
	public  Move getMove(int playerId)
	{
		
		
		
		int currentLocation = aiReadable.getNodeId(playerId);
		TextOutput.printDebug(String.format("In AI: player id %d location %d\n", playerId, currentLocation));
		List<Move> possibleMoves = getPossibleMoves(currentLocation, playerId);
		
		int numberOfMoves = possibleMoves.size();
		
				
		// get a random move
		int choice = (int) (Math.random() * numberOfMoves);
		Move chosenMove = possibleMoves.get(choice);
	
		
		
		
		return chosenMove;

		
	}
	
	private List<Move> getPossibleMoves(int currentLocation, int playerId)
	{
		
		List<Move> possibleMoves = new ArrayList<Move>();
		String locationName = Integer.toString(currentLocation);
		List<Edge> edges = aiReadable.getGraph().edges(locationName);
		
		for(Edge e: edges)
		{
			int connectingNode = Integer.parseInt(e.connectedTo(locationName));
			Move move = new Move(convertEdgeTypeToTicketType(e.type()), connectingNode);
			possibleMoves.add(move);
			
			if(aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId) > 0)
			{
				Move anotherMove = new Move(Initialisable.TicketType.SecretMove, connectingNode);
				possibleMoves.add(anotherMove);
			}
		
		}
		
	
		// if player has double or secret moves...
		if(aiReadable.getNumberOfTickets(Initialisable.TicketType.DoubleMove, playerId) > 0)
		{
			possibleMoves.add(new Move(Initialisable.TicketType.DoubleMove, currentLocation));
		}
		
		
		
		return possibleMoves;
	}
	
	
}
