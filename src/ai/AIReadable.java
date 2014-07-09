package ai;
import state.Visualisable;
import graph.*;
import state.Player;

public interface AIReadable extends Visualisable {
	
	/**
	 *  This interface allows the AI to get a copy of the graph object. The AI
	 *  needs this object as it would be unable to make informed descision without it
	 * @return The graph object being used by the game
	 */
	public Graph getGraph(); 

	public Player getPlayer(int id);
	
	public void setCurrentPlayer(int playerId);
	
	
}
