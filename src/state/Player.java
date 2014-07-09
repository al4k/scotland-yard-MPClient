package state;
import java.util.*;

import state.Initialisable.TicketType;

/**
 * Class representing a player
 */
public class Player {
	
	enum PlayerType { Detective, MrX };
	
	public static final Boolean[] mrXAppearanceList = {
		true, false, false, true, 
		false, false, false, false, true,
		false, false, false, false, true,
		false, false, false, false, true,
		false, false, false, false, false, true
	};
	
	
	private int playerId;
	private PlayerType playerType;
	private LinkedHashMap<Initialisable.TicketType, Integer> playerTickets;
	private List<Integer> locationHistory = new ArrayList<Integer>();
	private List<Initialisable.TicketType> ticketHistory = new ArrayList<Initialisable.TicketType>();
	private List<Boolean> visualisableHistory = new ArrayList<Boolean>();
	private int currentLocation;
	private int turnNumber = 0;

	
	/**
	 * Player constructor
	 * @param id Id of the player
	 * @param type Type of the player
	 * @param location Location of the player
	 */
	public Player(int id, PlayerType type, int location, LinkedHashMap<Initialisable.TicketType, Integer> tickets)
	{
		playerId = id;
		playerType = type;
		currentLocation = location;
		
		// initialise the tickets
		playerTickets = tickets;	
		
		if(type == PlayerType.MrX)
		{
			visualisableHistory.add(false);
		}
		else
		{
			visualisableHistory.add(true);
		}
		
		setStartLocation(location);
	}
	
	
	public void setStartLocation(int location)
	{
		if(locationHistory.size() == 0)
			locationHistory.add(0, location);
		else
			locationHistory.set(0, location);
	}
	
	public int numberOfMoves()
	{
		return ticketHistory.size();
	}

	
	
	public void addMove(Initialisable.TicketType type, int location)
	{
		if(type == Initialisable.TicketType.DoubleMove)
			return;
		
		//System.out.println("Turns: " + turnNumber);
		locationHistory.add(location);
		if(this.type() == PlayerType.MrX)
		{
			visualisableHistory.add(mrXAppearanceList[turnNumber]);
		}
		else
		{
			visualisableHistory.add(true);
		}
		ticketHistory.add(type);
		
		turnNumber++;
	}
	
	public void useTicket(Initialisable.TicketType type)
	{
		if(playerTickets.get(type) > 0)
		{
			playerTickets.put(type, playerTickets.get(type)-1);
		}
	}
	
	public void addTicket(Initialisable.TicketType type)
	{
		playerTickets.put(type, playerTickets.get(type)+1);
	}
	
	public int getTicketNumber(TicketType type)
	{
		if(playerTickets.containsKey(type))
		{
			return playerTickets.get(type);
		}
		
		return 0;
	}
	
	public PlayerType type()
	{
		return playerType;
	}
	
	public int playerId()
	{
		return playerId;
	}
	
	/**
	 * Function to get the current location of a player
	 * @return The players current location
	 */
	int getCurrentLocation()
	{
		// iterate through the locations
		//System.out.println("Getting Location");
		if(type() == PlayerType.MrX)
		{
			for(int i = locationHistory.size()-1; i >= 0; i--)
			{
				//System.out.println(i + ": " + mrXAppearanceList[i] + " - " + locationHistory.get(i));
				if(mrXAppearanceList[i] == true)
				{
				//	System.out.println("---------------------");
					return locationHistory.get(i);
				}
			}
				
			
			//System.out.println("---------------------");
			return locationHistory.get(0);
		}
		else
		{
			return currentLocation; 
		}
	}
	
	public int getActualLocation()
	{
		return currentLocation;
	}
	
	
	boolean isAtLocation(int loc)
	{
		if(this.currentLocation == loc) return true;
		return false;
	}
	
	boolean hasTickets(Initialisable.TicketType type)
	{
		if(this.playerTickets.get(type) > 0) return true;
		return false;
	}
	
	public int getNumberOfMoves()
	{
		return locationHistory.size();
	}
	
	
	public void moveTo(int target, TicketType type)
	{
		addMove(type, target);
		currentLocation = target;
	}
	
	@Override
	public String toString() {
		return "Player [playerId=" + playerId + ", playerType=" + playerType
				+ ", playerTickets=" + playerTickets + ", moveHistory="
				+ locationHistory + ", ticketHistory=" + ticketHistory
				+ ", visualisableHistory=" + visualisableHistory
				+ ", currentLocation=" + currentLocation + ", turnNumber="
				+ turnNumber + "]";
	}


	public void moveToSecret(int target)
	{
		locationHistory.add(target);
		if(this.type() == PlayerType.MrX)
		{
			visualisableHistory.add(mrXAppearanceList[turnNumber]);
		}
		else
		{
			visualisableHistory.add(true);
		}
		ticketHistory.add(TicketType.SecretMove);
		
		currentLocation = target;
		turnNumber++;
	}
	
	
	public List<Initialisable.TicketType> ticketHistory()
	{
		return ticketHistory;
	}
	
	public Boolean isVisible()
	{
		return visualisableHistory.get(turnNumber);
	}
	
	
	public List<Integer> getPositionList()
	{
		return locationHistory;
	}
	
	
}
