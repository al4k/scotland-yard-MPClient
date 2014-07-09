package state;

import game.TextOutput;

import java.io.*;
import java.util.*;

import javax.xml.soap.Text;



/**
 * Class that will hold the state of the game. This is the class that will need
 * to implement the interfaces that we have provided you with
 */
public class GameState implements MapVisualisable, PlayerVisualisable, Initialisable, Visualisable, Controllable {

	/**
	 * Vairable that will hold the filename for the map
	 */
	protected Map map;
	protected GameData gameData;
	private GameInitialiser gameInitailiser;
	boolean debug;
	boolean gameOver = false;
	boolean usingDoubleMove = false;
	boolean usingSecretMove = false;
	int doubleMoveTurnsTaken = 0;
	GameLogic gameLogic;
	

	protected Map getMap()
	{
		return map;
	}

	public GameState()
	{	
		gameInitailiser = new GameInitialiser();
	}
	
	//TODO delete
	public Player getPlayer(int id)
	{
		return gameData.getPlayer(id);
	}


	public Boolean initialiseGame(Integer numberOfDetectives) 
	{		
		initialiseMap();
		gameData = gameInitailiser.initialiseGame(numberOfDetectives);
		gameLogic = GameLogic.newGame(map, gameData);
		
		return true;
	}

	public Point getNodeLocation(int nodeId)
	{
		Point location = map.getNodeLocation(Integer.toString(nodeId));
		return location;
	}


	public Integer getLocationX(Integer nodeId) 
	{
		Point loc = getNodeLocation(nodeId);
		if(loc == null) 
		{
			return 0;
		}
		
		return loc.x();
	}

	public Integer getLocationY(Integer nodeId) 
	{
		Point loc = getNodeLocation(nodeId);
		if(loc == null) 
		{
			return 0;
		}
		return loc.y();
	}

	public List<Integer> getDetectiveIdList() 
	{
		return gameData.detectiveIds();
	}

	public List<Integer> getMrXIdList() 
	{
		return gameData.mrXIds();
	}

	public Integer getNodeId(Integer playerId) 
	{
		if(!gameData.hasPlayer(playerId))
		{
			TextOutput.printError(String.format("Player Id %d does not exist. Returning -1\n", playerId));
			Thread.dumpStack();
			return -1;
		}

		// if the id is a detective id
		if(!gameData.isMrX(playerId))
			return gameData.getPlayer(playerId).getActualLocation();
		
		
		// if the current player is mr x return his actual location
		if(gameData.isMrX(getNextPlayerToMove()))
			return gameData.getPlayer(playerId).getActualLocation();
		
		// get the hidden location
		return gameData.getPlayer(playerId).getCurrentLocation();
	}
	
	public Integer getActualPosition(Integer playerId)
	{
		return gameData.getPlayer(playerId).getActualLocation();
	}

	public Integer getNumberOfTickets(TicketType type, Integer playerId) 
	{
		return gameData.getPlayer(playerId).getTicketNumber(type);
	}


	@Override
	public List<TicketType> getMoveList(Integer playerId) {
		Player player = gameData.getPlayer(playerId);
		if(player==null) return null;
		return player.ticketHistory();
	}


	@Override
	public Boolean isVisible(Integer playerId) {
		Player player = gameData.getPlayer(playerId);
		if(player==null) return null;
		return player.isVisible();
	}


	@Override
	public Boolean isGameOver() {
		return gameLogic.isGameOver();
	}


	@Override
	public Integer getNextPlayerToMove() 
	{
		return gameLogic.currentPlayer();
	}


	@Override
	public Integer getWinningPlayerId() 
	{
		return gameLogic.winningPlayer();
	}







	public Boolean movePlayer(Integer playerId, Integer targetNodeId,
			TicketType ticketType) 
	{
		// check to see if we enter double move mode
		if(ticketType == Initialisable.TicketType.DoubleMove)
		{
			if(gameLogic.inDoubleMove())
			{
				return false;
			}
			boolean validDoubleMove = gameLogic.setDoubleMoveMode(playerId);
			return validDoubleMove;
		}


		boolean moveOk = gameLogic.movePlayer(playerId, targetNodeId, ticketType);
		gameLogic.evaluateWinningConditions();


	

		return moveOk;
		
	}


	@Override
	public Integer getNodeIdFromLocation(Integer xPosition, Integer yPosition) 
	{
		
		Point location = new Point(xPosition, yPosition);
		return map.getNearestNodeToLocation(location);
	}


	@Override
	public Boolean saveGame(String filename) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean loadGame(String filename) 
	{
		
		try 
		{
			this.gameData = GameData.loadFromFile(filename);
		} 
		catch (IOException e) 
		{
			TextOutput.printError("Couldn't initialise the game by loading\n");
			return false;
		}
		
		if(gameData == null) return false;
		
		
		gameLogic = GameLogic.newGame(map, gameData);
		
		return true;
	}


	public void initialiseMap()
	{
		map = new Map();
		try
		{
			map.loadAssests();
		}
		catch(IOException e)
		{
			TextOutput.printError(e.getMessage());
			System.exit(1);
		}
	}
	
	protected void initialiseMap(String mapFilename, String posFilename, String graphFilename)
	{
		map = new Map(mapFilename, posFilename, graphFilename);
		try
		{
			map.loadAssests();
		}
		catch(IOException e)
		{
			TextOutput.printError(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Function to retrieve the map filename
	 */
	public String getMapFilename()
	{
		return map.getMapFilename();	
	}
}
