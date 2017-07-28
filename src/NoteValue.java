
public enum NoteValue {
	Whole			("W", 1, 1),
	Half			("H", 2, 0.5f),
	Quarter			("Q", 4, 0.25f),
	Eighth			("E", 8, 0.125f),
	Sixteenth		("S", 16, 0.125f / 2),
	ThirtySecond	("T", 32, 0.125f / 4);
	
	private String letter;
	//the number of the note in the bottom of the time signature
	private int value;
	//1 is whole note, half note is 0.5, 2 half notes make up a whole note, etc.
	private float fracValue;
	
	private NoteValue(String letter, int value, float fracValue)
	{
		this.letter = letter;
		this.value = value;
		this.fracValue = fracValue;
	}
	
	public String getLetter()
	{
		return letter;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public float getFracValue()
	{
		return fracValue;
	}
}
