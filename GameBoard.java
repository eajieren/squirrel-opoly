import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class GameBoard
{
	private final int NUM_SPACES, DEFAULT_SMALL = 40, DEFAULT_LARGE = 80;
	private final String[] cornerCodes = {"PROCEED", "Animal Control Overflow", "Animal Control"},
			parkCodes = {"Brownstone Community Park", "Green Pastures Park", "Clovehitch Community Park",
					"Rosedale Lake Park"},
			cornerSpcQuips = {"Onward on your journey!", "Be glad that you're just passing through.",
					"Close your eyes if you're squeamish."};
	private final String[] colors = {"Black", "Blue", "Brown", "Green", "Orange", "Red", "White", "Yellow"};
	private final String[] geogFeatures = {"Mountain", "Lake", "River", "Tributary", "Isthmus", "Cape",
			"Butte", "Foothills", "Volcano", "Geyser", "Fjord", "Plateau", "Oasis", "Mesa", "Valley",
			"Bay", "Archipelago", "Strait", "Forest", "Plain", "Delta", "Gulf", "Harbor", "Atoll", "Lagoon",
			"Isle"};
	private final String[] streetNames = {"St.", "Ave.", "Blvd.", "Ln.", "Cir.", "Promenade", "Dr.",
			"Pkwy.", "Rd.", "Way", "Trail"};
	
	private GameSpace[] spaces;
	private int cornerCode_index, parkCode_index;
	private DeckManager decks;
	
	public GameBoard(boolean smallBoard, DeckManager manager, ArrayList<String> treeNames)
	{
		if(smallBoard)
			NUM_SPACES = DEFAULT_SMALL;
		else
			NUM_SPACES = DEFAULT_LARGE;
		
		spaces = new GameSpace[NUM_SPACES];
		decks = manager;
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
	
	public int getAnimalControlSpaceNum()
	{
		return NUM_SPACES/2;
	}
	
	public int getOverflowSpaceNum()
	{
		return NUM_SPACES/4;
	}
	
	public String getRandomAddress()
	{
		Random rand = new Random();
		String address = "";
		address += Integer.valueOf(Math.abs(rand.nextInt()) % 10000).toString();
		address += " ";
		address += colors[rand.nextInt(colors.length)];
		address += " ";
		address += geogFeatures[rand.nextInt(geogFeatures.length)];
		address += " ";
		address += streetNames[rand.nextInt(streetNames.length)];
		
		return address;
	}
	
	private void setup(ArrayList<String> treeNames)
	{
		CardSpace.Type[] cardSpaceTypes = 
			{CardSpace.Type.TROVE_OF_ACORNS, CardSpace.Type.FLEET_OF_PAW, CardSpace.Type.DOC_OR_DANGER};
		int typeIndex = 0;
		
		for(int spc = 0; spc < NUM_SPACES; spc++)
		{
			//if it's a corner space, give it a corner code
			if(spc % (NUM_SPACES/4) == 0)
			{
				if(spc/(NUM_SPACES/4) < 3)
				{
					spaces[spc] = new PassiveEventSpace(cornerCodes[cornerCode_index],
							cornerSpcQuips[cornerCode_index++], spc);
					continue;
				}
				else
				{
					spaces[spc] = new LiveTrap(spc);
					continue;
				}
			}
			else
			{
				if(spc % (NUM_SPACES/8) == 0)
				{
					//mark as a park
					spaces[spc] = new ResidenceSpace(parkCodes[parkCode_index++], spc, 3);
					continue;
				}
			}
			
			int mod10 = spc % 10;
			if(mod10 != 0 && mod10 % 3 == 0)
			{
				//pick for the spot to be comm. chest/chance/danger spot
				spaces[spc] = new CardSpace("Comm.Chest/Chance/Danger", spc, cardSpaceTypes[(typeIndex++)%cardSpaceTypes.length], decks);
				continue;
			}
			
			if(NUM_SPACES == DEFAULT_LARGE && mod10 == 5)
			{
				//adventure spaces for 80-spc boards
				
				//adventure space will have its own class/implementation, as it does depend upon user input
				spaces[spc] = new CardSpace("Adventure", spc, CardSpace.Type.DOC_OR_DANGER, decks);
				continue;
			}
			
			Random rand = new Random();
			
			if(mod10 == 4)
			{
				//spaces for residence spaces (human homes)
				spaces[spc] = new ResidenceSpace(getRandomAddress(), spc, Math.abs(rand.nextInt()) % 3 + 2);
				continue;
			}
			
			Integer[] treeMods = {1, 2, 7, 8};
			if(Arrays.asList(treeMods).contains(Integer.valueOf(mod10)))
			{
				String treeString = treeNames.remove(Math.abs(rand.nextInt()) % treeNames.size());
				//spaces for various tree locations, the names of which come from the TreesOfOK text file
				spaces[spc] = new ResidenceSpace(treeString, spc, Math.abs(rand.nextInt()) % 2 + 1);
			}
		}
	}
}
