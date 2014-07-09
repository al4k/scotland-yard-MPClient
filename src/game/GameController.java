package game;

import net.RemoteControllable;
import net.RemoteInitialisable;
import net.TCPClient;
import state.ClientControllable;
import state.Initialisable;
import gui.TriggeredGUI;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;

import javax.security.auth.callback.TextOutputCallback;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ai.AI;
import ai.AIReadable;
import ai.AI.Move;


public class GameController {


	private TriggeredGUI gui;
	private List<Integer> aiIDS = new ArrayList<Integer>();
	private List<Integer> guiIDS = new ArrayList<Integer>();
	private AI mrXAi;
	private AI detectiveAi;
    private AI ai;
	private int id;
	
	private final String mapFilename = "../resources/map.jpg";
	private final String posFilename = "../resources/pos.txt";
	private final String graphFilename = "../resources/graph.txt";
	
	private String host = "";
	private int port = 0;

	private boolean visualise;
	private boolean useServer;
	public enum PlayerAssignments { AllGui, AllAI, MrXAI };

	protected AIReadable aiReadable;
	protected ClientControllable clientControllable;
	protected RemoteInitialisable remoteInitialisable;
	protected RemoteControllable remoteControllable;
	
	
	/**
	 * Subclass that allows the timer to stop the
	 * move making process
	 */
	class MoveMaker implements Runnable 
	{
		Move move;
		int playerId;
		
		public MoveMaker(int playerId) 
		{
			this.playerId = playerId;
		}

		@Override
		public void run() {
			synchronized (this) {
				move = null;
				move = getMove(playerId);
			}
		}
		
		/**
		 * Function to get a move from either the gui or the ai
		 * @param playerId
		 * @return
		 */
		private Move getMove(int playerId)
		{
			if(guiControlled(playerId))
			{
				synchronized (GameController.this) {
					return gui.takeMove();
				}
				
			}
			else
			{
				synchronized (GameController.this) {
                    if(ai == null)
                    {
                        if(isMrx(playerId))
                            return mrXAi.getMove(playerId);
                        else
                            return detectiveAi.getMove(playerId);
                    }
                    else
                        return ai.getMove(playerId);
				}
				
			}
		}
		
	}


	protected PlayerAssignments assignments = PlayerAssignments.MrXAI;


	/**
	 * Decide if you want the game to run on the server
	 * @param val
	 */
	public void setUseServer(boolean val)
	{
		useServer = val;
	}

	/**
	 * Decide if you want to visualise the game. NOTE, this is
	 * only really going to work in the full AI controlled game as the gui 
	 * is needed to input player moves in other modes
	 * @param vis
	 */
	public void setVisualise(boolean vis)
	{
		visualise = vis;
	}


	/**
	 * This function allows you to set different player assignments. This can either
	 * be Full AI, Mr X AI or Full GUI
	 * @param assignment
	 */
	public void setPlayerAssignments(PlayerAssignments assignment)
	{
		this.assignments = assignment;
	}

	/**
	 * Function to set an AI object
	 * @param a
	 */
	public void setMrXAI(AI a)
	{
		this.mrXAi = a;
	}

	public void setDetectiveAI(AI a)
	{
		this.detectiveAi = a;
	}

    public void setAI(AI a) { this.ai = a;}

	/**
	 * Function to register a triggered AI
	 * @param gui
	 */
	public void registerGUI(TriggeredGUI gui)
	{
		this.gui = gui;
	}




	/**
	 * Here we check that the set up is ok depending on
	 * the different mode we have selected the GameController to use
	 * @return if setup was ok
	 */
	private boolean setupOK()
	{
		// check that if we are using an AI, an AI has been assigned
		if(this.assignments == PlayerAssignments.AllAI)
		{
            if(ai == null)
            {
                if(detectiveAi == null)
                {
                    TextOutput.printDebug("No Detective AI Set\n");
                    return false;
                }

                if(mrXAi == null)
                {
                    TextOutput.printDebug("No Mr X AI Set\n");
                    return false;
                }
            }
		}

		if(this.assignments == PlayerAssignments.MrXAI)
		{
            if(ai == null)
            {
                if(mrXAi == null)
                {
                    TextOutput.printDebug("No Mr X AI Set\n");
                    return false;
                }
            }
		}	

		// check that the needed objects have been set
		if(clientControllable == null)
		{
			TextOutput.printDebug("CLient Controllable Not Set\n");
			return false;
		}
		if(aiReadable == null)
		{
			TextOutput.printDebug("AI Readable Not Set\n");
			return false;
		}

		// depending on if we are using the server or not, we
		// do a different set of checks
		if(useServer)
		{
			return serverSetUpOk();
		}
		else
		{
			return offlineSetUpOk();
		}
	}

	/**
	 *  here we check that the GUI (no server) set up is ok
	 * @return is setup was ok
	 */
	private boolean offlineSetUpOk()
	{
		// obviously we need a valid gui object
		if(gui == null)
		{
			TextOutput.printDebug("No Gui Set\n");
			return false;
		}

		return true;
	}

	/**
	 * Function to check that the server setup is ok
	 * @return is setup was ok
	 */
	private boolean serverSetUpOk()
	{
		if(remoteControllable == null)
		{
			TextOutput.printDebug("Remote Controllable Not Set\n");
			return false;
		}


		return true;
	}




	/**
	 * Main function to run the game
	 */
	public void run(String sessionName)
	{
		//if(!setupOK())
		//{
		//	TextOutput.printError("Setup Not Ok\n");
		//}


		// check that if we are using an AI, an AI has been assigned
		if(this.assignments == PlayerAssignments.AllAI)
		{
            if(ai == null)
            {
                mrXAi.registerAiReadable(aiReadable);
                detectiveAi.registerAiReadable(aiReadable);
            }
			else
            {
                ai.registerAiReadable(aiReadable);
            }
		}

		if(this.assignments == PlayerAssignments.MrXAI)
		{
            if(ai == null)
            {
			    mrXAi.registerAiReadable(aiReadable);
            }
            else
            {
                ai.registerAiReadable(aiReadable);
            }
        }

		// run either offline or online
		if(!useServer)
		{
			runOffline();
		}
		else
		{
			runServer();
		}

	}

	/**
	 * Function that will be running the game on the server
	 */
	public void runServer()
	{

		// first we establish the TCP connection and we 
		// assign it to the relevant objects
		TCPClient tcp = new TCPClient(this.id, this.host, this.port);
		registerRemoteControllable(tcp);
		registerRemoteInitialisable(tcp);

		// assign the tcp connection the local game state so that it
		// is able to push changes to it
		tcp.registerClientControllable(clientControllable);

		// register the relevant objects with the GUI
		gui.registerVisualisable(aiReadable);
		gui.registerControllable(tcp);
		

		List<Integer> playerIds = remoteControllable.joinServerGame();
		
		if(playerIds == null)
		{
			TextOutput.printError("Couldn't Get the Player IDS\n");
			System.exit(1);
		}

		// we then get the files needed for the game to operate and initialise the local
		// game state with them
		String gameFilename = remoteInitialisable.getServerGame();
		//String graphFilename = remoteInitialisable.getServerGraph();
		//String posFilename = remoteInitialisable.getServerNodePositions();
		//String mapFilename = remoteInitialisable.getServerMap();
		
		this.clientControllable.initialiseGame(mapFilename, graphFilename, posFilename, gameFilename);


		TextOutput.printDebug("Game State Loaded From Server\n");


		// split the players between the gui and ai
		setUpAIandGUIIds(playerIds);
		
		
		


		boolean gameIsOver = false;
		while(!gameIsOver)
		{
			// get the current player
			int currentPlayer = getNextPlayer(playerIds);
			if(currentPlayer == -1) break;
			
			
			TextOutput.printDebug("---------------------------------------------\n");
			TextOutput.printDebug("Taking Turn For Player: " + currentPlayer + "\n");
			TextOutput.printDebug("Updating Game State From The Server\n");
			
			// update the game
			String updatedGame = remoteInitialisable.getServerGame();
			clientControllable.loadGame(updatedGame);
			gui.triggerUpdate();
			
			System.out.println("Game Updated");
			aiReadable.setCurrentPlayer(currentPlayer);
					
			
			// start the count down
			MoveMaker moveMaker = new MoveMaker(currentPlayer);
			TurnCountdown countdown = new TurnCountdown(remoteControllable, currentPlayer);
			countdown.start();
			
			// loop until we have a good move
			boolean moveIsGood = false;
			while(!moveIsGood)
			{
				
				
				Thread moveThread = new Thread(moveMaker);
				moveThread.start();
				
				
				// we loop until either a move is taken or we break
				boolean moveIsSubmitted = false;
				while(!moveIsSubmitted) {
					
					synchronized (moveMaker) {
						if(moveMaker.move != null)
							moveIsSubmitted = true;
					}
					
					// check if we need to break the move
					if(breakMove(currentPlayer, countdown)) 
						break;
				}
				
				// interupt the move thread
				moveThread.interrupt();
				
					
				// if the move is still null then a turn hasn't been taken so we 
				// skip
				if(moveMaker.move == null) break;
				
			
				
				// submit the move to the server
				moveIsGood = remoteControllable.makeServerMove(currentPlayer, moveMaker.move.location, moveMaker.move.type);
			
				
				// check if the move is ok
				if(moveIsGood)
				{
					countdown.stop();
					TextOutput.printDebug("Move Player " + currentPlayer + " to location " + 
							moveMaker.move.location + " using " + moveMaker.move.type  + "\n\n");
				}
				else
				{
					moveMaker.move = null;
				}
			}
			
			
			
			// stop the timer and update the gui
			countdown.stop();

			updatedGame = remoteInitialisable.getServerGame();
			clientControllable.loadGame(updatedGame);
			gui.triggerUpdate();
			
		}
		
		finishUp();
	}
	
	
	/**
	 * Function to check if we should break a move 
	 * @param currentPlayer
	 * @return
	 */
	boolean breakMove(int currentPlayer, TurnCountdown timer)
	{
		if(timer.turnOver()) 
		{
			System.out.println("Turn Over");
			return true;
		}
		
		// do the check for the game over
		boolean gameIsOver = remoteControllable.getServerGameOver();
		if(gameIsOver) return true;
		
		// check if the current player is wrong
		int playerCheck = remoteControllable.getServerNextPlayer();
		if(currentPlayer != playerCheck) return true;
		
		return false;
	}
	
	
	/**
	 * Here we have the function that runs offline mode
	 */
	public void runOffline()
	{
		//  register the visualisable and controllable objects with the gui
		gui.registerControllable(clientControllable);
		gui.registerVisualisable(aiReadable);


		// initialise a new game in the game state
		// and set up the gui
		clientControllable.initialiseGame(2);
		gui.setMrXMode(true);
		gui.setUpGui();


		// get the set of ids from the game state
		List<Integer> dIds = aiReadable.getDetectiveIdList();
		List<Integer> xIds  = aiReadable.getMrXIdList();

		List<Integer> allIds = new ArrayList<Integer>();
		for(int id : dIds)
		{
			allIds.add(id);
		}

		for(int id : xIds)
		{
			allIds.add(id);
		}

		// split the players between the gui and ai
		setUpAIandGUIIds(allIds);

		// do we want the gui?
		if(visualise) SwingUtilities.invokeLater(gui);


		
		while(!aiReadable.isGameOver())
		{
			// get the current player
			int currentPlayer = aiReadable.getNextPlayerToMove();
			
			TextOutput.printDebug("---------------------------------------------\n");
			TextOutput.printDebug("Taking Turn For Player: " + currentPlayer + "\n");
			MoveMaker moveMaker = new MoveMaker(currentPlayer);
			
			Move move = moveMaker.getMove(currentPlayer);
			
			// Use the suggested Move by the ai and push it to the game state
			clientControllable.movePlayer(currentPlayer, move.location, move.type);
			
			TextOutput.printDebug("Move Player " + currentPlayer + " to location " + 
					move.location + " using " + move.type  + "\n\n");
			
					//TextOutput.printDebug("Player " + currentPlayer + " "  + oldLocation + " -> " + move.location + " " + move.type + "\n");


			// trigger the gui to redraw itself
			gui.triggerUpdate();

		}

		// game has been won, get the winning player
		int winner = aiReadable.getWinningPlayerId();
		TextOutput.printDebug(String.format("The winning Player is: %d\n", winner));
		gui.close();
		System.exit(0);

	}
	
	
	
	
	
	
	private void finishUp()
	{
		TextOutput.printDebug("Game Is Over!\n");
		// game has been won, get the winning player
		int winner = remoteControllable.getServerWinningPlayer();
		//TextOutput.printDebug(String.format("The winning Player is: %d\n", winner));
		
		
		// bring up the alert
		JOptionPane.showMessageDialog(null, "Game Is Over!\n" + 
				String.format("The winning Player is: %d\n", winner));
		
		gui.close();
		System.exit(0);
	}
	
	
	private int getNextPlayer(List<Integer> playerIds)
	{
		// get the current player from the server
		int currentPlayer  = -1; 
		while(!playerIds.contains(currentPlayer))
		{
			// if the game is over we return -1
			if(remoteControllable.getServerGameOver())
				return -1;
			
		
			currentPlayer = remoteControllable.getServerNextPlayer();
			aiReadable.setCurrentPlayer(currentPlayer);
			gui.triggerUpdate();
		}
		return currentPlayer;
	}




	/**
	 * This function determines if a given id is controlled by the gui
	 * @param id
	 * @return true if gui controlled
	 */
	private boolean guiControlled(int id)
	{
		for(int t : guiIDS)
		{
			if(t == id) return true;
		}
		return false;
	}


	/**
	 * This function takes all the ids of the players and splits
	 * them by the choice of player assignment
	 * and given the 
	 * @param playerIds
	 */
	private void setUpAIandGUIIds(List<Integer> playerIds)
	{
		for(int id : playerIds)
		{
			if(assignments == PlayerAssignments.AllAI)
			{
				aiIDS.add(id);
			}
			else if(assignments == PlayerAssignments.AllGui)
			{
				guiIDS.add(id);
			}
			else
			{
				if(isMrx(id)) aiIDS.add(id);
				else guiIDS.add(id);
			}
		}
		
		boolean drawMrX = playerIds.contains(aiReadable.getMrXIdList().get(0));
		clientControllable.setMrXClient(drawMrX);

		

		// initialise the gui
		if(gui != null)
		{
			gui.setUpGui();
			gui.drawMrX(drawMrX);
			gui.triggerUpdate();
			if(visualise) SwingUtilities.invokeLater(gui);

		}
	}

	/**
	 * Function to check if a player id is of mr x
	 * @param id
	 * @return true if mr x
	 */
	private boolean isMrx(int id)
	{
		for(int t : aiReadable.getMrXIdList())
		{
			if(t == id) return true;
		}
		return false;
	}


	// registration functions
	public void registerAIReadable(AIReadable aiReadable)
	{
		this.aiReadable = aiReadable;
	}

	public void registerClientControllable(ClientControllable con)
	{
		this.clientControllable = con;
	}

	public void registerRemoteInitialisable(RemoteInitialisable in)
	{
		this.remoteInitialisable = in;
	}

	public void registerRemoteControllable(RemoteControllable con)
	{
		this.remoteControllable = con;
	}


	public GameController(int id, String host, int port)
	{
		this.host = host;
		this.id = id;
		this.port = port;
	}
}
