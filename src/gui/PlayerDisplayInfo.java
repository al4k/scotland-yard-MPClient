package gui;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import state.*;
import state.Initialisable.TicketType;

import java.awt.Color;


public class PlayerDisplayInfo implements PlayerInformationReadable {
	
	public static final Color[] PLAYER_COLOURS = {
		new Color(94, 178, 18), 
		new Color(25, 67, 255),
		new Color(255, 212,  0),
		new Color(204,  64,  20),
		new Color(178,  0, 161)
	};

	
	public static final Color LIGHT_TEXT = new Color(230,230,230);
	public static final Color DARK_TEXT  = new Color(30, 30, 30);
	
	public static final Color[] TEXT_COLOURS = {
		LIGHT_TEXT, 
		LIGHT_TEXT,
		LIGHT_TEXT,
		LIGHT_TEXT,
		LIGHT_TEXT
	};
	
	
	
	
	
	@Override
	public String toString() {
		return "PlayerDisplayInfo [playerLocationHistory="
				+ playerLocationHistory + ", playerTicketHistory="
				+ playerTicketHistory + ", playerVisualisableHistory="
				+ playerVisualisableHistory + ", id=" + id + ", location="
				+ location + ", tickets=" + tickets + ", playerColour="
				+ playerColour + ", previousColour=" + previousColour
				+ ", playerTextColour=" + playerTextColour
				+ ", playerPosition=" + playerPosition + ", type=" + type
				+ ", currentTurn=" + currentTurn + "]";
	}


	public static final Color MR_X_COLOUR = new Color(50, 50, 50);
	
	public enum PlayerType { Detective, MrX };
	
	private List<Integer> playerLocationHistory                = new ArrayList<Integer>();
	private List<Initialisable.TicketType> playerTicketHistory = new ArrayList<Initialisable.TicketType>();
	private List<Boolean> playerVisualisableHistory            = new ArrayList<Boolean>();
	
	private int id;
	private int location;
	private HashMap<Initialisable.TicketType, Integer> tickets = new HashMap<Initialisable.TicketType, Integer>();
	private Color playerColour;
	private Color previousColour;
	private Color playerTextColour;
	private Point playerPosition;
	private PlayerType type;
	private boolean currentTurn = false;
	private boolean isActive = true;
	
	
	
	
	
	public PlayerDisplayInfo()
	{
		
	}
	
	
	public void setTickets(Initialisable.TicketType type, int amount)
	{
		tickets.put(type, amount);
	}
	
	public int getTickets(Initialisable.TicketType type)
	{
		return tickets.get(type);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the location
	 */
	public int getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(int location) {
		this.location = location;
	}

	public Color getMarkerColour()
	{
		if(type == PlayerType.MrX)
		{
			int numMoves = playerTicketHistory.size();
			int count = numMoves;
			int diff = 0;
			while(count >= 0)
			{
				if(Player.mrXAppearanceList[count] == true)
				{
					diff = numMoves - count;
					break;
				}
				count--;
			}
			if(diff == 0)
			{
				return playerColour;
			}
			else
				return Color.GRAY;
		}
		else
		{
			return playerColour;
		}
	}
	
	/**
	 * @return the playerColour
	 */
	public Color getPlayerColour() {
		return playerColour;
	}

	

	/**
	 * @return the previousColour
	 */
	public Color getPreviousColour() {
		return previousColour;
	}

	/**
	 * @param previousColour the previousColour to set
	 */
	public void setPreviousColour(Color previousColour) {
		this.previousColour = previousColour;
	}

	/**
	 * @return the playerTextColour
	 */
	public Color getPlayerTextColour() {
		return playerTextColour;
	}

	/**
	 * @param playerTextColour the playerTextColour to set
	 */
	public void setPlayerTextColour(Color playerTextColour) {
		this.playerTextColour = playerTextColour;
	}

	/**
	 * @return the playerPosition
	 */
	public Point getPlayerPosition() {
		return playerPosition;
	}

	/**
	 * @param playerPosition the playerPosition to set
	 */
	public void setPlayerPosition(Point playerPosition) {
		this.playerPosition = playerPosition;
	}

	/**
	 * @return the type
	 */
	public PlayerType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PlayerType type) {
		this.type = type;
	}

	/**
	 * @return the currentTurn
	 */
	public boolean isCurrentTurn() {
		return currentTurn;
	}

	/**
	 * @param currentTurn the currentTurn to set
	 */
	public void setCurrentTurn(boolean currentTurn) {
		this.currentTurn = currentTurn;
	}


	
	@Override
	public Point getPlayerMapLocation() {
		return this.playerPosition;
	}
	
	/**
	 * @param playerColour the playerColour to set
	 */
	public void setPlayerColour(Color playerColour) {
		this.playerColour = playerColour;
	}


	/**
	 * @return the playerLocationHistory
	 */
	public List<Integer> getPlayerLocationHistory() {
		return playerLocationHistory;
	}


	/**
	 * @param playerLocationHistory the playerLocationHistory to set
	 */
	public void setPlayerLocationHistory(List<Integer> playerLocationHistory) {
		this.playerLocationHistory = playerLocationHistory;
	}


	/**
	 * @return the playerTicketHistory
	 */
	public List<Initialisable.TicketType> getPlayerTicketHistory() {
		return playerTicketHistory;
	}


	/**
	 * @param playerTicketHistory the playerTicketHistory to set
	 */
	public void setTicketHistory(List<Initialisable.TicketType> playerTicketHistory) {
		this.playerTicketHistory = playerTicketHistory;
	}


	/**
	 * @return the playerVisualisableHistory
	 */
	public List<Boolean> getPlayerVisualisableHistory() {
		return playerVisualisableHistory;
	}


	/**
	 * @param playerVisualisableHistory the playerVisualisableHistory to set
	 */
	public void setPlayerVisualisableHistory(
			List<Boolean> playerVisualisableHistory) {
		this.playerVisualisableHistory = playerVisualisableHistory;
	}


	@Override
	public int getTicketNumber(TicketType type) {
		
		return this.tickets.get(type);
	}


	@Override
	public List<Boolean> getVisualisableHistory() {
		return this.getPlayerVisualisableHistory();
	}


	@Override
	public List<TicketType> getTicketHistory() {
		return this.playerTicketHistory;
	}


	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}


	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


}
