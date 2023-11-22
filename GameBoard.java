
public class GameBoard
{
	private final int NUM_SPACES, DEFAULT_SMALL = 40, DEFAULT_LARGE = 80;
	
	public GameBoard(boolean smallBoard)
	{
		if(smallBoard)
			NUM_SPACES = DEFAULT_SMALL;
		else
			NUM_SPACES = DEFAULT_LARGE;
	}
	
	public int getNumSpaces()
	{
		return NUM_SPACES;
	}
}
