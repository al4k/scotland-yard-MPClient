package net;

import java.util.*;

import javax.xml.ws.Response;

import org.omg.CORBA.RepositoryIdHelper;

import game.TextOutput;
import state.Initialisable;


public class TCPClient extends ClientBase {
	
	private NetworkWrapper network;
	private int id;
	
	
	public TCPClient(int id, String host, int port)
	{
		this.id = id;
		// initialise the networ
		network = NetworkWrapper.getConnection(host, port, id);
		network.setPrintRequests(false);
		network.setPrintResponses(false);
		if(network == null) 
		{
			TextOutput.printError("Couldn't establish Connection with server\n");
			System.exit(1);
		}
	}
	
   
 


    
    
    public boolean resetServerGame()
    {
    	return false;
    }
    
    /**
     * This function will query the server to try and join the current game. If 
     * this is possible, the server should return the list of player ids that 
     * this client will take control of
     * @return List of integers representing the players ids assigned to this client
     */
    public synchronized List<Integer> joinServerGame()
    {
    	List<Integer> output = new ArrayList<Integer>();
    	
    	String call = "join";
    	String reponse = network.send(call);
    	String parts1[] = reponse.split(",");
    	String[] ids = parts1[1].split(":");
    	
    	for(String id : ids)
    	{
    		output.add(Integer.parseInt(id));
    	}
    	
		return output;
    }
    
    /**
     * This function should send a command to the server to make a move. If the move is possible
     * the server will make the move and write it to the db and this function should return true
     * @param playerId The id of the player
     * @param targetLocation The target location
     * @param ticket The type of ticket
     * @return true if the move was successful
     */
    public synchronized boolean makeServerMove(int playerId, int targetLocation, Initialisable.TicketType ticket)
    {
    	String call = "move," + Integer.toString(playerId) + "," + Integer.toString(targetLocation) + "," + ticket;
    	String resp = network.send(call);
    	String[] response = resp.split(",");
		if(Integer.parseInt(response[1]) == 1)
		{
			return true;
		}
		else
		{	
			return false;
		}
    }
    
    /**
     * This function is used to get the next player to move.
     * @return The id of the next player to move
     */
    public  synchronized int getServerNextPlayer()
    {
    	
    	String call = "next_player";
		String[] response = network.send(call).split(",");
		
		
		if(Integer.parseInt(response[1]) == 0)
		{
			return -1;
		}
		
		return Integer.parseInt(response[2]);
		
    }
    
    /**
     * This function will query the server to ask it is the current game is
     * over. This function should then return an appropriate value
     * @return True is game is over, false if not
     */
    public synchronized boolean getServerGameOver()
    {

		String call = "game_over";
		String resp = network.send(call);
		//System.out.println("GAME OVERR: " + resp);
		
		String[] response = resp.split(",");
		
		if(response.length <= 1)
		{
			System.out.println(resp);
			System.exit(0);
		}
		
		if(Integer.parseInt(response[1]) == 1)
		{
			return true;
		}
		
		
		return false;
    }
    
    
    public synchronized double getServerTurnOver(int playerId)
    {
    	String call = "turn_over," + Integer.toString(playerId);
    	String resp = network.send(call);
    	
    	String[] response = resp.split(",");
    	double time = Double.parseDouble(response[1]);
    	
    	
    	return time;
    }
    
    
    /**
     * This function is used to get the winning player from the server. This should
     * then return the id if the game is over. 
     * @return The winning players id
     */
    public synchronized int getServerWinningPlayer()
    {
    	String call = "winning_player";
    	String[] response = network.send(call).split(",");
		if(Integer.parseInt(response[1]) == 1)
		{
			return Integer.parseInt(response[2]);
		}
    	return -1;
    }
    
    

	
	
	
	
	public synchronized String getServerGraph()
	{
		String filename = "graph" + Integer.toString(id) + ".txt";
		return getTextFile("graph", filename);
	}
	
	
	private synchronized String getTextFile(String type, String filename)
	{
		String serverCall = "get_file," + type;
		boolean success = network.getTextFile(serverCall, filename);
		if(success) return filename;
		else return null;
	}
	
	public synchronized String getServerMap()
	{
		String serverCall = "get_file,map";
		String filename = "map" + Integer.toString(id) + ".txt";
		boolean success = network.getBinaryFile(serverCall, filename);
		
		if(success) return filename;
		else return null;
	}
	
	public synchronized String getServerGame()
	{
		String filename = "game" + Integer.toString(id) + ".txt";
		
		return getTextFile("game", filename);
	}
	
	
	public synchronized String getServerNodePositions()
	{
		String filename = "pos" + Integer.toString(id) + ".txt";
		return getTextFile("pos", filename);
	}

    /**
     * This is the function that will initialise a a game session on the 
     * server. 
     * @param sessionName A given name of the session
     * @param numDetectives The number of detectives that are going to player
     * @return If it was initalised ok
     */
    public synchronized boolean initialiseServerGame(String sessionName, int numDetectives, int filesId)
    {
    	String serverCall = "initialise," + 
				Integer.toString(numDetectives) + 
				","+ sessionName + "," + 
				Integer.toString(filesId);
				
				
		String response = network.send(serverCall);
		
		String[] parts = response.split(",");
		int returnCode = Integer.parseInt(parts[1]);
		System.out.println(returnCode);
		if(returnCode == 0)
		{
			return false;
		}
		
		return true;
    }

}
	
	

	
	
	
