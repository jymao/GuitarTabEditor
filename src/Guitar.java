//Different guitar instruments for midiplayer
public enum Guitar {
	Nylon 		(24),
	Steel 		(25),
	Jazz  		(26),
	Clean 		(27),
	Muted 		(28);
	
	private final int index;
	
	private Guitar(int index)
	{
		this.index = index;
	}
	
	public int getIndex()
	{
		return index;
	}
}
