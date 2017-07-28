import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToolBar;


public class MainPanel extends JPanel implements WindowListener{

	public ViewPanel viewPane;
	private JToolBar toolbar;
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	
	public MainPanel()
	{
		super(new BorderLayout());
		init();
	}
	
	private void init()
	{
		//size of panel
		setPreferredSize(new Dimension(1000, 500));
		
		//Set up main view
		viewPane = new ViewPanel();
		add(viewPane, BorderLayout.CENTER);
		
		//Set up the toolbar
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		JPanel playerPanel = playerPanelSetup();
		JPanel editorPanel = editorPanelSetup();
		JPanel navPanel = navPanelSetup();
		
		toolbar.add(playerPanel);
		right.add(editorPanel);
		right.add(navPanel);
		toolbar.add(right);
		
		add(toolbar, BorderLayout.PAGE_START);
	}
	
	private JPanel playerPanelSetup()
	{
		JPanel playerPanel = new JPanel();
		playerPanel.setBorder(BorderFactory.createEmptyBorder(10,5,0,100));
		
		URL imageURL = getClass().getResource("play.png");
		
		JButton button = new JButton();
		button.setToolTipText("Play");
		button.setActionCommand("play");
		button.addActionListener(viewPane);
		if(imageURL != null)
		{
			button.setIcon(new ImageIcon(imageURL, "Play"));
		}
		else
		{
			button.setText("Play");
		}
		buttons.add(button);
		playerPanel.add(button);
		playerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		imageURL = getClass().getResource("rewind.png");
		
		button = new JButton();
		button.setToolTipText("Rewind");
		button.setActionCommand("rewind");
		button.addActionListener(viewPane);
		if(imageURL != null)
		{
			button.setIcon(new ImageIcon(imageURL, "Rewind"));
		}
		else
		{
			button.setText("Rewind");
		}
		buttons.add(button);
		playerPanel.add(button);
		
		return playerPanel;
	}
	
	private JPanel editorPanelSetup()
	{
		JPanel editorPanel = new JPanel();
		editorPanel.setBorder(BorderFactory.createEmptyBorder(0,22,0,10));
		
		JButton button = new JButton("+ Note");
		button.setToolTipText("Note Placing Mode");
		button.setActionCommand("+note");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		button = new JButton("- Note");
		button.setToolTipText("Note Erasing Mode");
		button.setActionCommand("-note");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		editorPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		button = new JButton("+ Measure");
		button.setToolTipText("Add Measure");
		button.setActionCommand("+measure");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		button = new JButton("- Measure");
		button.setToolTipText("Remove Measure");
		button.setActionCommand("-measure");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		editorPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		button = new JButton("+ Position");
		button.setToolTipText("Add Measure Position");
		button.setActionCommand("+position");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		button = new JButton("- Position");
		button.setToolTipText("Remove Measure Position");
		button.setActionCommand("-position");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		button = new JButton("Beat");
		button.setToolTipText("Set Measure Position Beat");
		button.setActionCommand("beat");
		button.addActionListener(viewPane);
		buttons.add(button);
		editorPanel.add(button);
		
		return editorPanel;
	}
	
	private JPanel navPanelSetup()
	{
		JPanel navPanel = new JPanel();
		navPanel.setBorder(BorderFactory.createEmptyBorder(0,400,0,10));
		
		URL imageURL = getClass().getResource("left.png");
		
		JButton button = new JButton();
		button.setToolTipText("Previous Measure Position");
		button.setActionCommand("left");
		button.addActionListener(viewPane);
		if(imageURL != null)
		{
			button.setIcon(new ImageIcon(imageURL, "Left"));
		}
		else
		{
			button.setText("Left");
		}
		buttons.add(button);
		navPanel.add(button);
		
		imageURL = getClass().getResource("right.png");
		
		button = new JButton();
		button.setToolTipText("Next Measure Position");
		button.setActionCommand("right");
		button.addActionListener(viewPane);
		if(imageURL != null)
		{
			button.setIcon(new ImageIcon(imageURL, "Right"));
		}
		else
		{
			button.setText("Right");
		}
		buttons.add(button);
		navPanel.add(button);
		
		navPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		
		imageURL = getClass().getResource("up.png");
		
		button = new JButton();
		button.setToolTipText("Previous Measure");
		button.setActionCommand("up");
		button.addActionListener(viewPane);
		if(imageURL != null)
		{
			button.setIcon(new ImageIcon(imageURL, "Up"));
		}
		else
		{
			button.setText("Up");
		}
		buttons.add(button);
		navPanel.add(button);
		
		imageURL = getClass().getResource("down.png");
		
		button = new JButton();
		button.setToolTipText("Next Measure");
		button.setActionCommand("down");
		button.addActionListener(viewPane);
		if(imageURL != null)
		{
			button.setIcon(new ImageIcon(imageURL, "Down"));
		}
		else
		{
			button.setText("Down");
		}
		buttons.add(button);
		navPanel.add(button);
		
		return navPanel;
	}
	
	//disable buttons while tab is playing, only pause is allowed
	public void disableButtons()
	{
		for(int i = 0; i < buttons.size(); i++)
		{
			if(!buttons.get(i).getActionCommand().equals("pause"))
			{
				buttons.get(i).setEnabled(false);
			}
		}
		
		for(int i = 0; i < Main.menus.size(); i++)
		{
			Main.menus.get(i).setEnabled(false);
		}
	}
	
	public void enableButtons()
	{
		for(int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).setEnabled(true);
		}
		
		for(int i = 0; i < Main.menus.size(); i++)
		{
			Main.menus.get(i).setEnabled(true);
		}
	}
	
	//when tab is done playing, restore play button and enable other functionality again
	public void donePlaying()
	{
		for(int i = 0; i< buttons.size(); i++)
		{
			JButton currButton = buttons.get(i);
			
			if(currButton.getActionCommand().equals("pause"))
			{
				URL imageURL = getClass().getResource("play.png");
				currButton.setToolTipText("Play");
				currButton.setActionCommand("play");
				
				if(imageURL != null)
				{
					currButton.setIcon(new ImageIcon(imageURL, "Play"));
				}
				else
				{
					currButton.setText("Play");
				}
				
				break;
			}
		}
		
		enableButtons();
	}
	
	//WindowListener
	@Override
	public void windowClosing(WindowEvent arg0) {
		
		int response = JOptionPane.showConfirmDialog
				(Main.frame, 
				"Are you sure? Unsaved work will be lost.",
				"Exit",
				JOptionPane.YES_NO_OPTION);

		if(response == JOptionPane.YES_OPTION)
		{
			Main.midiPlayer.onClose();
			System.exit(0);			
		}
	}
	
	//----------------Unused WindowListener methods----------
	@Override
	public void windowClosed(WindowEvent evt) {}
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
