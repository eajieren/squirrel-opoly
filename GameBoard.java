import java.util.Arrays;

public class GameBoard
{
	private final int NUM_SPACES, DEFAULT_SMALL = 40, DEFAULT_LARGE = 80;
	private String[] spcToLocationCode;
	
	public GameBoard(boolean smallBoard)
	{
		if(smallBoard)
			NUM_SPACES = DEFAULT_SMALL;
		else
			NUM_SPACES = DEFAULT_LARGE;
		
		spcToLocationCode = new String[NUM_SPACES];
		setup();
		System.out.println("GameBoard constructor: Locations:");
		for(int spcNum = 0; spcNum < NUM_SPACES; spcNum++)
		{
			System.out.println("\t" + spcNum + ": " + spcToLocationCode[spcNum]);
		}
	}
	
	public int getNumSpaces()
	{
		return NUM_SPACES;
	}
	
	private void setup()
	{
		String[] cornerCodes = {"PROC", "ACES", "ANCO", "LVTP"};
		int cornerCode_index = 0;
		for(int spc = 0; spc < NUM_SPACES; spc++)
		{
			//if it's a corner space, give it a corner code
			if(spc % (NUM_SPACES/4) == 0)
			{
				spcToLocationCode[spc] = cornerCodes[cornerCode_index++];
				continue;
			}
			else
			{
				if(spc % (NUM_SPACES/8) == 0)
				{
					//mark as a utility
					spcToLocationCode[spc] = "UTILITY";
					continue;
				}
			}
			
			int mod10 = spc % 10;
			if(mod10 != 0 && mod10 % 3 == 0)
			{
				//pick for the spot to be comm. chest/chance/danger spot
				spcToLocationCode[spc] = "Comm.Chest/Chance/Danger";
				continue;
			}
			
			if(NUM_SPACES == DEFAULT_LARGE && mod10 == 5)
			{
				//adventure spaces for 80-spc boards
				spcToLocationCode[spc] = "Adventure";
				continue;
			}
			
			if(mod10 == 4)
			{
				//spaces for residence spaces (human homes)
				spcToLocationCode[spc] = "Human Home";
				continue;
			}
			
			Integer[] treeMods = {1, 2, 7, 8};
			if(Arrays.asList(treeMods).contains(Integer.valueOf(mod10)))
			{
				//spaces for various tree locations, the names of which come from the TreesOfOK text file
				spcToLocationCode[spc] = "Tree Spot";
			}
		}
		//set up corner spaces
		//0-proceed; 1/4-high-security-animal-control; 1/2-animal control; 3/4
		
		//set up utilities
		
		//set up c.chest/chance/
		
		//if 80 spcs, set up adventure spcs
	}
}
