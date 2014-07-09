package gui;

import game.TextOutput;
import gui.IconProvider.IconType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JLabel;

import state.Initialisable;
import state.Player;

public class MrXPanel extends PlayerPanel {

	// layout fields
	private Box mainBox;
	JLabel secretTicketLabel;
	JLabel doubleTicketLabel;
	JLabel busTicketLabel;
	JLabel undergroundTicketLabel;
	JLabel taxiTicketLabel;

	int[] mrXShowing = {3,8,13,18,24};

	public MrXPanel(PlayerInformationReadable player)
	{
		super(player);

		// create the labels
		// initialise the new layout objects
		taxiTicketLabel        = new JLabel();
		labelList.add(taxiTicketLabel);
		busTicketLabel         = new JLabel();
		labelList.add(busTicketLabel);
		undergroundTicketLabel = new JLabel();
		labelList.add(undergroundTicketLabel);
		secretTicketLabel      = new JLabel();
		labelList.add(secretTicketLabel);
		doubleTicketLabel       = new JLabel();
		labelList.add(doubleTicketLabel);

		setBackground(player.getPlayerColour());

		setMaximumSize(new Dimension(280, 210));

	
	}


	protected void paintComponent(Graphics go)
	{
		super.paintComponent(go);
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
		
		int numberOfMoves = player.getTicketHistory().size();
		//System.out.print("Length: " + player.playerTicketHistory.size());

		g2d.setColor(Color.WHITE);
		Font numberFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
		g2d.setFont(numberFont);

		int basex = 10;
		int xinc  = 52;
		int basey = 35;
		int yinc  = 30;
		int count = 1;
		for(int i = 0; i < 4; i++)
		{
			int xpos = basex + (xinc * i);
			for(int j = 0; j < 6; j++)
			{
				int ypos = basey + (yinc*j);

				// write the text

				
				g2d.drawString(Integer.toString(count), xpos, ypos);
				

				// create the icon surround
				if(!hovered) 
				{
					g2d.setColor(main);
				}
				else
				{
					g2d.setColor(main.brighter());
				}
				g2d.fillOval(xpos+18, ypos-17, 29, 29);
				if(Player.mrXAppearanceList[count] == true)
					g2d.setColor(Color.WHITE);
				else
					g2d.setColor(Color.GRAY);
				
				g2d.fillOval(xpos+20, ypos-15, 25, 25);
				
				g2d.setColor(Color.WHITE);
				
				
				if(count-1 < numberOfMoves)
				{
					//TextOutput.printDebug(String.format("Drawing %d %d\n", count, numberOfMoves));
					Initialisable.TicketType ttype = player.getTicketHistory().get(count-1);
					IconProvider.IconType itype = IconProvider.ticketTypeToIconType(ttype);
					g2d.drawImage(IconProvider.getIcon(itype, 24, 23).getImage(), xpos+20, ypos-14, 24,23, null);
				}
				
				count++;

			}
		}
	}







}
