import javax.swing.JOptionPane;

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
	private boolean isUserPlayer, isImpounded, sexuallyMature, isTrapped;
	private int myPlayerID, doublesRolled, gamePosition, myMaxHealth, currentHealth, lapsCompleted;
	private int foodUnitCarryingCapacity, currentFoodUnits, numMoves, trapTurns, totalTrapTurns, impoundTurns;
	private Gender myGender;
	
	public SquirrelPlayer(String name, boolean userPlayer, boolean male)
	{
		myName = name;
		isUserPlayer = userPlayer;
		myPlayerID = playerIDCounter++;
		isImpounded = false;
		sexuallyMature = false;
		isTrapped = false;
		currentHealth = myMaxHealth = MAX_HEALTH;
		foodUnitCarryingCapacity = MAX_FOOD - 1;
		currentFoodUnits = STARTING_FOOD;
		doublesRolled = gamePosition = numMoves = trapTurns = totalTrapTurns = impoundTurns = 0;
		lapsCompleted = 0;
		if(male)
			myGender = Gender.MALE;
		else
			myGender = Gender.FEMALE;
	}
	
	public String getName()
	{
		return myName;
	}
	
	public String getObjectPronoun()
	{
		switch(myGender)
		{
			case FEMALE:
				return "her";
			default:
				return "him";
		}
	}
	
	public int getGamePosition()
	{
		return gamePosition;
	}
	
	public void setGamePosition(int pos)
	{
		gamePosition = pos;
	}
	
	public boolean isUserPlayer()
	{
		return isUserPlayer;
	}
	
	public int getImpoundTurns()
	{
		return impoundTurns;
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
	
	public String getSubjectPronoun(boolean uppercase)
	{
		switch(myGender)
		{
			case FEMALE:
				if(uppercase)
				{
					return "She";
				}
				return "she";
			default:
				if(uppercase)
				{
					return "He";
				}
				return "he";
		}
	}

	public int getCurrentFood()
	{
		return currentFoodUnits;
	}
	
	public int getMaxFoodCapacity()
	{
		return foodUnitCarryingCapacity;
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
	
	public int getTrapTurns()
	{
		return trapTurns;
	}
	
	public int getTotalTrapTurns()
	{
		return totalTrapTurns;
	}
	
	public boolean getTrappedStatus()
	{
		return isTrapped;
	}
	
	public boolean isSexuallyMature()
	{
		return sexuallyMature;
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
	
	public void incrementLapsCompleted()
	{
		lapsCompleted++;
		
		if(!sexuallyMature)
			sexuallyMature = !sexuallyMature;
	}
	
	public int getNumLapsCompleted()
	{
		return lapsCompleted;
	}
	
	public void setTotalTrapTurns(int totalTrapped)
	{
		totalTrapTurns = totalTrapped;
	}
	
	public void incrementTrapTurns()
	{
		trapTurns++;
	}
	
	public void incrementImpoundTurns()
	{
		impoundTurns++;
	}
	
	//NOT DONE
	public void setCurrentHealth(int healthInt)
	{
		currentHealth = healthInt;
		
		if(healthInt <= 0)
		{
			//all properties owned by this player are set back to owner=null
			
			JOptionPane.showMessageDialog(null, myName + " has lost all health points and is now dead.");
		}
	}
	
	public void setFoodUnits(int food)
	{
		currentFoodUnits = food;
	}
	
	public void setTrappedStatus(boolean status)
	{
		isTrapped = status;
		
		if(!isTrapped)
		{
			trapTurns = 0;
			totalTrapTurns = 0;
		}
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
		impoundTurns = 0;
	}
	
	public void incrementDoubles()
	{
		doublesRolled++;
	}
	
	public void zeroOutDoubles()
	{
		doublesRolled = 0;
	}

	public String getPossessivePronoun()
	{
		switch(myGender)
		{
			case FEMALE:
				return "her";
			default:
				return "his";
		}
	}
	
	public static int getAllSquirrelMaxHealth()
	{
		return MAX_HEALTH;
	}
}
