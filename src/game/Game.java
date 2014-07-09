package game;
import ai.*;
import game.GameController.PlayerAssignments;
import gui.TriggeredGUI;
import state.*;


public class Game {

	public static void main(String[] args) {
		Game game = new Game();
		TextOutput.setDebugMode(true);
		int id = Integer.parseInt(args[0]);
		int useServer = Integer.parseInt(args[1]);
		int vis = Integer.parseInt(args[2]);
		int ai = Integer.parseInt(args[3]);
		game.run(id, useServer, vis, ai, args[4], Integer.parseInt(args[5]));
	}
	

	
	public void run(int id, int server, int vis, int ai, String host, int port)
	{
		// create all the object for the game
		ClientGameState state = new ClientGameState();
		GameController controller = new GameController(id, host, port);
		TriggeredGUI gui = new TriggeredGUI();
		
		// initialise the class that scores the boards
		/*
        FreedomDistanceBoardScorer boardScorer = new FreedomDistanceBoardScorer();
		boardScorer.setDistanceWeighting(20);
		*/

        // initialise Smart AI
        SmartAI sAi = new SmartAI();
        /*
		// initialise the Mr X AI
		MinMaxAi mrXAi = new MinMaxAi();
		mrXAi.setBoardScorer(boardScorer);		
		
		// set the ai parameters
		mrXAi.setPly(3);
		mrXAi.setAlphaBetaTolerance(0.0);
		mrXAi.setMaxEvaluations(-1);
		mrXAi.setUseQuiescenceExpansion(false);
		mrXAi.setQuiescenceThreshold(25);
		mrXAi.setQuiescenceDepthLimit(1);
		mrXAi.setUseAlphaBetaPruning(true);
		mrXAi.setUseRandomChoice(false);
		mrXAi.setUseQuiescentChoice(false);
		mrXAi.setRandomChoiceTolerance(0.0);
		mrXAi.setTimeLimit(3000); // about 3 seconds
		
		// initialise the detective AI		
		SimpleDetectiveAI detectiveAI = new SimpleDetectiveAI();
        */
		
		
		// register the Ai readable and client controllable objects (local game state)
		controller.registerAIReadable(state);
		controller.registerClientControllable(state);
		
		// register the gui and the ai
		/*
        controller.setMrXAI(mrXAi);
		controller.setDetectiveAI(detectiveAI);
		*/
        controller.setAI(sAi);
		controller.registerGUI(gui);
		
		// specify the mode of the game we are playing
		if(vis == 0)
			controller.setVisualise(false);
		else
			controller.setVisualise(true);
		
		if(ai == 0)
			controller.setPlayerAssignments(PlayerAssignments.AllAI);
		else if (ai == 1)
			controller.setPlayerAssignments(PlayerAssignments.MrXAI);
		else
			controller.setPlayerAssignments(PlayerAssignments.AllGui);
		
		if(server==1)
			controller.setUseServer(true);
		else 
			controller.setUseServer(false); // note, server mode will not work
		
		// run the game
		controller.run("Test Session");
	}
}
