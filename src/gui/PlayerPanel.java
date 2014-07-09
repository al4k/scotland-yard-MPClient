package gui;

import java.util.*;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

public abstract class PlayerPanel extends JPanel {
	PlayerInformationReadable player;
	protected JLabel idLabel;
	protected JLabel locationLabel;
	protected Font textFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	protected List<JLabel> labelList = new ArrayList<JLabel>();
	protected boolean hovered = false;
	
	public PlayerPanel(PlayerInformationReadable playerInfo)
	{
		player = playerInfo;
		
		
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(new Color(100,100,100));
		
		// create the labels
		idLabel = new JLabel();
		labelList.add(idLabel);
		locationLabel = new JLabel();
		labelList.add(locationLabel);
		
		
	}
	
	public void setHover(boolean value)
	{
		hovered = value;
	}
	
		
	protected void paintComponent(Graphics go)
	{
		super.paintComponent(go);
		
		// paint the background onto the panel
		Graphics2D g2d = (Graphics2D) go;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
        Color main = player.getPlayerColour();
		Color second = main.darker();
		if(hovered)
		{
			main = main.brighter();
		}
		

		int w = getWidth();
		int h = getHeight();
		//System.out.println(player.id);
		
		g2d.setPaint(player.getPreviousColour().darker());
		g2d.fillRect(0, 0, w, h);
		
		GradientPaint gp = new GradientPaint(0, 0, main, 0, h, second);
		g2d.setPaint(gp);
		
		RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, w, h, 30, 30);
		
		
		g2d.fill(rect);
		g2d.fillRect(0, 0, 50, h);
		g2d.fillRect(0, h-20, w, 20);
		
		RoundRectangle2D rect2 = new RoundRectangle2D.Float(-10,5, 240, h, 30,30);
		g2d.setPaint(player.getPlayerColour().darker());
		g2d.fill(rect2);
		
       
		if(player.isCurrentTurn() || !player.isActive())
		{
			
			if(!hovered) 
			{
				g2d.setColor(main);
			}
			else
			{
				g2d.setColor(main.brighter());
			}
			g2d.fillOval(246, 8, 24, 24);
			if(player.isActive())
			{
				g2d.setColor(Color.WHITE);
			}
			else
			{
				g2d.setColor(Color.BLACK);
			}
			g2d.fillOval(248, 10, 20, 20);
		}
        
		
	}
	
	
	
}
