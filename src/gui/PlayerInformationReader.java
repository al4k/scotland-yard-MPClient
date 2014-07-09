package gui;

import gui.PlayerDisplayInfo.PlayerType;

import java.awt.Color;
import java.util.List;

import state.Initialisable;
import state.PlayerVisualisable;
import state.Point;
import state.Visualisable;
import state.Initialisable.TicketType;

public class PlayerInformationReader {
	private PlayerVisualisable playerVisualisable;
	private Visualisable visualisable;
	

	boolean isVisualisable = false;
	boolean isPlayerVisualisable = false;

	public PlayerInformationReader(PlayerVisualisable pv, Visualisable v)
	{
		if(pv != null) 
		{
			playerVisualisable = pv;
			isPlayerVisualisable = true;
		}

		if(v != null)
		{
			visualisable = v;
			isVisualisable = true;
		}
	}
	
	private boolean isMrX(int id)
	{
		for(int val : playerVisualisable.getMrXIdList())
		{
			if(val == id) return true;
		}
		return false;
	}

	private void isPlayerStillInGame(PlayerDisplayInfo player)
	{
		if(player.getType() == PlayerType.MrX) 
		{
			player.setActive(true);
			return;
		}
		
		List<Integer> detectiveIds = playerVisualisable.getDetectiveIdList();
		for(int id : detectiveIds)
		{
			if(player.getId() == id) 
			{
				player.setActive(true);
				return;
			}
		}
		
		player.setActive(false);
	}
	
	
	private void setPlayerVisualisableInfo(PlayerDisplayInfo player, boolean drawMrX)
	{
		// get the playerVisualisable Information
		int id = player.getId();
		int location = 0;
		
		
		if(player.getType() == PlayerType.Detective || 
				drawMrX)
		{
			location = playerVisualisable.getActualPosition(id);
		}
		else
		{
			
			location = playerVisualisable.getNodeId(id);
			//System.out.println("Getting Hidden " + location);
		}
		
		player.setLocation(location);
		player.getPlayerLocationHistory().add(location);
		
		
		//player.setLocation(location);
		//player.getPlayerLocationHistory().add(location);
		
		/*
		//if(player.getType() == PlayerType.Detective || 
		//		drawMrX || visualisable.isVisible(id))
		{
			player.setLocation(location);
		}
		else
		{
			player.setLocation(playerVisualisable.getNodeId(id));
		}
		*/
	
		
		if(isMrX(id)) player.setType(PlayerType.MrX);
		else player.setType(PlayerType.Detective);
		
		
		int currentPlayer = visualisable.getNextPlayerToMove();
		if(currentPlayer == id) player.setCurrentTurn(true);
		else player.setCurrentTurn(false);
		
		
		Point playerPosition = new Point(
				playerVisualisable.getLocationX(player.getLocation()), 
				playerVisualisable.getLocationY(player.getLocation()));

		//if(player.getType() == PlayerType.Detective || 
		//		drawMrX || visualisable.isVisible(id))
		//{
			player.setPlayerPosition(playerPosition);
		//}
		
		isPlayerStillInGame(player);
		
		
	}
	
	private void setPlayerColours(PlayerDisplayInfo player)
	{
		player.setPlayerTextColour(PlayerDisplayInfo.LIGHT_TEXT);
		
		// check for mr x
		if(isMrX(player.getId()))
		{
			player.setPlayerColour(PlayerDisplayInfo.MR_X_COLOUR);	
			player.setPreviousColour(new Color(150,150,150));
			player.setType(PlayerType.MrX);
		}
		else
		{
			int colourIndex = 0;
			List<Integer> detectiveIds = playerVisualisable.getDetectiveIdList();
			for(int i = 0; i < detectiveIds.size(); i++)
			{
				if(player.getId() == detectiveIds.get(i)) colourIndex = i;
			}
			
			
			
			player.setPlayerColour(PlayerDisplayInfo.PLAYER_COLOURS[colourIndex]);
			if(colourIndex == 0) player.setPreviousColour(new Color(150,150,150));
			else player.setPreviousColour(PlayerDisplayInfo.PLAYER_COLOURS[colourIndex-1]);
		}
	}
	
	private void setVisualisableInfo(PlayerDisplayInfo player)
	{
		int id = player.getId();
		int taxi = visualisable.getNumberOfTickets(TicketType.Taxi, id);
		int bus = visualisable.getNumberOfTickets(TicketType.Bus, id);
		int underground = visualisable.getNumberOfTickets(TicketType.Underground, id);
		int secret = visualisable.getNumberOfTickets(TicketType.SecretMove, id);
		int doubleMove = visualisable.getNumberOfTickets(TicketType.DoubleMove, id);


		// assign the tickets
		player.setTickets(Initialisable.TicketType.Taxi, taxi);
		player.setTickets(Initialisable.TicketType.Bus, bus);
		player.setTickets(Initialisable.TicketType.Underground, underground);
		player.setTickets(Initialisable.TicketType.SecretMove, secret);
		player.setTickets(Initialisable.TicketType.DoubleMove, doubleMove);

		
		player.setTicketHistory(visualisable.getMoveList(id));
		player.getPlayerVisualisableHistory().add(visualisable.isVisible(id));

	
	}

	public PlayerDisplayInfo createPlayerDisplayInfo(int playerId, boolean drawMrX)
	{
		PlayerDisplayInfo playerDisplay = new PlayerDisplayInfo();
		playerDisplay.setId(playerId);

		// get the relevant information from the visualisable interface
		if(!isPlayerVisualisable) return playerDisplay;
		
		setPlayerVisualisableInfo(playerDisplay, drawMrX);
		setPlayerColours(playerDisplay);


		// check if we have the visualisable interface
		if(!isVisualisable) return playerDisplay;
		
		setVisualisableInfo(playerDisplay);

		
		return playerDisplay;
	}

	public void updatePlayerDisplayInfo(PlayerDisplayInfo player, boolean drawMrX)
	{
		if(isPlayerVisualisable) setPlayerVisualisableInfo(player, drawMrX);
		if(isVisualisable) setVisualisableInfo(player);
	}

}
