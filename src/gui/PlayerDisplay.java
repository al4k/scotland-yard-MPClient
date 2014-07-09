package gui;

import gui.PlayerDisplayInfo.PlayerType;

import java.awt.*;
import java.util.*;

import javax.swing.*;

public class PlayerDisplay extends JPanel {

	HashMap<Integer, PlayerPanel> displayedPlayers;	
	
	
	public PlayerDisplay() 
	{
		//this.title = title;
		displayedPlayers = new HashMap<Integer, PlayerPanel>();
		setBackground(new Color(220,220,220));
		
	}
	
	public boolean hasPlayer(int id)
	{
		return displayedPlayers.containsKey(id);
	}
	
	
	public void addPlayer(PlayerDisplayInfo player)
	{
		PlayerPanel panel = null;
		if(player.getType() == PlayerType.MrX) panel = new MrXPanel(player);
		else panel = new DetectivePanel(player);
		
		panel.addMouseListener(new PlayerPanelHoverListener(panel));
		displayedPlayers.put(player.getId(), panel);
		
		
		add(panel);
	}
	
	public void updatePlayer(PlayerDisplayInfo player)
	{
		
		//displayedPlayers.get(player.getId()).setPlayerInfo(player);
		//System.out.println("Input2: " + player.location);
	}
		
	
	public void clear()
	{
		for(int key : displayedPlayers.keySet())
		{
			remove(displayedPlayers.get(key));
		}
		displayedPlayers.clear();
		
	}
	
	public void update()
	{
		for(int key : displayedPlayers.keySet())
		{
			displayedPlayers.get(key).revalidate();
			displayedPlayers.get(key).repaint();
		}
	}
	
	
	
	public void redo()
	{
		for(int key: displayedPlayers.keySet())
		{
			PlayerPanel player = displayedPlayers.get(key);
			//player.addMouseListener(new PlayerPanelHoverListener(player));
			add(displayedPlayers.get(key));
			player.revalidate();
			player.repaint();
		}
	}
	
	public void setUp()
	{	
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);		
	}
}
