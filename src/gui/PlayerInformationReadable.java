package gui;

import state.Initialisable;
import java.util.List;
import java.awt.Color;

public interface PlayerInformationReadable extends PlayerLocationReadable {
	public int getId();
	public Color getPreviousColour();
	public boolean isCurrentTurn();
	public Color getPlayerTextColour();
	public int getTicketNumber(Initialisable.TicketType type);
	public int getLocation();
	public List<Boolean> getVisualisableHistory();
	public List<Initialisable.TicketType> getTicketHistory();


}
