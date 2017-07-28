import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class Measure {
	
	private int timeSigTop;
	private NoteValue timeSigBot;
	private float capacity; //used to figure how many notes measure can hold based on timeSig
	private float currFracValue = 0;
	
	private int currPosition = 0;
	private ArrayList<MeasurePosition> positions = new ArrayList<MeasurePosition>();
	
	//for drawing a measure
	private final int STRING1_Y = 120;
	private final int OFFSET_Y = 30;
	private final int CHAR_WIDTH = 10;
	
	public Measure(int timeSigTop, NoteValue timeSigBot)
	{
		this.timeSigTop = timeSigTop;
		this.timeSigBot = timeSigBot;
		
		calcMaxFracValue();
		
		//measure starts with a quarter note position
		MeasurePosition pos = new MeasurePosition(NoteValue.Quarter);
		positions.add(pos);
		pos.setSelected(true);
		currFracValue += NoteValue.Quarter.getFracValue();
	}
	
	public int getCurrPosition() { return currPosition; }
	
	//Find capacity which is used to figure how many notes the measure can hold
	private void calcMaxFracValue()
	{
		capacity = timeSigBot.getFracValue() * timeSigTop;
	}
	
	public ArrayList<MeasurePosition> getPositions()
	{
		return positions;
	}
	
	public void addPosition(NoteValue noteValue)
	{
		if(currFracValue + noteValue.getFracValue() <= capacity)
		{
			currFracValue += noteValue.getFracValue();
			positions.get(currPosition).setSelected(false);
			currPosition++;
			MeasurePosition pos = new MeasurePosition(noteValue);
			positions.add(currPosition, pos);
			pos.setSelected(true);
		}
		//Error message
		else
		{
			JOptionPane.showMessageDialog(Main.frame, "Note value can't fit in this measure.");
		}
	}
	
	public void removePosition()
	{
		if(positions.size() > 1)
		{
			currFracValue -= positions.get(positions.size() - 1).getNoteValue().getFracValue();
			
			//removing endmost position
			if(currPosition == positions.size() - 1)
			{
				currPosition--;
				positions.remove(positions.size() - 1);
				positions.get(currPosition).setSelected(true);
			}
			else
			{
				positions.remove(currPosition);
				positions.get(currPosition).setSelected(true);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(Main.frame, "Can't delete the last remaining position.");
		}
	}
	
	//rewind current measure position to start
	public void moveToStart()
	{
		positions.get(currPosition).setSelected(false);
		currPosition = 0;
		positions.get(currPosition).setSelected(true);
	}
	
	public void moveLeftPosition()
	{
		if(currPosition > 0)
		{
			positions.get(currPosition).setSelected(false);
			currPosition--;
			positions.get(currPosition).setSelected(true);
		}
	}
	
	public void moveRightPosition()
	{
		if(currPosition < positions.size() - 1)
		{
			positions.get(currPosition).setSelected(false);
			currPosition++;
			positions.get(currPosition).setSelected(true);
		}
	}
	
	//change current measure position's note value
	public void setPositionValue(NoteValue noteValue)
	{
		MeasurePosition position = positions.get(currPosition);
		NoteValue oldValue = position.getNoteValue();
		
		if(currFracValue - oldValue.getFracValue() + noteValue.getFracValue() <= capacity)
		{
			currFracValue = currFracValue - oldValue.getFracValue() + noteValue.getFracValue();
			position.setNoteValue(noteValue);
		}
		//Error message
		else
		{
			JOptionPane.showMessageDialog(Main.frame, "Note value can't fit in this measure.");
		}
	}
	
	public void placeNote(Note note)
	{
		MeasurePosition position = positions.get(currPosition);	
		position.addNote(note);
	}
	
	public void removeNote(int string)
	{
		MeasurePosition position = positions.get(currPosition);
		position.removeNote(string);
	}
	
	//Convert measure info to string form to be written and saved to a file
	public String toString()
	{
		String result = "";
		
		int numStrings = 6; //strings of guitar
		int numPositions = 65; //assuming 4/4 time sig, 32 thirty-second beats is most measure can hold.
							   //With padding for -x-x- pattern, 32*2 + 1 = 65
		
		int toSkip = 0; //skip empty positions after note value
		int positionIndex = 0; //actual index of measure's current position, not empty position spots
		boolean noteLine = false; //for last line printing note values instead
		
		for(int string = 0; string < numStrings+1; string++)
		{
			if(string == numStrings)
			{
				noteLine = true;
			}
			
			for(int position = 0; position < numPositions; position++)
			{
				//padding
				if(position % 2 == 0)
				{
					if(noteLine)
					{
						result += " ";
					}
					else
					{
						result += "-";						
					}
				}
				//measure position
				else
				{
					//previous measure position has more beat length
					if(toSkip > 0)
					{
						if(noteLine)
						{
							result += "  ";
						}
						else
						{
							result += "--";						
						}
						toSkip--;
					}
					//position exists
					else if(positionIndex < positions.size())
					{
						Note note = positions.get(positionIndex).getNote(string + 1);
						NoteValue noteValue = positions.get(positionIndex).getNoteValue();
						
						//note value
						if(noteLine)
						{
							result += " " + noteValue.getLetter();
						}
						//empty string
						else if(note == null)
						{
							result += "--";
						}
						//note available
						else
						{
							result += note.toString();
						}
						
						switch(noteValue)
						{
							case Whole:
								toSkip = 31;
								break;
							case Half:
								toSkip = 15;
								break;
							case Quarter:
								toSkip = 7;
								break;
							case Eighth:
								toSkip = 3;
								break;
							case Sixteenth:
								toSkip = 1;
								break;
							case ThirtySecond:
								toSkip = 0;
								break;
						}
						
						positionIndex++;
					}
					//empty spot
					else
					{
						if(noteLine)
						{
							result += "  ";
						}
						else
						{
							result += "--";
						}
					}
				}
			}
			
			toSkip = 0;
			positionIndex = 0;
			result += System.lineSeparator(); //new line
		}
		
		return result;
	}
	
	//draws measure to view
	//similar to toString, but calls position's draw method to draw position by position instead of by line
	public void draw(Graphics g)
	{
		int numPositions = 65; //assuming 4/4 time sig, 32 thirty-second beats is most measure can hold.
		   //With padding for -x-x- pattern, 32*2 + 1 = 65
		int toSkip = 0;
		int positionIndex = 0;
		int charCounter = 1;
		
		for(int pos = 0; pos < numPositions; pos++)
		{
			//padding
			if(pos % 2 == 0)
			{
				drawEmptyPos(g, (charCounter * CHAR_WIDTH), true);
				charCounter++;
			}
			//measure position
			else
			{
				//previous measure position has more beat length
				if(toSkip > 0)
				{
					drawEmptyPos(g, (charCounter * CHAR_WIDTH), false);
					charCounter += 2;
					toSkip--;
				}
				//position exists
				else if(positionIndex < positions.size())
				{
					MeasurePosition position = positions.get(positionIndex);
					NoteValue noteValue = position.getNoteValue();
					
					position.draw(g, charCounter * CHAR_WIDTH);
					
					switch(noteValue)
					{
						case Whole:
							toSkip = 31;
							break;
						case Half:
							toSkip = 15;
							break;
						case Quarter:
							toSkip = 7;
							break;
						case Eighth:
							toSkip = 3;
							break;
						case Sixteenth:
							toSkip = 1;
							break;
						case ThirtySecond:
							toSkip = 0;
							break;
					}
					
					positionIndex++;
					charCounter += 2;
				}
				//empty spot
				else
				{
					drawEmptyPos(g, (charCounter * CHAR_WIDTH), false);
					charCounter += 2;
				}
			}
		}
	}
	
	//draw empty position or padding
	private void drawEmptyPos(Graphics g, int posX, boolean isPadding)
	{
		for(int string = 0; string < 6; string++)
		{
			if(isPadding)
			{
				g.drawString("-", posX, STRING1_Y + (string * OFFSET_Y));
			}
			else
			{
				g.drawString("--", posX, STRING1_Y + (string * OFFSET_Y));
			}
		}
	}
}
