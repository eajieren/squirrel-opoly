
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Squirrelopoly
{
	public static final int TOTAL_NUM_PLAYERS = 4;
	
	public static void main(String[] args)
	{
		//intro
		System.out.println("Welcome to Squirrelopoly!");
		GameBoard board = new GameBoard();
		System.out.println("Game board size: " + board.getNumSpaces() + " spaces");
		
		//prompt user for # user players
		int numUserPlayers = getNumPlayers();
		System.out.println("Thank you for deciding on " + numUserPlayers + " user players!");
		
		ArrayList<String> nonUserPlayerNames = textFileToArrayList("src/PlayerNames.txt");
		
		ArrayList<SquirrelPlayer> allPlayers = generatePlayers(numUserPlayers, nonUserPlayerNames);
		for(SquirrelPlayer sp : allPlayers)
		{
			System.out.println(sp.getPlayerID() + ": " + sp.getName());
		}
	}
	
	private static int getNumPlayers()
	{
		String numPlayersString = "";
		boolean firstIteration = true;
		
		do
		{
			if(firstIteration)
			{
				numPlayersString = JOptionPane.showInputDialog("How many user players will be in today's game? (Please enter a number between 1 and " + TOTAL_NUM_PLAYERS + ".)");
				firstIteration = false;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "ERROR", JOptionPane.ERROR_MESSAGE);
				numPlayersString = JOptionPane.showInputDialog("How many user players will be in today's game? (Please enter a number between 1 and " + TOTAL_NUM_PLAYERS + ".)");
			}
		}
		while(!isNonNegIntString(numPlayersString) || Integer.parseInt(numPlayersString) < 1 || Integer.parseInt(numPlayersString) > TOTAL_NUM_PLAYERS);
		
		return Integer.parseInt(numPlayersString);
	}
	
	private static ArrayList<SquirrelPlayer> generatePlayers(int numUserPlayers, ArrayList<String> computerPlayerNames)
	{
		ArrayList<SquirrelPlayer> allPlayers = generateUserPlayers(numUserPlayers);
		
		if(numUserPlayers < TOTAL_NUM_PLAYERS)
		{
			Random randGenerator = new Random(System.nanoTime());
			HashSet<String> uniqueNames = new HashSet<String>();
			for(SquirrelPlayer sp : allPlayers)
			{
				uniqueNames.add(sp.getName());
			}
			
			for(int j = numUserPlayers; j < TOTAL_NUM_PLAYERS; j++)
			{
				String possiblePlyrName = "";
				do
				{
					int randIndex = Math.abs(randGenerator.nextInt()) % uniqueNames.size();
					Object[] objArr = computerPlayerNames.toArray();
					possiblePlyrName = (String) objArr[randIndex];
				}
				while(!uniqueNames.add(possiblePlyrName));
				allPlayers.add(new SquirrelPlayer(possiblePlyrName, false));
				computerPlayerNames.remove(possiblePlyrName);
			}
		}
		
		return allPlayers;
	}
	
	private static ArrayList<SquirrelPlayer> generateUserPlayers(int numUserPlayers)
	{
		//using a set in order to have distinct names for each player
		HashSet<String> playerNames = new HashSet<String>();
		ArrayList<SquirrelPlayer> userPlayers = new ArrayList<SquirrelPlayer>();
				
		for(int i = 0; i < numUserPlayers; i++)
		{
			String playerName = "";
			boolean firstIteration = true;
					
			do
			{
				if(firstIteration)
				{
					playerName = getUserPlayerName(i+1);
					firstIteration = false;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Uh-oh! That player name is already in use. Please enter a unique player name.", "Duplicate Name", JOptionPane.ERROR_MESSAGE);
					playerName = getUserPlayerName(i+1);
				}
			}
			while(!playerNames.add(playerName));	//this will continue to loop while the name is not a unique player name
					
			userPlayers.add(new SquirrelPlayer(playerName, true));
		}
				
		return userPlayers;
	}
	
	private static String getUserPlayerName(int playerNum)
	{
		String playerName = "";
		boolean firstIteration = true;
		
		do
		{
			if(firstIteration)
			{
				playerName = JOptionPane.showInputDialog("Please enter a name for Player #" + playerNum + ":");
				firstIteration = false;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "ERROR", JOptionPane.ERROR_MESSAGE);
				playerName = JOptionPane.showInputDialog("Please enter a name for Player #" + playerNum + ":");
			}
		}
		while(playerName == null || playerName.length() < 1);
		
		return playerName;
	}
	
	//returns true if the input string contains only numeric characters; false, otherwise
	private static boolean isNonNegIntString(String input)
	{
		if(input == null)
			return false;
		
		for(char c : input.toCharArray())
		{
			if(c < '0' || c > '9')
				return false;
		}
		
		return true;
	}
	
	private static ArrayList<String> textFileToArrayList(String fileLocation)
	{
		ArrayList<String> stringList = new ArrayList<String>();
		File readSpace = new File(fileLocation);
		
		try
		{
			Scanner textRead = new Scanner(new FileReader(readSpace));
			while(textRead.hasNextLine())
			{
				stringList.add(textRead.nextLine());
			}
			textRead.close();
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		return stringList;
	}
}
