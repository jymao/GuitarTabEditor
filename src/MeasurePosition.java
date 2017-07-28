import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashMap;

//contains notes of each string at a measure position
public class MeasurePosition {

	private NoteValue noteValue;
	private HashMap<Integer, Note> notes = new HashMap<Integer, Note>();
	
	private boolean isSelected = false; //highlight for current position in a measure
	
	//for draw method
	private final int STRING1_Y = 120;
	private final int OFFSET_Y = 30;
	
	public MeasurePosition(NoteValue noteValue)
	{
		this.noteValue = noteValue;
	}
	
	public boolean isEmpty() { return notes.isEmpty(); }
	public NoteValue getNoteValue() { return noteValue; }
	public Collection<Note> getNotes() { return notes.values(); }
	public void setNoteValue(NoteValue noteValue) { this.noteValue = noteValue; }
	public void setSelected(boolean b) { isSelected = b; }
	
	public void addNote(Note note)
	{
		int string = note.getString();
		notes.put(string, note);
	}
	
	public void removeNote(int string)
	{
		notes.remove(string);
	}
	
	//Can return null if no note at that string
	public Note getNote(int string)
	{
		return notes.get(string);
	}
	
	//draw method called by measure draw
	public void draw(Graphics g, int posX)
	{
		//selected position is highlighted in red and has a marker underneath
		if(isSelected)
		{
			g.setColor(Color.RED);
			g.drawString("__", posX, STRING1_Y + (8 * OFFSET_Y));
		}
		else
		{
			g.setColor(Color.BLACK);
		}
		
		for(int string = 0; string < 6; string++)
		{
			String note;
			if(getNote(string + 1) != null)
			{
				note = getNote(string + 1).toString();
			}
			else
			{
				note = "--";
			}
			g.drawString(note, posX, STRING1_Y + (string * OFFSET_Y));
		}
		
		g.drawString(" " + noteValue.getLetter(), posX, STRING1_Y + (6 * OFFSET_Y));
		g.setColor(Color.BLACK);
	}
	
}
