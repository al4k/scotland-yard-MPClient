package gui;

import gui.IconProvider.IconType;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import state.Initialisable.TicketType;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;

public class TakeMoveDialog extends JDialog implements ActionListener {

	PlayerDisplayInfo playerInfo;
	JPanel panel;
	JButton taxiButton;
	JButton busButton;
	JButton undergroundButton;
	JButton secretButton;
	JButton doubleButton;
	JButton cancelButton;
	JLabel infoLabel;
	
	private static final int DIALOG_WIDTH = 450;


	TicketType answer;
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(cancelButton == e.getSource()) answer = null;
		else if(busButton == e.getSource()) answer = TicketType.Bus;
		else if(taxiButton == e.getSource()) answer = TicketType.Taxi;
		else if(undergroundButton == e.getSource()) answer = TicketType.Underground;
		else if(secretButton == e.getSource()) answer = TicketType.SecretMove;
		else if(doubleButton == e.getSource()) answer = TicketType.DoubleMove;
		
		setVisible(false);
	}
	
	public TicketType answer()
	{
		return answer;
	}
	
	public static TicketType getMove(JFrame frame, PlayerDisplayInfo playerInfo, String info)
	{
		TakeMoveDialog dialog = new TakeMoveDialog(frame, playerInfo, info);
		return dialog.answer();
	}
	
	public TakeMoveDialog(JFrame frame, PlayerDisplayInfo playerInfo, String info)
	{
		super(frame, true);
		this.playerInfo  = playerInfo;
		
		int bigButtonDims = DIALOG_WIDTH / 3;
		setPreferredSize(new Dimension(DIALOG_WIDTH+30, bigButtonDims+50+60+20));
		
		
		
		// create the main panel of the dialog
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		
		add(panel);
		
		infoLabel = new JLabel(info);
		panel.add(infoLabel);
		int t = playerInfo.getTickets(TicketType.Taxi);
		int b = playerInfo.getTickets(TicketType.Bus);
		int u = playerInfo.getTickets(TicketType.Underground);
		int s = playerInfo.getTickets(TicketType.SecretMove);
		int d = playerInfo.getTickets(TicketType.DoubleMove);
		
		
		
		// create the three big buttons
		JPanel bigButtonPanel = new JPanel();
		bigButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bigButtonPanel.setBackground(new Color(200,200,200));
		bigButtonPanel.setLayout(new BoxLayout(bigButtonPanel, BoxLayout.X_AXIS));
		
		
		taxiButton        = new JButton(IconProvider.getIcon(IconType.Taxi, t));
				busButton         = new JButton(IconProvider.getIcon(IconType.Bus, b));
		undergroundButton = new JButton(IconProvider.getIcon(IconType.Underground, u));
		
		if(t == 0) taxiButton.setEnabled(false);
		if(u == 0) undergroundButton.setEnabled(false);
		if(b == 0) busButton.setEnabled(false);
		
		
		
		taxiButton.setMaximumSize(new Dimension(bigButtonDims, bigButtonDims));
		busButton.setMaximumSize(new Dimension(bigButtonDims, bigButtonDims));
		undergroundButton.setMaximumSize(new Dimension(bigButtonDims, bigButtonDims));
		
		taxiButton.addActionListener(this);
		busButton.addActionListener(this);
		undergroundButton.addActionListener(this);
		
		bigButtonPanel.add(taxiButton);
		bigButtonPanel.add(busButton);
		bigButtonPanel.add(undergroundButton);
		
		JPanel secretButtonPanel = new JPanel();
		secretButtonPanel.setLayout(new BoxLayout(secretButtonPanel, BoxLayout.X_AXIS));
		secretButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		secretButtonPanel.setBackground(new Color(150,150,150));
		
		secretButton = new JButton("Secret Move", IconProvider.getIcon(IconType.Lock, 30, 30));
		doubleButton = new JButton("Double Move", IconProvider.getIcon(IconType.Times2, 30, 30));
		cancelButton = new JButton("Cancel");
		
		if(s==0) secretButton.setEnabled(false);
		if(d==0) doubleButton.setEnabled(false);
		doubleButton.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 12));
		secretButton.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 12));
		cancelButton.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 16));
		
		secretButton.setMaximumSize(new Dimension(bigButtonDims, 50));
		doubleButton.setMaximumSize(new Dimension(bigButtonDims, 50));
		cancelButton.setMaximumSize(new Dimension(bigButtonDims, 50));
		
		doubleButton.addActionListener(this);
		secretButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		
		secretButtonPanel.add(secretButton);
		secretButtonPanel.add(doubleButton);
		secretButtonPanel.add(cancelButton);
		
		
		
		panel.add(bigButtonPanel);
		panel.add(secretButtonPanel);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}
	
}
