package state;
import java.util.*;

/**
 * Interface that enables information about displaying player information to
 * be extracted
 */
public interface PlayerVisualisable  {


	
	/**
	 * Function to get the x position of a node given its id
	 * @param nodeId The node id you wish to find the location of
	 * @return An integer value of a nodes x position 
	 */
	public Integer getLocationX(Integer nodeId);
	
	/**
	 * Function to get the y position of a node given its id
	 * @param nodeId The node id you wish to find the location of
	 * @return An integer value of a nodes y position 
	 */
	public Integer getLocationY(Integer nodeId);	
	
	/**
	 * Function to return a list of detective IDS from the game. These IDs will
	 * be unique identifiers given to the player
	 * @return The list of detective IDs
	 */
	public List<Integer> getDetectiveIdList();
	
	/**
	 * Function to get the list of Mr X IDs. In this version of the game you will really only
	 * have one Mr X so this function will return a list with only one entry. However this
	 * may change down the line...
	 * @return The list of Mr X IDs
	 */
	public List<Integer> getMrXIdList();
	
	/**
	 * Function to retireve the current location of a player. The location is an id of a graph node
	 * @param playerId The id of the player
	 * @return The node id that the player is on
	 */
	public Integer getNodeId(Integer playerId);
	
		
	public Integer getActualPosition(Integer playerId);
	
}
