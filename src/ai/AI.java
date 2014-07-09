package ai;

import graph.Edge;
import state.Initialisable;

public abstract class AI {
	AIReadable aiReadable;

	
	/**
	 * Small class to hold a move object. A move is simply
	 * a target location and a ticket type
	 */
	static public class Move
	{
		public Move(Initialisable.TicketType type, int location)
		{
			this.type = type;
			this.location = location;
		}
		
		public Move()
		{
		}
		
		public Initialisable.TicketType type;
		public int location;
		
		public String toString()
		{
			return String.format("[%s %d]", type, location);
		}
	}
	
	
	/**
	 * Helper function to convert from edge type to ticket type
	 * @param edgeType
	 * @return The ticket type corresponding to the edge type
	 */
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
	 * Registration of the AIReadable object
	 * @param a
	 */
	public void registerAiReadable(AIReadable a)
	{
		this.aiReadable = a;
	}
	
	/**
	 * Abstract method for getting the AI to suggest a move
	 * @param playerId
	 * @return The move propsed by the AI
	 */
	public abstract Move getMove(int playerId);
}
