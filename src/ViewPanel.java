import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class ViewPanel extends JPanel implements ActionListener{

	private Thread thread; //new thread for playing notes
	private final JFileChooser fileChooser = new JFileChooser();
	
	public ViewPanel()
	{
		setBackground(Color.WHITE);
		fileChooser.addChoosableFileFilter(new TxtFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	
	//display document model's data
	public void paintComponent(Graphics g) {
        super.paintComponent(g);       

        g.setFont(new Font("Courier New", Font.BOLD, 16));
        
        Main.tabModel.draw(g);
    }
	
	//called by document model's play method and filehandler's open
	public void redraw()
	{
		repaint();
	}
	
	//actions for buttons and menus
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand() == "play")
		{
			//Toggle to pause button
			JButton button = (JButton) e.getSource();
			URL imageURL = getClass().getResource("pause.png");
			button.setToolTipText("Pause");
			button.setActionCommand("pause");
			
			if(imageURL != null)
			{
				button.setIcon(new ImageIcon(imageURL, "Pause"));
			}
			else
			{
				button.setText("Pause");
			}
			
			//disable actions besides pause
			MainPanel m = (MainPanel)this.getParent();
			m.disableButtons();
			
			//start new thread for playing notes while still being able to
			//use the pause button and repaint the component
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					Main.tabModel.setPlaying(true);
					Main.tabModel.play();					
				}
			};
			
			thread = new Thread(runnable);
			thread.start();
		}
		else if(e.getActionCommand() == "pause")
		{
			Main.tabModel.setPlaying(false);
			try {
				thread.interrupt();
				Main.midiPlayer.stopSounds();
				thread.join();
			} catch (InterruptedException e1) {
				//e1.printStackTrace();
			}
		}
		else if(e.getActionCommand() == "rewind")
		{
			Main.tabModel.rewind();
			repaint();
		}
		else if(e.getActionCommand() == "+note")
		{
			Object[] choices = {1, 2, 3, 4, 5, 6};
			Integer string = (Integer) JOptionPane.showInputDialog
												(Main.frame,
												 "Which string?",
												 "Place Note",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 choices,
												 1);
															
			if(string != null)
			{
				Object[] choices2 = new Object[23];
				for(int i = 0; i < 23; i++)
				{
					choices2[i] = i;
				}
				Integer fret = (Integer) JOptionPane.showInputDialog
													(Main.frame,
													 "Which fret?",
													 "Place Note",
													 JOptionPane.PLAIN_MESSAGE,
													 null,
													 choices2,
													 0);
				
				if(fret != null)
				{
					Main.tabModel.placeNote(new Note(string, fret));
					repaint();
				}
			}
		}
		else if(e.getActionCommand() == "-note")
		{	
			Object[] choices = {1, 2, 3, 4, 5, 6};
			Integer string = (Integer) JOptionPane.showInputDialog
												(Main.frame,
												 "Which string?",
												 "Remove Note",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 choices,
												 1);
															
			if(string != null)
			{
				Main.tabModel.removeNote(string);
				repaint();
			}
		}
		else if(e.getActionCommand() == "+measure")
		{
			Main.tabModel.addMeasure();
			repaint();
		}
		else if(e.getActionCommand() == "-measure")
		{
			int response = JOptionPane.showConfirmDialog
								(Main.frame, 
								"Are you sure you want to delete this measure?",
								"Remove Measure",
								JOptionPane.YES_NO_OPTION);
			
			if(response == JOptionPane.YES_OPTION)
			{
				Main.tabModel.removeMeasure();
				repaint();
			}
		}
		else if(e.getActionCommand() == "+position")
		{
			Object[] choices = {NoteValue.Whole, NoteValue.Half, NoteValue.Quarter, 
					NoteValue.Eighth, NoteValue.Sixteenth, NoteValue.ThirtySecond};
			NoteValue noteValue = (NoteValue) JOptionPane.showInputDialog
												(Main.frame,
												 "Pick the note value of the measure position.",
												 "Add Measure Position",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 choices,
												 NoteValue.Quarter);
															
			if(noteValue != null)
			{
				Main.tabModel.addPosition(noteValue);
				repaint();
			}
		}
		else if(e.getActionCommand() == "-position")
		{
			int response = JOptionPane.showConfirmDialog
					(Main.frame, 
					"Are you sure you want to delete this position?",
					"Remove Position",
					JOptionPane.YES_NO_OPTION);

			if(response == JOptionPane.YES_OPTION)
			{
				Main.tabModel.removePosition();
				repaint();
			}
		}
		else if(e.getActionCommand() == "beat")
		{
			Object[] choices = {NoteValue.Whole, NoteValue.Half, NoteValue.Quarter, 
					NoteValue.Eighth, NoteValue.Sixteenth, NoteValue.ThirtySecond};
			NoteValue noteValue = (NoteValue) JOptionPane.showInputDialog
												(Main.frame,
												 "Pick the note value of the measure position.",
												 "Set Measure Position Beat",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 choices,
												 NoteValue.Quarter);
															
			if(noteValue != null)
			{
				Main.tabModel.setPositionBeat(noteValue);
				repaint();
			}
		}
		else if(e.getActionCommand() == "left")
		{
			Main.tabModel.moveLeftPosition();
			repaint();
		}
		else if(e.getActionCommand() == "right")
		{
			Main.tabModel.moveRightPosition();
			repaint();
		}
		else if(e.getActionCommand() == "up")
		{
			Main.tabModel.prevMeasure();
			repaint();
		}
		else if(e.getActionCommand() == "down")
		{
			Main.tabModel.nextMeasure();
			repaint();
		}
		else if(e.getActionCommand() == "new")
		{
			int response = JOptionPane.showConfirmDialog
					(Main.frame, 
					"Are you sure? Unsaved work will be lost.",
					"New File",
					JOptionPane.YES_NO_OPTION);

			if(response == JOptionPane.YES_OPTION)
			{
				Main.tabModel = new DocumentModel();
				Main.midiPlayer.reInit();
				repaint();
			}
		}
		else if(e.getActionCommand() == "open")
		{
			int response = JOptionPane.showConfirmDialog
					(Main.frame, 
					"Are you sure? Unsaved work will be lost.",
					"Open File",
					JOptionPane.YES_NO_OPTION);

			if(response == JOptionPane.YES_OPTION)
			{
				int fileResponse = fileChooser.showOpenDialog(Main.frame);
				
				if(fileResponse == fileChooser.APPROVE_OPTION)
				{
					File file = fileChooser.getSelectedFile();
					
					String fileName = file.getName();
					//Check if txt extension is present, add it if no
					if(fileName.length() > 3)
					{
						if(!fileName.substring(fileName.length()-4).equalsIgnoreCase(".txt"))
						{
							file = new File(file.toString() + ".txt");
						}
					}
					else
					{
						file = new File(file.toString() + ".txt");
					}
					
					FileHandler.openFile(file);
				}
			}
		}
		else if(e.getActionCommand() == "save")
		{
			int fileResponse = fileChooser.showSaveDialog(Main.frame);
			
			if(fileResponse == fileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();

				String fileName = file.getName();
				//Check if txt extension is present, add it if no
				if(fileName.length() > 3)
				{
					if(!fileName.substring(fileName.length()-4).equalsIgnoreCase(".txt"))
					{
						file = new File(file.toString() + ".txt");
					}
				}
				else
				{
					file = new File(file.toString() + ".txt");
				}
				
				FileHandler.saveFile(file);
			}
		}
		else if(e.getActionCommand() == "bpm")
		{
			String bpm = (String) JOptionPane.showInputDialog
												(Main.frame,
												 "Input bpm. (1-999)",
												 "Set BPM",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 null,
												 null);
			
			if(bpm != null)
			{
				try
				{
					int bpmNum = Integer.parseInt(bpm);
					
					if(bpmNum >= 1 && bpmNum <= 999)
					{
						Main.midiPlayer.setBPM(bpmNum);
						Main.tabModel.setBPM(bpmNum);
						repaint();
					}
					else
					{
						JOptionPane.showMessageDialog(Main.frame, "Invalid bpm.");
					}
				}
				catch(NumberFormatException exception)
				{
					JOptionPane.showMessageDialog(Main.frame, "Invalid bpm.");
				}
			}
		}
		else if(e.getActionCommand() == "guitar")
		{
			Object[] choices = {Guitar.Nylon, Guitar.Steel, Guitar.Jazz, Guitar.Clean,
					 			Guitar.Muted};
			Guitar guitar = (Guitar) JOptionPane.showInputDialog
												(Main.frame,
												 "Which guitar?",
												 "Change Guitar",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 choices,
												 Guitar.Clean);
															
			if(guitar != null)
			{
				Main.midiPlayer.changeInstrument(guitar);
			}
		}
		else if(e.getActionCommand() == "tuning")
		{
			Object[] choices = {1, 2, 3, 4, 5, 6};
			Integer string = (Integer) JOptionPane.showInputDialog
												(Main.frame,
												 "Which string?",
												 "Tuning",
												 JOptionPane.PLAIN_MESSAGE,
												 null,
												 choices,
												 1);
															
			if(string != null)
			{
				Object[] choices2 = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
				
				String note = (String) JOptionPane.showInputDialog
													(Main.frame,
													 "Which note?",
													 "Tuning",
													 JOptionPane.PLAIN_MESSAGE,
													 null,
													 choices2,
													 "C");
				
				if(note != null)
				{
					Object[] choices3 = {0, 1, 2, 3, 4, 5, 6, 7, 8};
					
					Integer octave = (Integer) JOptionPane.showInputDialog
														(Main.frame,
														 "Which octave?",
														 "Tuning",
														 JOptionPane.PLAIN_MESSAGE,
														 null,
														 choices3,
														 0);
					
					if(octave != null)
					{
						Main.midiPlayer.tuneString(string, note+octave);
						Main.tabModel.tuneString(string, note+octave);
						repaint();
					}
				}
			}
		}
	}

}
