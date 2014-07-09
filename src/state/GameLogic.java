package state;

import game.TextOutput;

import java.util.List;

import state.Initialisable.TicketType;

/** Class that implements the game logic
*/
public class GameLogic {
	private boolean gameOver;
	private int winningPlayer;
	private Map map;
	private GameData data;
	private boolean doubleMoveMode;
	private int doubleMoveCount;
	
	private static final int TOTAL_MOVES_PER_GAME = 25;

	

	
	private GameLogic()
	{
		
	}
	
	
	public static GameLogic newGame(Map map, GameData data)
	{
		GameLogic logic = new GameLogic();
		logic.map = map;
		logic.data = data;
		
		
		
		logic.setupNewGame();

		
		return logic;
	}
	
	
	private void setupNewGame()
	{
		gameOver      = false;
		winningPlayer = -1;
		doubleMoveMode = false;
	}
	
	
	private boolean validDoubleMove(int playerId)
	{
		if(!data.isMrX(playerId)) return false;
		if(!data.mrX().hasTickets(Initialisable.TicketType.DoubleMove)) return false;
		return true;
		
	}
	
	public boolean inDoubleMove()
	{
		return doubleMoveMode;
	}
	
	public boolean setDoubleMoveMode(int playerId)
	{
		if(!validDoubleMove(playerId)) return false;
		doubleMoveMode = true;
		doubleMoveCount = 0;
		
		// remove the double move ticket
		data.getPlayer(playerId).useTicket(Initialisable.TicketType.DoubleMove);
		
		//TextOutput.printDebug("Going Into Double Move Mode\n");
		return true;
	}
	
	public boolean isDoubleMoveOver()
	{
		if(doubleMoveCount == 2) return true;
		return false;
	}
	

	// this is the main function for the class. The first thing that needs to be done
	// is to check if the player is in fact out of the game 
	public boolean movePlayer(int playerId, int target, 
			Initialisable.TicketType ticket)
	{
		Player player = data.getPlayer(playerId);
		
		if(playerIsOutOfGame(player))
		{
			TextOutput.printDebug(String.format("Removing Player: %d\n", playerId));
			data.removePlayer(playerId);
			return false;
		}
		
		if(playerIsUnableToMove(player))
		{
			nextTurn();
			return false;
		}
		
		// check if the move is possible
		if(!moveIsPossible(player, target, ticket)) return false;
		
		
		// move is valid so take it
		player.moveTo(target, ticket);
		player.useTicket(ticket);
		if(ticket != TicketType.SecretMove)
			data.mrX().addTicket(ticket);
		
		
		if(doubleMoveMode)
		{
			doubleMoveCount++;
			//System.out.println("Double Move Count: " + doubleMoveCount);
			if(isDoubleMoveOver()) 
			{
				doubleMoveMode = false;	
				nextTurn();
			}
			return true;
		}
		
		nextTurn();
		return true;
	}
	
	
	public boolean moveIsPossible(Player player, Integer targetNodeId,
			TicketType ticketType)
	{
		// get the player
		if(player == null) 
		{
			TextOutput.printDebug("Null Player\n");
			return false;
		}

		// check the player has enough tickets
		if(player.getTicketNumber(ticketType) <= 0)
		{
			TextOutput.printDebug("Not Enough Tickets\n");
			return false;
		}

		// check that a move is possible given the current location
		int currentLocation = player.getActualLocation();
		if(!map.isMovePossible(currentLocation, targetNodeId, ticketType))
		{
			TextOutput.printDebug("Location is not movable to\n");
			return false;
		}

		// check if the target is occupied
		if(isLocationOccupied(targetNodeId))
		{
			TextOutput.printDebug("Location is Occupied\n");
			return false;
		}

		return true;
	}
	
	public void setCurrentPlayer(int playerId)
	{
		this.data.setCurrentPlayer(playerId);
	}
	
	public boolean isGameOver()
	{
		return gameOver;
	}
	
	public int currentPlayer()
	{
		return data.currentPlayer();
	}
	
	public int winningPlayer()
	{
		if(!isGameOver()) return -1;
		return winningPlayer;
	}
	
	
	private void nextTurn()
	{
		data.nextTurn();
		evaluateWinningConditions();
	
	}
	
	
	public void evaluateWinningConditions()
	{
		// is mr x under another player?
		int xLocation = data.mrX().getActualLocation();
		for(int id : data.detectiveIds())
		{
			if(data.getPlayer(id).isAtLocation(xLocation))
			{
				TextOutput.printDebug(
						String.format("Mr X Has Been Caught at Location %d\n", xLocation));
				winningPlayer = id;
				gameOver = true;
				return;
			}
		}
		
		
		// check if mrx can make a move
		List<Map.Move> moves = map.getPossibleMoves(xLocation);
		boolean allOccupied = true;
		for(Map.Move move : moves)
		{
			if(!isLocationOccupied(move.location))
			{
				allOccupied = false;
				break;
			}
		}

		if(allOccupied)
		{
			TextOutput.printDebug(
					String.format("Mr X Cannot move anywhere!!!\n", xLocation));
			gameOver = true;
			winningPlayer = data.detectiveIds().get(0);
			return;
		}
		
		
		// check the possible winning conditions
		if(data.detectiveIds().size() == 0)
		{
			gameOver = true;
			TextOutput.printDebug("No More Detectives in the Game. Game Over!\n");
			winningPlayer = data.mrX().playerId();
			return;
		}
		
		
		// are all the mr x gos used up
		if(data.mrX().getNumberOfMoves() == TOTAL_MOVES_PER_GAME)
		{
			gameOver = true;
			TextOutput.printDebug("Detectives Have run Out of Turns. Game Over\n");
			winningPlayer = data.mrX().playerId();
			return;
		}
	}
	
	
	private boolean playerIsUnableToMove(Player player)
	{
		int location = player.getActualLocation();
		List<Map.Move> possibleMoves = map.getPossibleMoves(location);

		// check if any of the moves at that location are possible
		for(Map.Move move : possibleMoves)
		{
			if(player.getTicketNumber(move.type) > 0 && !isLocationOccupied(move.location))
			{
				return false;
			}
		}

		return true;
	}
	
	
	
	public boolean mrXIsAtTarget(int location)
	{
		Player mrX = data.mrX();
		if(mrX.getActualLocation() == location) return true;
		return false;
	}
	
	
	public boolean isLocationOccupied(int location)
	{
		//System.out.println(data.numberOfPlayers());
		for(int id: data.detectiveIds())
		{
			//System.out.println(data.getPlayer(id));
			if(data.getPlayer(id).isAtLocation(location))
			{
				//Thread.dumpStack();
				return true;
			}
		}

		return false;
	}
	
	

	
	
	// function to determine if a player is out of the game. This is only the case 
	// when the player has no possible moves given their set of tickets
	private boolean playerIsOutOfGame(Player player)
	{
		int location = player.getActualLocation();
		List<Map.Move> possibleMoves = map.getPossibleMoves(location);

		// check if any of the moves at that location are possible
		for(Map.Move move : possibleMoves)
		{
			if(player.getTicketNumber(move.type) > 0)
			{
				return false;
			}
		}

		return true;
	}

	
}
