package gui;
import game.GameVisualiser;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import game.TextOutput;
import gui.*;
import gui.PlayerDisplayInfo.PlayerType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import ai.AI.Move;
import state.Initialisable;
import state.Initialisable.TicketType;
import state.Point;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Main visualising class
 *
 */
public class GUI extends GameVisualiser 
{
	MainWindow mainWindow;
	List<PlayerDisplayInfo>             detectives           = new ArrayList<PlayerDisplayInfo>();
	List<PlayerDisplayInfo>             mrXs                 = new ArrayList<PlayerDisplayInfo>();
	HashMap<Integer, PlayerDisplayInfo> allPlayers           = new HashMap<Integer, PlayerDisplayInfo>();
	List<Integer>                       detectiveIds         = new ArrayList<Integer>();
	List<Integer>                       mrXIds               = new ArrayList<Integer>();
	HashMap<Integer, Integer>           playerColourLookup   = new HashMap<Integer, Integer>();
	int currentPlayer;
	boolean interacting = false;
	boolean initialised = false;
	PlayerInformationReader playerInformationReader;
	protected boolean canTakeTurn = true;
	protected boolean controlledGui = false;
	protected boolean drawMrX;
	protected Move recentMove;


	/**
	 * Function to initialise the gui
	 */
	public void initialise()
	{
		if(mapVisualisable != null)
		{
			mainWindow = new MainWindow(this, mapVisualisable.getMapFilename());		
			mainWindow.setUp();
		}
		else
		{
			TextOutput.printDebug("Map Visualisable Not Set: Cant display much!\n");
		}

		if(visualisable != null) 
		{
			currentPlayer = visualisable.getNextPlayerToMove();
			playerInformationReader = new PlayerInformationReader(playerVisualisable, visualisable);
			createInitialPlayerInfo();
		}
		else TextOutput.printDebug("Visualisable Not Set\n");

		if(initialisable != null) mainWindow.setUpInitialiseCallback();
		else TextOutput.printDebug("Initialisable Not Set, GUI cant initalise a game\n");

		if(controllable != null) mainWindow.setUpMapCallbacks();
		else TextOutput.printDebug("Controllable Not Set, GUI Cant control a game\n");	
		
		TextOutput.printDebug("Game GUI Is Initialised\n");
		this.initialised = true;

	}
	
	
	public void drawMrX(boolean val)
	{
		this.drawMrX = val;
	}

	private void createInitialPlayerInfo()
	{
		// clear all previous data
		clearData();

		// get all the player id information
		currentPlayer = visualisable.getNextPlayerToMove();
		detectiveIds  = playerVisualisable.getDetectiveIdList();
		mrXIds        = playerVisualisable.getMrXIdList();

		// create the list of all players
		for(int id : detectiveIds)
		{
			PlayerDisplayInfo detective = playerInformationReader.createPlayerDisplayInfo(id, drawMrX);
			detectives.add(detective);
			allPlayers.put(id, detective);
			
		}

		for(int id : mrXIds)
		{
			PlayerDisplayInfo mrx = playerInformationReader.createPlayerDisplayInfo(id, drawMrX);
			mrXs.add(mrx);
			allPlayers.put(id, mrx);
		}
		
		TextOutput.printDebug("GUI Player Info Is Created\n");
		mainWindow.setPlayers(detectives, mrXs);

	}


	public GUI()
	{

	}

	public void close()
	{
		mainWindow.setVisible(false);
	}

	/**
	 * Function to initialise a new game given a number of 
	 * detectives. This is passed onto the initialisable interface
	 */
	public void initialiseNewGameEvent()
	{
		if(initialisable==null) return;

		boolean newGame = true;
		if(gameInProgress())
		{
			int response = JOptionPane.showConfirmDialog(mainWindow, 
					"Game is currently in progress.\n"
							+ "Do you want to restart?",
							"Game Restart?",
							JOptionPane.YES_NO_OPTION);

			if(response == JOptionPane.NO_OPTION)
			{
				newGame = false;
			}

		}

		if(newGame)
		{
			boolean inputOk = false;
			int numberOfDetectives = 0;
			while(!inputOk)
			{
				String output = (String) JOptionPane.showInputDialog(
						mainWindow,
						"How many detectives do you want to play with?\n",
						"New Game!",
						JOptionPane.PLAIN_MESSAGE);

				try
				{
					numberOfDetectives = Integer.parseInt(output);
					if(numberOfDetectives > 0 && numberOfDetectives <= 5)
					{
						inputOk = true;
					}
				}
				catch(NumberFormatException e)
				{
					inputOk = false;
				}



				if(!inputOk)
				{
					JOptionPane.showMessageDialog(mainWindow, "Please input an integer between 1-5");
				}

			}
			initialisable.initialiseGame(numberOfDetectives);
			clearData();
			drawState();		
		}
	}

	public void clearData()
	{
		// clear all old data
		detectives.clear();
		detectiveIds.clear();
		allPlayers.clear();
		mrXs.clear();
		mrXIds.clear();

		if(mainWindow==null)
		{
			TextOutput.printError("Main Window is not initialised\n");
		}

		mainWindow.clearPlayers();
	}

	public void drawState()
	{
		//createInitialPlayerInfo();
		this.currentPlayer = visualisable.getNextPlayerToMove();
		
		
		for(int key : allPlayers.keySet())
		{
			PlayerDisplayInfo player = allPlayers.get(key);
			playerInformationReader.updatePlayerDisplayInfo(player, drawMrX);
			mainWindow.updatePlayerVisualisation(player);
		}
		
		
		// check the winning conditions
		if(visualisable != null)
		{
			if(visualisable.isGameOver())
			{
				int winningPlayer = visualisable.getWinningPlayerId();
				String message = "Game Is Over! ";
				
				if(mrXIds.contains(winningPlayer))
				{
					message += " Mr X has won!\n";
				}
				else
				{
					message += String.format(" Detective %d has won\n", winningPlayer);					
				}
				
				JOptionPane.showMessageDialog(mainWindow, message);
			
			}
		}
		
	}
	


	public void mapClickEvent(Point location)
	{
		if(!canTakeTurn) return;
		if(controllable == null) return;

		Point targetLocation = new Point();
		int targetId = locationIsCloseEnough(location, targetLocation);

		if(targetId == -1)
		{
			return;
		}

		// get the ticket options dialog
		boolean moveIsOk = false;


		while(!moveIsOk)
		{
			String info = "Player " + Integer.toString(currentPlayer) + " take your go!"; 
			Initialisable.TicketType ticketChoice = TakeMoveDialog.getMove(mainWindow, this.allPlayers.get(currentPlayer), info);

			// check for the double move



			if(ticketChoice == null)
			{
				System.out.println("Do Nothing");
				return;
			}
			
			
			
			if(controlledGui)
			{
				recentMove = new Move(ticketChoice, targetId);
				moveIsOk = true;
			}
			else
			{
				moveIsOk = controllable.movePlayer(currentPlayer, targetId, ticketChoice);
			}
		}

		
		PlayerDisplayInfo playerInfo = allPlayers.get(currentPlayer);

		
		
		// try the move
		if(moveIsOk)
		{
			playerInformationReader.updatePlayerDisplayInfo(playerInfo, drawMrX); 
			playerInformationReader.updatePlayerDisplayInfo(allPlayers.get(mrXIds.get(0)), drawMrX);

			// update the current player visualisation and the mrx visualisation
			mainWindow.updatePlayerVisualisation(allPlayers.get(currentPlayer));
			mainWindow.updatePlayerVisualisation(allPlayers.get(mrXIds.get(0)));

			allPlayers.get(currentPlayer).setCurrentTurn(false);

			// update the next turn
			currentPlayer = visualisable.getNextPlayerToMove();
			allPlayers.get(currentPlayer).setCurrentTurn(true);
			// redraw the gui
			mainWindow.revalidate();
			mainWindow.repaint();
			
			
			
			if(controlledGui && canTakeTurn) 
			{
				synchronized (this) {
					this.canTakeTurn = false;	
				}
			}
		}
		drawState();
		
	}
	

	public int locationIsCloseEnough(Point input, Point output)
	{
		if(controllable == null) return 0;
		if(visualisable == null) return 0;
		
		// get nearest node to the location
		int nodeId = controllable.getNodeIdFromLocation(input.x(), input.y());
	
		
		// get the node distance
		output.x(visualisable.getLocationX(nodeId));
		output.y(visualisable.getLocationY(nodeId));
		double distance = input.distanceTo(output);

		if(distance < 25.0)
		{
			return nodeId;
		}
		else
		{
			return -1;
		}


	}
	

	public void mapMouseHoverEvent(Point location)
	{
		if(!canTakeTurn) return;
		Point targetLocation = new Point();
		int targetId = locationIsCloseEnough(location, targetLocation);	
		Color colour = Color.WHITE;
		Point outputLocation = location;

		// check if the click is valid
		if(targetId != -1)
		{
			// snap the location to the node
			PlayerDisplayInfo player = allPlayers.get(currentPlayer);
			colour = player.getPlayerColour();
			outputLocation = targetLocation;

		}

		//System.out.println(location.toString());
		mainWindow.setPossibleMoveMarker(outputLocation,colour);
	}
	


	public void run()
	{

		mainWindow.setVisible(true);

	}



	/**
	 * Function to check if a game is in progress
	 * @return true if game is in progress
	 */
	public boolean gameInProgress()
	{
		if(playerVisualisable.getDetectiveIdList().size() > 0 && 
				playerVisualisable.getMrXIdList().size() > 0)
		{
			return true;
		}

		return false;
	}

}
