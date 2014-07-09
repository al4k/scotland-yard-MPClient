package gui;

import state.Point;
import java.awt.Color;

public interface PlayerLocationReadable {
	public Point getPlayerMapLocation();
	public Color getPlayerColour();
	public Color getMarkerColour();
	public boolean isActive();
}
