
public class Note {

	private int string; //guitar string note is on
	private int fret;
	
	public Note(int string, int fret)
	{
		this.string = string;
		this.fret = fret;
	}
	
	public int getString()
	{
		return string;
	}
	
	public int getFret()
	{
		return fret;
	}
	
	public String toString()
	{
		String result = "";
		
		//single digit frets will have a hyphen padding
		if(fret < 10)
		{
			result += "-";
		}
		result += String.valueOf(fret);
		return result;
	}
}
