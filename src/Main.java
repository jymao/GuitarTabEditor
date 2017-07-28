import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class Main {

	public static MidiPlayer midiPlayer;
	public static DocumentModel tabModel;
	public static JFrame frame;
	public static MainPanel mainPane;
	public static ArrayList<JMenu> menus = new ArrayList<JMenu>();
	
	public static void main(String[] args) 
	{
		midiPlayer = new MidiPlayer();
		midiPlayer.init();
		
		tabModel = new DocumentModel();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				showGUI();
			}
		});
		
	}

	private static void showGUI() 
	{
		frame = new JFrame("Tab Editor");
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//handled by EditorPanel windowClosing event
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
				
		mainPane = new MainPanel();
		frame.setContentPane(mainPane);
		frame.addWindowListener(mainPane);
		
		frame.pack();
		frame.setLocationRelativeTo(null); //center frame
		frame.setVisible(true);
		
		setUpMenus();
	}
	
	private static void setUpMenus()
	{
		//Set up menu bar and menus
		JMenuBar menuBar = new JMenuBar();
		
		//Menu for file handling
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem item = new JMenuItem("New");
		item.setActionCommand("new");
		item.addActionListener(mainPane.viewPane);
		fileMenu.add(item);
		item = new JMenuItem("Open");
		item.setActionCommand("open");
		item.addActionListener(mainPane.viewPane);
		fileMenu.add(item);
		item = new JMenuItem("Save");
		item.setActionCommand("save");
		item.addActionListener(mainPane.viewPane);
		fileMenu.add(item);
		
		//Menu for extra tools
		JMenu toolMenu = new JMenu("Tools");
		item = new JMenuItem("BPM");
		item.setActionCommand("bpm");
		item.addActionListener(mainPane.viewPane);
		toolMenu.add(item);
		item = new JMenuItem("Guitar");
		item.setActionCommand("guitar");
		item.addActionListener(mainPane.viewPane);
		toolMenu.add(item);
		item = new JMenuItem("Tuning");
		item.setActionCommand("tuning");
		item.addActionListener(mainPane.viewPane);
		toolMenu.add(item);
		
		menuBar.add(fileMenu);
		menuBar.add(toolMenu);
		
		menus.add(fileMenu);
		menus.add(toolMenu);
		
		frame.setJMenuBar(menuBar);
	}
}
