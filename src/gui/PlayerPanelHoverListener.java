package gui;

import game.TextOutput;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlayerPanelHoverListener  implements MouseListener{

	PlayerPanel caller;
	
	public PlayerPanelHoverListener(PlayerPanel caller)
	{
		this.caller = caller;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		caller.setHover(true);	
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		caller.setHover(false);
		repaint();
	}
	
	
	private void repaint()
	{
		caller.revalidate();
		caller.repaint();
	}

}
