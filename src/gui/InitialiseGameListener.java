package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InitialiseGameListener extends MouseAdapter {
	
	GUI gui;
	
	public InitialiseGameListener(GUI gui)
	{
		this.gui = gui;
	}
	
	public void mousePressed(MouseEvent me)
	{
		gui.initialiseNewGameEvent();
	}
	
}
