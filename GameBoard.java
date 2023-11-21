
public class GameBoard
{
	private final int NUM_SPACES, DEFAULT_SIZE = 80;
	
	public GameBoard()
	{
		NUM_SPACES = DEFAULT_SIZE;
	}
	
	public GameBoard(int numSpaces)
	{
		if(numSpaces > 0 && numSpaces % 4 == 0)
			NUM_SPACES = numSpaces;
		else
			NUM_SPACES = DEFAULT_SIZE;
	}
	
	public int getNumSpaces()
	{
		return NUM_SPACES;
	}
}
