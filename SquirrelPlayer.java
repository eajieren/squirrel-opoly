
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
	private static final int MAX_HEALTH = 20, MAX_FOOD = 3, STARTING_FOOD = 2;
	
	private String myName;
	private boolean isUserPlayer, isImpounded, sexuallyMature;
	private int myPlayerID, doublesRolled, gamePosition, myMaxHealth, currentHealth;
	private int foodUnitCarryingCapacity, currentFoodUnits, numMoves;
	private Gender myGender;
	
	public SquirrelPlayer(String name, boolean userPlayer, boolean male)
	{
		myName = name;
		isUserPlayer = userPlayer;
		myPlayerID = playerIDCounter++;
		isImpounded = false;
		sexuallyMature = false;
		currentHealth = myMaxHealth = MAX_HEALTH;
		foodUnitCarryingCapacity = MAX_FOOD - 1;
		currentFoodUnits = STARTING_FOOD;
		doublesRolled = gamePosition = numMoves = 0;
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
	
	public boolean isAlive()
	{
		return currentHealth > 0;
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
	
	public int getCurrentHealth()
	{
		return currentHealth;
	}
	
	public int getMyMaxHealth()
	{
		return myMaxHealth;
	}
	
	public int getNumMoves()
	{
		return numMoves;
	}
	
	//advances the player numSpcs spaces and returns true if the player passes go while moving forward
	public boolean advanceSpaces(int numSpcs, int totalBoardSpcs)
	{
		gamePosition += numSpcs;
		
		//if the resulting game position is negative, we need to make it valid (between 0 and totalBoardSpcs-1, inclusive)
		if(gamePosition < 0)
		{
			gamePosition += totalBoardSpcs;
			return false;
		}
		
		if(gamePosition >= totalBoardSpcs)
		{
			gamePosition %= totalBoardSpcs;
			return true;
		}
		else
			return false;
	}
	
	public void incrementMoves()
	{
		numMoves++;
	}
	
	public void setCurrentHealth(int healthInt)
	{
		currentHealth = healthInt;
	}
	
	public void setMaxHealth(int max)
	{
		myMaxHealth = max;
	}
	
	public void addFoodUnits(int units)
	{
		currentFoodUnits += units;
		if(currentFoodUnits > foodUnitCarryingCapacity)
		{
			currentFoodUnits = foodUnitCarryingCapacity;
		}
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
