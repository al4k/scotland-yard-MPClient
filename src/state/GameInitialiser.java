package state;


import game.TextOutput;

import java.util.*;
import java.io.*;

import state.Player.PlayerType;

public class GameInitialiser {
	private final static String DETECTIVE_START_POS_FILENAME = "resources/detective_starts.txt";
	private final static String MR_X_START_POS_FILENAME = "resources/x_starts.txt";
	List<Integer> detectiveStartPositions;
	List<Integer> mrXStartPositions;
	
	// constants for the number of tickets given
	private final static int UNDERGROUND_TICKETS = 4;
	private final static int BUS_TICKETS         = 8;
	private final static int TAXI_TICKETS        = 11;
	private final static int SECRET_TICKETS      = 5;
	private final static int DOUBLE_TICKETS      = 2;
	
	private final static int TOTAL_UNDERGROUND_TICKETS = 23;
	private final static int TOTAL_BUS_TICKETS         = 45;
	private final static int TOTAL_TAXI_TICKETS        = 57;
	
	public GameInitialiser()
	{
		
		
		
	}
	
	public GameData initialiseGame(int numberOfDetectives)
	{
		detectiveStartPositions = loadStartPositions(DETECTIVE_START_POS_FILENAME);
		mrXStartPositions       = loadStartPositions(MR_X_START_POS_FILENAME);
		
		GameData data = new GameData();
		
		// keep a count of the remaining tickets
		int taxiCount = TOTAL_TAXI_TICKETS;
		int busCount = TOTAL_BUS_TICKETS;
		int undergroundCount = TOTAL_UNDERGROUND_TICKETS;
		
		
		// first create the detectives
		int playerIdCount = 0;
		List<Integer> usedStartingLocations = new ArrayList<Integer>();
		for(int i = 0; i < numberOfDetectives; i++)
		{
			int startPos = getStartingPosition(detectiveStartPositions, usedStartingLocations);
			Player newPlayer = new Player(
					playerIdCount, 
					PlayerType.Detective, startPos, 
					getDetectiveTickets());
			
			data.addPlayer(newPlayer);
			
			taxiCount -= TAXI_TICKETS;
			busCount -= BUS_TICKETS;
			undergroundCount -= UNDERGROUND_TICKETS;
			
			playerIdCount++;
		}
		
		// now create the Mr x
		int startPos = getStartingPosition(mrXStartPositions, usedStartingLocations);
		Player mrX = new Player(
				playerIdCount, 
				PlayerType.MrX, startPos,
				getMrXTickets(taxiCount, busCount, undergroundCount));
		
		data.addPlayer(mrX);
		//data.currentPlayer(0);
		
		
		
		
		
		return data;
	}
	
	/**
	 * Get a valid starting position
	 * @param positions
	 * @param usedPositions
	 * @return The starting position
	 */
	private int getStartingPosition(List<Integer> positions, List<Integer> usedPositions)
	{
		int index = (int)(Math.random() * positions.size());
		int value = positions.get(index);
		while(usedPositions.contains(value))
		{
			//System.out.println(value);
			index = (int)(Math.random() * positions.size());
			value = positions.get(index);
		}
		
		usedPositions.add(value);
		
		return value;
	}
	
	private LinkedHashMap<Initialisable.TicketType, Integer> getDetectiveTickets()
	{
		LinkedHashMap<Initialisable.TicketType, Integer> tickets = new LinkedHashMap<Initialisable.TicketType,Integer>();
		tickets.put(Initialisable.TicketType.Bus, BUS_TICKETS);
		tickets.put(Initialisable.TicketType.Taxi, TAXI_TICKETS);
		tickets.put(Initialisable.TicketType.Underground, UNDERGROUND_TICKETS);
		tickets.put(Initialisable.TicketType.SecretMove, 0);
		tickets.put(Initialisable.TicketType.DoubleMove, 0);
		
		return tickets;
	}
	
	private LinkedHashMap<Initialisable.TicketType, Integer> getMrXTickets(int taxi, int bus, int underground)
	{
		LinkedHashMap<Initialisable.TicketType, Integer> tickets = new LinkedHashMap<Initialisable.TicketType,Integer>();
		tickets.put(Initialisable.TicketType.Bus, bus);
		tickets.put(Initialisable.TicketType.Taxi, taxi);
		tickets.put(Initialisable.TicketType.Underground, underground);
		tickets.put(Initialisable.TicketType.SecretMove, SECRET_TICKETS);
		tickets.put(Initialisable.TicketType.DoubleMove, DOUBLE_TICKETS);
		
		return tickets;
	}
	
	
	
	/**
	 * Function to load a start position file from disk
	 * @param filename
	 * @return List of starting positions
	 */
	List<Integer> loadStartPositions(String filename)
	{
		List<Integer> positions = new ArrayList<Integer>();
		
		Scanner fileScanner = null;
		try 
		{
			fileScanner = new Scanner(new File(filename));
		} 
		catch (FileNotFoundException e) 
		{
			String message = String.format("Couldn't find file: " + filename + "\n" + e.getMessage());
			TextOutput.printError(message);
			System.exit(1);
		}
		
		// read the lines of the file
		while(fileScanner.hasNextLine())
		{
			String line = fileScanner.nextLine();
			positions.add(Integer.parseInt(line));
		}
		
		fileScanner.close();
		
		return positions;
	}
	
	
}
