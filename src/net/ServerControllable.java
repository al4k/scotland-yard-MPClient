package net;

import state.Controllable;
import java.util.List;

public interface ServerControllable extends Controllable {
	public Boolean initialiseServerGame(Integer numberOfDetectives, String sessionName);
	public List<Integer> joinGame();
	public int getServerNextPlayer();
	public Boolean isGameOver();
}
