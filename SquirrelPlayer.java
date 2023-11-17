
public class SquirrelPlayer
{
	private String myName;
	private boolean isUserPlayer;
	
	public SquirrelPlayer(String name, boolean userPlayer)
	{
		myName = name;
		isUserPlayer = userPlayer;
	}
	
	public String getName()
	{
		return myName;
	}
	
	public boolean isUserPlayer()
	{
		return isUserPlayer;
	}
}
