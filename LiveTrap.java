import java.util.Random;

import javax.swing.JOptionPane;

public class LiveTrap extends EventSpace
{
	private final String premise = "You've been caught in a live rat trap. You can choose to either escape and risk injury or wait until you're released.";
	
	public LiveTrap(int spcNum)
	{
		super("LVTP", spcNum);
	}
	
	public void applyEvent(SquirrelPlayer player, Game currentGame)
	{	
		GameGUI display = currentGame.getDisplay();
		boolean escape;
		
		if(player.isUserPlayer())
		{
			//allow player to decide to escape or wait
			Object[] choices = {"ESCAPE", "WAIT"};
			escape = JOptionPane.showOptionDialog(display, premise, player.getName() + " stuck in a rat trap!", 
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]) == 0;
		}
		else
		{
			JOptionPane.showMessageDialog(display, player.getName() + " has been caught in a live rat trap. " +
					player.getSubjectPronoun(true) + " can now decide to attempt an escape or to wait to be released.");
			Random rand = new Random();
			escape = rand.nextBoolean();
		}
			
		if(escape)
		{
			applyEscape(player, currentGame);
		}
		else
		{
			applyWait(player, display);
		}
	}
	
	private void applyEscape(SquirrelPlayer player, Game currentGame)
	{
		GameGUI display = currentGame.getDisplay();
		
		if(player.isUserPlayer())
		{
			JOptionPane.showMessageDialog(display, player.getName() + ": You've chosen to escape! You'll now roll one die and then another in order to calculate the damage " +
			"your character will endure while escaping.");
		}
		else
		{
			JOptionPane.showMessageDialog(display, player.getName() + " has chosen to escape and will now roll one die and then another in order to calculate the damage " +
					"that will be incurred while escaping.");
		}
					
		int[] firstRoll = new int[] {Game.rollDie()};
		Game.displayDiceRoll(firstRoll, display);
					
		int[] secondRoll = new int[] {Game.rollDie()};
		Game.displayDiceRoll(secondRoll, display);
					
		//roll dice for damage
		Random rand = new Random();
		int damage = firstRoll[0] + Math.abs(rand.nextInt()) % secondRoll[0];
					
		JOptionPane.showMessageDialog(display, player.getName() + " escaped! Injury level was calculated as " + damage + " health point(s) out of your total of " +
			player.getCurrentHealth() + " health points.");
					
		player.setCurrentHealth(player.getCurrentHealth() - damage, currentGame);
	}
	
	private void applyWait(SquirrelPlayer player, GameGUI display)
	{
		player.setTrappedStatus(true);
		
		if(player.isUserPlayer())
		{
			JOptionPane.showMessageDialog(display, player.getName() + ": You've chosen to wait for your release. You'll now roll one die, divide the result by two, and round up " +
				"to the nearest whole number in order to determine how many turns to wait.");
		}
		else
		{
			JOptionPane.showMessageDialog(display, player.getName() + " has chosen to wait to be released and will now roll one die, divide the result by two, and round up " +
					"to the nearest whole number in order to determine how many turns to wait.");
		}
		
		int[] firstRoll = new int[] {Game.rollDie()};
		Game.displayDiceRoll(firstRoll, display);
		
		int turns = (firstRoll[0] % 2 == 1) ? (firstRoll[0] + 1)/2 : firstRoll[0]/2;
		player.setTotalTrapTurns(turns);
		
		JOptionPane.showMessageDialog(display, player.getName() + ": You'll skip your next " + turns + " turns as you await your release.");
	}
}
