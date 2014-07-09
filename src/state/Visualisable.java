package state;
import java.util.*;


/**
 * Main interface that enables the visualisation of the game state
 */
public interface Visualisable extends Initialisable, MapVisualisable, PlayerVisualisable {


	/**
	 * Function to retrieve the number of tickets of a certain type held by a player 
	 * @param type The type of ticket you are checking for
	 * @param playerId The id of the player you are looking up
	 * @return The number of tickets of that type that the player has
	 */
	public Integer getNumberOfTickets(TicketType type, Integer playerId);
	
	
	/**
	 * Function to retrieve the list of previous move types that a player has taken
	 * @param playerId The id of player you are looking up
	 * @return The list of moves that a player has made
	 */
	public List<TicketType> getMoveList(Integer playerId);
	
	 
	/**
	 * Check if a player is visible. The detectives are going to be visible at all times, Mr X is not..
	 * @param playerId The id of the player you are checking
	 * @return If that player is visible
	 */
	public Boolean isVisible(Integer playerId);
			
	/**
	 * Function to check if the game is over
	 * @return True if the game is over, false if not
	 */
	public Boolean isGameOver();
		
	/**
	 * Function to find our who is the next player to move. i.e. whos go it is
	 * @return The id of the player whos go it is
	 */
	public Integer getNextPlayerToMove();
	
	/**
	 * Function to get the id of the winning player (if the game has been won)
	 * @return The id of the winning player
	 */
	public Integer getWinningPlayerId();
	
	

}
