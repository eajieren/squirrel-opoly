
public class SquirrelPlayer
{
	private static int playerIDCounter = 1;
	
	private String myName;
	private boolean isUserPlayer;
	private int myPlayerID;
	
	public SquirrelPlayer(String name, boolean userPlayer)
	{
		myName = name;
		isUserPlayer = userPlayer;
		myPlayerID = playerIDCounter++;
	}
	
	public String getName()
	{
		return myName;
	}
	
	public boolean isUserPlayer()
	{
		return isUserPlayer;
	}
	
	public int getPlayerID()
	{
		return myPlayerID;
	}
}
