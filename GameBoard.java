
public class GameBoard
{
	private final int NUM_SPACES;
	
	public GameBoard()
	{
		NUM_SPACES = 60;
	}
	
	public GameBoard(int numSpaces)
	{
		if(numSpaces > 0 && numSpaces % 4 == 0)
			NUM_SPACES = numSpaces;
		else
			NUM_SPACES = 60;
	}
	
	public int getNumSpaces()
	{
		return NUM_SPACES;
	}
}
