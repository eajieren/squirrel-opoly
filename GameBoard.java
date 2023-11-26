import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class GameBoard
{
	private final int NUM_SPACES, DEFAULT_SMALL = 40, DEFAULT_LARGE = 80;
	private final String[] cornerCodes = {"PROC", "ACES", "ANCO"},
			parkCodes = {"PRKA", "PRKB", "PRKC", "PRKD"};
	//private String[] spcToLocationCode;
	private GameSpace[] spaces;
	private int cornerCode_index, parkCode_index;
	
	public GameBoard(boolean smallBoard, ArrayList<String> treeNames)
	{
		if(smallBoard)
			NUM_SPACES = DEFAULT_SMALL;
		else
			NUM_SPACES = DEFAULT_LARGE;
		
		//spcToLocationCode = new String[NUM_SPACES];
		spaces = new GameSpace[NUM_SPACES];
		cornerCode_index = parkCode_index = 0;
		setup(treeNames);
		
		//for testing setup() method:
		///*_****************************************************
		System.out.println("GameBoard constructor: Locations:");
		for(int spcNum = 0; spcNum < NUM_SPACES; spcNum++)
		{
			System.out.println("\t" + spcNum + ": " + getLocationName(spcNum));
		}
		//*****************************************************_*/
	}
	
	public int getNumSpaces()
	{
		return NUM_SPACES;
	}
	
	public String getLocationName(int spaceNum)
	{
		if(spaceNum < 0 || spaceNum >= NUM_SPACES)
			return null;
		else
			return spaces[spaceNum].getCode();
	}
	
	public GameSpace getGameSpaceAt(int spaceNum)
	{
		if(spaceNum < 0 || spaceNum >= NUM_SPACES)
			return null;
		else
			return spaces[spaceNum];
	}
	
	private void setup(ArrayList<String> treeNames)
	{
		for(int spc = 0; spc < NUM_SPACES; spc++)
		{
			//if it's a corner space, give it a corner code
			if(spc % (NUM_SPACES/4) == 0)
			{
				if(spc/(NUM_SPACES/4) < 3)
				{
					spaces[spc] = new PassiveEventSpace(cornerCodes[cornerCode_index++]);
					continue;
				}
				else
				{
					spaces[spc] = new LiveTrap();
					continue;
				}
			}
			else
			{
				if(spc % (NUM_SPACES/8) == 0)
				{
					//mark as a park
					spaces[spc] = new ResidenceSpace(parkCodes[parkCode_index++], 5);
					continue;
				}
			}
			
			int mod10 = spc % 10;
			if(mod10 != 0 && mod10 % 3 == 0)
			{
				//pick for the spot to be comm. chest/chance/danger spot
				spaces[spc] = new CardSpace("Comm.Chest/Chance/Danger", CardSpace.Type.COMMUNITY_CHEST);
				continue;
			}
			
			if(NUM_SPACES == DEFAULT_LARGE && mod10 == 5)
			{
				//adventure spaces for 80-spc boards
				
				//adventure space will have its own class/implementation, as it does depend upon user input
				spaces[spc] = new CardSpace("Adventure", CardSpace.Type.CHANCE);
				continue;
			}
			
			Random rand = new Random();
			
			if(mod10 == 4)
			{
				//spaces for residence spaces (human homes)
				spaces[spc] = new ResidenceSpace("Human Home", Math.abs(rand.nextInt()) % 6 + 2);
				continue;
			}
			
			Integer[] treeMods = {1, 2, 7, 8};
			if(Arrays.asList(treeMods).contains(Integer.valueOf(mod10)))
			{
				String treeString = treeNames.remove(Math.abs(rand.nextInt()) % treeNames.size());
				//spaces for various tree locations, the names of which come from the TreesOfOK text file
				spaces[spc] = new ResidenceSpace(treeString, Math.abs(rand.nextInt()) % 3 + 1);
			}
		}
	}
}
