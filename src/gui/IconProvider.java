package gui;
import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import state.Initialisable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class IconProvider {
	private static final IconProvider INSTANCE = new IconProvider();
	
	private static final String IMAGE_PATH           = "../resources/images/";
	private static final String BUS_FILENAME         = IMAGE_PATH + "bus.png";
	private static final String TAXI_FILENAME        = IMAGE_PATH + "taxi.png";
	private static final String UNDERGROUND_FILENAME = IMAGE_PATH + "underground.png";
	private static final String DETECTIVE_FILENAME   = IMAGE_PATH + "detective.png";
	private static final String CRIMINAL_FILENAME    = IMAGE_PATH + "criminal.png";
	private static final String LOCK_FILENAME        = IMAGE_PATH + "lock.png";
	private static final String TIMES2_FILENAME      = IMAGE_PATH + "times2.png";
	
	public enum IconType { Bus, Taxi, Underground, Detective, Criminal, Lock, Times2 };
	
	private HashMap<IconType, ImageIcon> icons = new HashMap<IconType, ImageIcon>();
	
	public static IconType ticketTypeToIconType(Initialisable.TicketType type)
	{
		IconType output = null;
		switch(type)
		{
		case Underground:
			output = IconType.Underground;
			break;
		case Taxi:
			output = IconType.Taxi;
			break;
		case Bus:
			output = IconType.Bus;
			break;
		case DoubleMove:
			output = IconType.Times2;
			break;
		case SecretMove:
			output = IconType.Lock;
			break;
		default:
			break;
		}
		
		return output;
	}
	
	
	private IconProvider()
	{
		if(INSTANCE != null)
		{
			throw new IllegalStateException("Already instantiated");
		}
		
		// load up the images
		icons.put(IconType.Bus, loadIcon(BUS_FILENAME));
		icons.put(IconType.Taxi, loadIcon(TAXI_FILENAME));
		icons.put(IconType.Underground, loadIcon(UNDERGROUND_FILENAME));
		icons.put(IconType.Detective, loadIcon(DETECTIVE_FILENAME));
		icons.put(IconType.Criminal, loadIcon(CRIMINAL_FILENAME));
		icons.put(IconType.Lock, loadIcon(LOCK_FILENAME));
		icons.put(IconType.Times2, loadIcon(TIMES2_FILENAME));
	}
	
	public static ImageIcon getIcon(IconType type)
	{
		return INSTANCE.icons.get(type);
	}
	
	public static ImageIcon getIcon(IconType type, int x, int y)
	{
		ImageIcon scaled = new ImageIcon(INSTANCE.getScaledImage(INSTANCE.icons.get(type).getImage(), x, y));
		return scaled;
	}
	
	public static ImageIcon getIcon(IconType type, int num)
	{
		ImageIcon written = new ImageIcon(INSTANCE.getWrittenImage(INSTANCE.icons.get(type).getImage(), num));
		return written;
	}
	
	private Image getWrittenImage(Image srcImg, int num)
	{
		int width = srcImg.getWidth(null);
		int height = srcImg.getHeight(null);
		BufferedImage drawnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = drawnImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.drawImage(srcImg, 0, 0, width, height, null);
        
        g2.setColor(Color.BLACK);
        // put the cricle in
        g2.fillOval(0, 0, 30, 30);
       
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
       
        
        String numStr = Integer.toString(num);
        int ypos = 3;
        if(numStr.length() == 1)
        {
        	ypos +=6;
        }
        
        g2.drawString(Integer.toString(num), ypos, 21);
        g2.dispose();
        return drawnImage;
        
	}
	
	private ImageIcon loadIcon(String filename)
	{
		return new ImageIcon(filename);
	}
	
	
	private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
	
	
	
	

	
}
