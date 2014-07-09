package net;

import state.ClientControllable;
import state.Controllable;


public abstract class ClientBase implements Controllable, RemoteControllable {
	
	private ClientControllable clientControllable;
	
	public void registerClientControllable(ClientControllable con)
	{
		clientControllable = con;
	}
	
	
	// pass the move player through the RemoteControllable Interface
	public Boolean movePlayer(Integer playerId, Integer targetNodeId, TicketType ticketType)
	{
		boolean success = this.makeServerMove(playerId, targetNodeId, ticketType);
		if(success && clientControllable != null)
		{
			if(!clientControllable.movePlayer(playerId, targetNodeId, ticketType))
			{
				System.out.println("Why?");
			}
		}
		return success;
			
	}
	
	// pass the call into the client controllable
	public Boolean initialiseGame(Integer numDetectives)
	{
		if(clientControllable != null)
			return clientControllable.initialiseGame(numDetectives);
		return false;
	}

	// pass the call into the client controllable
	public Integer getNodeIdFromLocation(Integer xPosition, Integer yPosition)
	{
		if(clientControllable != null)
			return clientControllable.getNodeIdFromLocation(xPosition, yPosition);
		return 0;
	}
	
	// pass the call into the client controllable
	public Boolean saveGame(String filename)
	{
		if(clientControllable != null)
			return clientControllable.saveGame(filename);
		return false;
	}
	

	// pass the call into the client controllable
	public Boolean loadGame(String filename)
	{
		if(clientControllable != null)
			return clientControllable.loadGame(filename);
		return false;
	}
	
}
