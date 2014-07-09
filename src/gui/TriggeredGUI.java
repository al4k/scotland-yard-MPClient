package gui;

import ai.AI.Move;



public class TriggeredGUI extends GUI {
	
	private boolean mrXMode = false;
	
	public void setMrXMode(boolean val)
	{
		this.drawMrX = val;	
	}
	
	/**
	 * Function to tell the gui to redraw itself. It call the superclasse's
	 * drawstate function which updates the visualisation given the current 
	 * state of the game
	 */
	public void triggerUpdate()
	{
		super.drawState();
	}
	
	/**
	 * Function to tell the gui to take a move
	 */
	public Move takeMove()
	{
		
		// first we get the current player to take a move
		triggerUpdate();
		this.currentPlayer = visualisable.getNextPlayerToMove();
		
		
		// we then set a flag telling the GUI that it is able to generate moves
		// to be passed to its controllable interface. Notice the synchronise as this
		// variable will be accessed in two different threads
		synchronized (this) {
			this.canTakeTurn = true;
		}
		
		// now we loop indefinitely until the gui has taken a move
		boolean finished = false;
		while(!finished)
		{
			synchronized (this) {
				// if a valid move has been taken the variable canTakeTurn will be
				// switched to false meaning we will break out of the loop
				finished = !this.canTakeTurn;
			}
		}
		
		return recentMove;

	}
	
	/**
	 * This function sets up the gui. 
	 * @return if it was setup ok
	 */
	public boolean setUpGui()
	{
		this.initialise();
		this.canTakeTurn = false;
		this.controlledGui = true;
		return true;
	}
	
	/**
	 * This function can trigger the gui to close
	 */
	public void close()
	{
		mainWindow.setVisible(false);
	}	
}
