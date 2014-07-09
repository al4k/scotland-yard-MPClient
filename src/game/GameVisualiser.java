package game;


import state.Controllable;
import state.Initialisable;
import state.MapVisualisable;
import state.PlayerVisualisable;
import state.Visualisable;


/**
 * Abstract class that ensures that any inheriting class will be able 
 * to visualise a GameState that implements the correct interfaces. This
 * class also implements the Runnable interface
 */
public abstract class GameVisualiser implements Runnable
{
	
	protected MapVisualisable mapVisualisable;
	protected PlayerVisualisable playerVisualisable;
	protected Visualisable visualisable;
	protected Controllable controllable;
	protected Initialisable initialisable;
	
	/**
	 * Abstract run function from the Runnable interface. This is
	 * abstract so that inheriting classes must implement
	 */
	public abstract void run();
	
	
	/**
	 * Function to register a class that implements MapVisualisable
	 * @param a The object that implements MapVisualisable
	 */
	public void registerMapVisualisable(MapVisualisable a)
	{
		mapVisualisable = a;
	}
	
	/**
	 * Function to register an object that implements the PlayerVisualisable interface
	 * @param a The PlayerVisualisable object
	 */
	public void registerPlayerVisualisable(PlayerVisualisable a)
	{
		playerVisualisable = a;
	}
	
	/**
	 * Function to register an object that implements the Visualisable interface. This function
	 * will also set the PlayerVisualisable, MapVisualisable object  and the Intisable object
	 * @param a The Visualisable object 
	 */
	public void registerVisualisable(Visualisable a)
	{
		visualisable = a;
		registerPlayerVisualisable(a);
		registerMapVisualisable(a);
		registerInitialisable(a);
	}

	/**
	 * Function to register an object that implements the Controllable interface. It also
	 * set the Initialisable object
	 * @param a The Controllable object
	 */
	public void registerControllable(Controllable a)
	{
		controllable = a;
		registerInitialisable(a);
	}
	
	/**
	 * Function to register an object that implements the Initialisable interface
	 * @param a The Initialisable object
	 */
	public void registerInitialisable(Initialisable a)
	{
		initialisable = a;
	}
	
}
