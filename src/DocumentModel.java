import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class DocumentModel {

	private int bpm = 120;
	private int timeSigTop = 4;
	private NoteValue timeSigBot = NoteValue.Quarter;
	private String[] guitarStrings = {"E4", "B3", "G3", "D3", "A2", "E2"};
	
	private int currMeasure = 0;
	private ArrayList<Measure> measures = new ArrayList<Measure>();
	
	private boolean playing = false;
	
	public DocumentModel()
	{
		measures.add(new Measure(timeSigTop, timeSigBot));
	}
	
	public int getBPM() { return bpm; }
	public int getTimeSigTop() { return timeSigTop; }
	public NoteValue getTimeSigBot() { return timeSigBot; }
	public String[] getStrings() { return guitarStrings; }
	public Measure getCurrMeasure() { return measures.get(currMeasure); }
	public ArrayList<Measure> getMeasures() { return measures; };
	
	public void setPlaying(boolean b) { playing = b; }
	public void setBPM(int bpm) { this.bpm = bpm; }
	
	//go through all measures and their positions and have midiplayer play their notes
	public void play()
	{
		for(int measure = currMeasure; measure < measures.size(); measure++)
		{
			Measure measurePlaying = measures.get(measure);
			ArrayList<MeasurePosition> positions = measurePlaying.getPositions();
			
			for(int position = measurePlaying.getCurrPosition(); position < positions.size(); position++)
			{
				MeasurePosition positionPlaying = positions.get(position);
				Main.midiPlayer.playPosition(positionPlaying.getNotes(), positionPlaying.getNoteValue());

				//user pressed pause. break before moving to next position
				if(!playing)
				{
					break;
				}
				
				moveRightPosition();
				Main.mainPane.viewPane.redraw();
			}
			
			//user pressed pause
			if(!playing)
			{
				break;
			}
			
			nextMeasure();
			Main.mainPane.viewPane.redraw();
		}
		
		Main.mainPane.donePlaying();
	}
	
	//Rewind position to start of document
	public void rewind()
	{
		currMeasure = 0;
		measures.get(currMeasure).moveToStart();
	}
	
	//place note at current measure position
	public void placeNote(Note note)
	{
		measures.get(currMeasure).placeNote(note);
	}
	
	//remove note at passed in guitar string at current measure position
	public void removeNote(int string)
	{
		measures.get(currMeasure).removeNote(string);
	}
	
	public void prevMeasure()
	{
		if(currMeasure > 0)
		{
			currMeasure--;
			measures.get(currMeasure).moveToStart();
		}
	}
	
	public void nextMeasure()
	{
		if(currMeasure < measures.size() - 1)
		{
			currMeasure++;
			measures.get(currMeasure).moveToStart();
		}
	}
	
	public void addMeasure()
	{
		currMeasure++;
		measures.add(currMeasure, new Measure(timeSigTop, timeSigBot));
	}
	
	public void removeMeasure()
	{
		if(measures.size() > 1)
		{
			//removing endmost measure
			if(currMeasure == measures.size() - 1)
			{
				currMeasure--;
				measures.remove(measures.size() - 1);
			}
			else
			{
				measures.remove(currMeasure);
			}
			
			measures.get(currMeasure).moveToStart();
		}
		else
		{
			JOptionPane.showMessageDialog(Main.frame, "Can't delete the last remaning measure");
		}
	}
	
	public void addPosition(NoteValue noteValue)
	{
		measures.get(currMeasure).addPosition(noteValue);
	}
	
	public void removePosition()
	{
		measures.get(currMeasure).removePosition();
	}
	
	//Set note value of current measure position
	public void setPositionBeat(NoteValue noteValue)
	{
		measures.get(currMeasure).setPositionValue(noteValue);
	}
	
	public void moveLeftPosition()
	{
		measures.get(currMeasure).moveLeftPosition();
	}
	
	public void moveRightPosition()
	{
		measures.get(currMeasure).moveRightPosition();
	}
	
	public void tuneString(int string, String note)
	{
		guitarStrings[string-1] = note;
	}
	
	//display current measure to view
	public void draw(Graphics g)
	{
		g.drawString("bpm = " + bpm, 10, 30);
		g.drawString("Time Signature: " + timeSigTop + "/" + timeSigBot.getValue(), 670, 60);
        g.drawString("Tuning: ", 670, 30);
        
        int counter = 5;
        for(int i = 0; i < 6; i++)
        {
        	g.drawString(guitarStrings[counter] + " ", 750 + (i * 40), 30);
        	counter--;
        }
        
        g.drawString("Measure " + (currMeasure+1), 10, 60);
        
        measures.get(currMeasure).draw(g);
	}
}
