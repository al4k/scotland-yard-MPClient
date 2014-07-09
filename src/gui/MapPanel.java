package gui;
import game.TextOutput;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import state.Point;

/**
 * Class to handle the drawing of the map image
 */
public class MapPanel extends JPanel 
{
	Dimension       mapDimensions;
	BufferedImage   mapImage;
	List<PlayerLocationReadable> players = new ArrayList<PlayerLocationReadable>();


	// data for drawing the player markers to the map
	class Marker
	{
		public int opactiy;
		public Color colour;
		public Point location;

		public Marker(Color c, Point p, int o)
		{
			opactiy = o;
			colour = c;
			location = p;
		}
	}

	HashMap<Integer,Marker> markersToDraw = new HashMap<Integer,Marker>();
	Marker hoverMarker;



	/**
	 * Constructor for the gui map class
	 */
	public MapPanel(String mapFilename)
	{
		// load the map
		mapImage = loadMapImage(mapFilename);
		mapDimensions = new Dimension(mapImage.getWidth(), mapImage.getHeight());
		setPreferredSize(mapDimensions);
	}

	public Dimension getMapDimensions()
	{
		return mapDimensions;
	}

	/**
	 * Function to load the map image
	 * @param filename The filename of the map
	 * @return The loaded buffered image
	 */
	public BufferedImage loadMapImage(String filename)
	{
		BufferedImage im= null;
		try
		{
			im = ImageIO.read(new File(filename));
		}
		catch(IOException e)
		{
			String message = String.format("Caught IO Exception in loadMapImage(): %s", e.getMessage());
			TextOutput.printStandardLn(message);
			System.exit(1);
		}

		return im;
	}

	public void clearMarkers()
	{
		markersToDraw.clear();
	}

	public void addPlayer(PlayerLocationReadable player)
	{
		players.add(player);
	}
	
	public void setHoverMarker(Color color, Point location, int opacity)
	{
		hoverMarker = new Marker(color, location, opacity);
	}
	
	public void removeHoverMarker()
	{
		hoverMarker = null;
	}

	private void drawMapMarker(Graphics2D graphics, Marker m)
	{
		if(m.location == null)
			return;
		
		Color newColor = new Color(
				m.colour.getRed(), 
				m.colour.getGreen(), 
				m.colour.getBlue(), 
				m.opactiy);
		
		Color background = new Color(
				255, 255, 255,
				m.opactiy);

		int circleSize = 40;
		int offset = circleSize / 2;

		graphics.setPaint(background);
		
		BasicStroke stroke = new BasicStroke(13);
		graphics.setStroke(stroke);
		
		graphics.drawOval(
				m.location.x()-offset, 
				m.location.y()-offset,
				circleSize, circleSize);
		
		stroke = new BasicStroke(10);
		graphics.setStroke(stroke);
		graphics.setPaint(newColor);
		graphics.drawOval(
				m.location.x()-offset, 
				m.location.y()-offset,
				circleSize, circleSize);
		
		

	}

	public void paintComponent(Graphics go)
	{
		super.paintComponent(go);
		Graphics2D g2d = (Graphics2D) go;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
		rh.put(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		g2d.drawImage(mapImage, 0, 0, null);
		g2d.setRenderingHints(rh);
		
		
		// paint the side
		
		
		
		// draw the markers
		for(PlayerLocationReadable player : players)
		{
			Marker marker = null;
			if(player.isActive())
			{
				marker  = new Marker(player.getMarkerColour(), player.getPlayerMapLocation(), 255);
			}
			else
			{
				marker  = new Marker(player.getMarkerColour().darker().darker(), player.getPlayerMapLocation(), 200);
			}
			drawMapMarker(g2d, marker);
		}
		
		if(hoverMarker != null)
		{
			//System.out.println("Printing Hover");
			drawMapMarker(g2d, hoverMarker);
		}


		Color c1 = new Color(0, 0, 0, 200);
		Color c2 = new Color(50, 50, 50, 0);
		GradientPaint gp = new GradientPaint(0, 0, c1, 15, 0, c2);
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, 15, this.getHeight());
		
	}

}
