
public class SquirrelPlayer
{
	enum Gender{
		FEMALE,
		MALE;
		
		public String toString()
		{
			switch(this)
			{
				case FEMALE:
					return "female";
				default:
					return "male";
			}
		}
	}
	
	private static int playerIDCounter = 0;
	
	private String myName;
	private boolean isUserPlayer, isImpounded;
	private int myPlayerID, doublesRolled, gamePosition;
	private Gender myGender;
	
	public SquirrelPlayer(String name, boolean userPlayer, boolean male)
	{
		myName = name;
		isUserPlayer = userPlayer;
		myPlayerID = playerIDCounter++;
		isImpounded = false;
		doublesRolled = 0;
		gamePosition = 0;
		if(male)
			myGender = Gender.MALE;
		else
			myGender = Gender.FEMALE;
	}
	
	public String getName()
	{
		return myName;
	}
	
	public int getGamePosition()
	{
		return gamePosition;
	}
	
	public boolean isUserPlayer()
	{
		return isUserPlayer;
	}
	
	//returns whether or not the player is in the custody of Animal Control
	public boolean isImpounded()
	{
		return isImpounded;
	}
	
	public int getPlayerID()
	{
		return myPlayerID;
	}
	
	public Gender getGender()
	{
		return myGender;
	}
	
	public int getNumDoublesRolled()
	{
		return doublesRolled;
	}
	
	public void advanceSpaces(int numSpcs, int totalBoardSpcs)
	{
		gamePosition += numSpcs;
		gamePosition %= totalBoardSpcs;
	}
	
	public void setImpoundStatus(boolean impoundStatus)
	{
		isImpounded = impoundStatus;
	}
	
	public void incrementDoubles()
	{
		doublesRolled++;
	}
	
	public void zeroOutDoubles()
	{
		doublesRolled = 0;
	}
}
