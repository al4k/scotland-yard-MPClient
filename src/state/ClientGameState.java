package state;

import ai.AIReadable;
import game.TextOutput;
import graph.Graph;
import gui.MrXPanel;

public class ClientGameState extends GameState implements AIReadable, ClientControllable {
	
		
	private boolean mrXClient = false;
	
	public Graph getGraph() 
	{
		return map.getGraph();
	}
	
	public void setCurrentPlayer(int playerId)
	{
		gameLogic.setCurrentPlayer(playerId);
	}
	
	public void setMrXClient(boolean val)
	{
		mrXClient = val;
	}

	
	public Integer getNodeId(Integer playerId) 
	{
		if(!gameData.hasPlayer(playerId))
		{
			TextOutput.printError(String.format("Player Id %d does not exist. Returning -1\n", playerId));
			Thread.dumpStack();
			return -1;
		}

		if(mrXClient)
		{
			return gameData.getPlayer(playerId).getActualLocation();
		}
		else
		{
			// if the id is a detective id
			if(!gameData.isMrX(playerId))
				return gameData.getPlayer(playerId).getActualLocation();
			
			// get the hidden location
			return gameData.getPlayer(playerId).getCurrentLocation();
		}
	}

	public Boolean initialiseGame(String mapFilename, String graphFilename,
			String positionsFilename, String gameFilename) {
		
		// initialise the Map object from the map file
		this.initialiseMap(mapFilename, positionsFilename, graphFilename);
		
		// call the gamestate's load game function. This will
		// create a new set of game data that matches the game data in the database
		if(!this.loadGame(gameFilename))
		{
			TextOutput.printError("Couldn't load the Game Data\n");
			System.exit(1);
		}
		
		// create a new game logic object based on the map and game data
		gameLogic = GameLogic.newGame(map, gameData);
		
		return true;
	}
}
