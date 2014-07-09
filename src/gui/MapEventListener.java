package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import state.Point;

public class MapEventListener implements MouseMotionListener, MouseListener
{
	GUI gui;
	
	public MapEventListener(GUI gui)
	{
		this.gui = gui;
	}
	
	public void mouseMoved(MouseEvent me)
	{
		Point loc = new Point(me.getX(), me.getY());
		gui.mapMouseHoverEvent(loc);
	}


	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void mouseClicked(MouseEvent e) {
		Point loc = new Point(e.getX(), e.getY());
		gui.mapClickEvent(loc);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
	
		// remove the hover marker
		MapPanel map = (MapPanel) e.getComponent();
		map.removeHoverMarker();
		map.revalidate();
		map.repaint();
		
	}
	

}
