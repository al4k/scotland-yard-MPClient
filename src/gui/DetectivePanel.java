package gui;

import gui.IconProvider.IconType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import state.Initialisable;

public class DetectivePanel extends PlayerPanel {
		
	// layout fields
	private Box mainBox;
	JLabel busTicketLabel;
	JLabel undergroundTicketLabel;
	JLabel taxiTicketLabel;

	
	
	/**
	 * Contructor. Takes a valid player object
	 * @param player
	 */
	public DetectivePanel(PlayerInformationReadable player)
	{
		super(player);
		
		// initialise the new layout objects
		taxiTicketLabel        = new JLabel();
		labelList.add(taxiTicketLabel);
		busTicketLabel         = new JLabel();
		labelList.add(busTicketLabel);
		undergroundTicketLabel = new JLabel();
		labelList.add(undergroundTicketLabel);
		
		setMaximumSize(new Dimension(280, 100));
		setBackground(new Color(100,100,100));
		
	

	}
	
	
	
	

	
	protected void paintComponent(Graphics go)
	{
		super.paintComponent(go);
		Graphics2D g2d = (Graphics2D) go;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
				
		// write the data to the panel
		g2d.setColor(player.getPlayerTextColour());
		g2d.drawString("Location: " + Integer.toString(player.getLocation()), 5, 24);
		g2d.drawString("ID: " + Integer.toString(player.getId()), 180, 24);
		
		// draw the bus tickets
		g2d.drawImage(IconProvider.getIcon(IconType.Taxi, 25,25).getImage(), 2, 29, null);
		g2d.drawString(Integer.toString(player.getTicketNumber(Initialisable.TicketType.Taxi)), 35, 47);
	
		g2d.drawImage(IconProvider.getIcon(IconType.Bus, 25,25).getImage(), 2, 51, null);
		g2d.drawString(Integer.toString(player.getTicketNumber(Initialisable.TicketType.Bus)), 35, 69);
	
		g2d.drawImage(IconProvider.getIcon(IconType.Underground, 25,25).getImage(), 3, 73, null);
		g2d.drawString(Integer.toString(player.getTicketNumber(Initialisable.TicketType.Underground)), 35, 91);
		
	}



	
	
	
	
}
