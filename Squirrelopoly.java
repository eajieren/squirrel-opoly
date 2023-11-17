
import javax.swing.JOptionPane;

public class Squirrelopoly
{
	public static void main(String[] args)
	{
		System.out.println("Welcome to Squirrelopoly!");
		GameBoard board = new GameBoard();
		System.out.println("Game board size: " + board.getNumSpaces() + " spaces");
		
		int numPlayers = getNumPlayers();
		
		System.out.println("Thank you for deciding on " + numPlayers + " user players!");
	}
	
	private static int getNumPlayers()
	{
		String numPlayersString = "";
		boolean firstIteration = true;
		
		do
		{
			if(firstIteration)
			{
				numPlayersString = JOptionPane.showInputDialog("How many user players will be in today's game? (Please enter a number between 1 and 4.)");
				firstIteration = false;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid Input! Please try again.", "ERROR", JOptionPane.ERROR_MESSAGE);
				numPlayersString = JOptionPane.showInputDialog("How many user players will be in today's game? (Please enter a number between 1 and 4.)");
			}
		}
		while(!isNonNegIntString(numPlayersString) || Integer.parseInt(numPlayersString) < 1 || Integer.parseInt(numPlayersString) > 4);
		
		return Integer.parseInt(numPlayersString);
	}
	
	//returns true if the string contains only numeric characters; false, otherwise
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
}
