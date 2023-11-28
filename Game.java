
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game
{
	private final int NUM_PLAYERS = 4, IMPOUND_ESCAPE_LIMIT = 3;
	private GameBoard myBoard;
	private GameGUI gameDisplay;
	private ArrayList<SquirrelPlayer> players;
	
	
	public Game(GameBoard board, ArrayList<SquirrelPlayer> gamePlayers, GameGUI display)
	{
		myBoard = board;
		players = gamePlayers;
		gameDisplay = display;
	}
	
	public void play()
	{
		gameDisplay.setGameInPlay(true);
		gameDisplay.pack();
		gameDisplay.setVisible(true);
		//reference comment; not meant to be uncommented
		//int[] result = {rollDie(), rollDie(), rollDie()};
		
		int currentPlayerID = getFirstPlayerID();
		
		while(!gameOver())
		{
			if(players.get(currentPlayerID % NUM_PLAYERS).isAlive())
			{
				giveTurn(players.get(currentPlayerID % NUM_PLAYERS));
			}
				
			currentPlayerID++;
		}
		
		if(gameOver())
		{
			gameDisplay.setGameInPlay(false);
			/*for(SquirrelPlayer sp : players)
			* {
			* 	if(sp.isAlive())
			* 	{
			* 		//add 15-20 points
			* 	}
			* }
			*/
		}
		
		//int resultSum = rollDice(3, true);
		//System.out.print("Dice Roll Sum = " + resultSum);
	}
	
	private int getFirstPlayerID()
	{
		Object[] option = {"Let's roll!"};
		JOptionPane.showOptionDialog(gameDisplay, "Let's roll dice to see who goes first.", "Ready to Roll?",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, option, option[0]);
		int numRolls = 3;
		int maxTotal = numRolls * 6;
		int highID = 0, highSum = 0;
		for(int id = 0; id < NUM_PLAYERS; id++)
		{
			SquirrelPlayer currentPlayer = players.get(id);
			int currentSum = rollDice(3, currentPlayer);
			if(currentSum == maxTotal)
			{
				//report this roll and declare this person the winner
				JOptionPane.showMessageDialog(gameDisplay, currentPlayer.getName() + " rolled the maximum of " + currentSum + " and goes first.");
				clearScreen(gameDisplay);
				return id;
			}
			if(currentSum > highSum)
			{
				if(id == NUM_PLAYERS - 1)
				{
					JOptionPane.showMessageDialog(gameDisplay, currentPlayer.getName() + " rolled the highest total of " + currentSum + " and goes first.");
					clearScreen(gameDisplay);
					return id;
				}
				
				highSum = currentSum;
				highID = id;
			}
				
			JOptionPane.showMessageDialog(gameDisplay, currentPlayer.getName() + " rolled a total of " + currentSum + ".");
			clearScreen(gameDisplay);
		}
		
		JOptionPane.showMessageDialog(gameDisplay, players.get(highID).getName() + " rolled the highest total of " + highSum + " and goes first.");
		clearScreen(gameDisplay);
		
		return highID;
	}
	
	private void giveTurn(SquirrelPlayer turnPlayer)
	{
		if(turnPlayer.isAlive())
		{
			boolean exit;
			do
			{
				Object[] choices = {"OK", "EXIT"};
				exit = JOptionPane.showOptionDialog(gameDisplay, "It's " + turnPlayer.getName() + "\'s turn.", "Proceed to " + turnPlayer.getName() + "\'s turn", 
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]) == 1;
				if(exit)
				{
					if(GameGUI.exit(gameDisplay))
						System.exit(0);
				}
			}
			while(exit);
			
			do
			{
				if(turnPlayer.getTrappedStatus() == true)
				{
					boolean stillTrapped = giveTrapTurn(turnPlayer);
					if(stillTrapped)
						break;
				}
				
				if(turnPlayer.isImpounded())
				{
					boolean stillImpounded = giveImpoundTurn(turnPlayer);
					if(stillImpounded)
						break;
				}
				
				//if (turnPlayer.getNumMoves() + 1) % eatingFrequency == 0
				//if(turnPlayer.getFoodUnits() >= baseMetabolism)
				//{give opportunity to forage, advance, dig for buried nuts, } else {player must forage}
				
				//get the roll for this player with 2 dice
				int rollResult = rollDice(2, turnPlayer);
				
				JOptionPane.showMessageDialog(gameDisplay, turnPlayer.getName() + " rolls " + formatIndefiniteArticle(rollResult) + ".");
				clearScreen(gameDisplay);
				
				if(turnPlayer.getNumDoublesRolled() > 2)
				{
					turnPlayer.zeroOutDoubles();
					
					//send SquirrelPlayer to Animal Control
					JOptionPane.showMessageDialog(gameDisplay, turnPlayer.getName() + " has rolled 3 consecutive doubles while navigating. " + turnPlayer.getSubjectPronoun(true) + " pestered the wrong bird-feeder and has been apprehended by Animal Control.");
					apprehend(turnPlayer);
					
					break;
				}
				
				executeMove(rollResult, turnPlayer);	
				
				//announce where the player is on the gameboard
				System.out.println(turnPlayer.getName() + " has arrived at " + myBoard.getLocationName(turnPlayer.getGamePosition()));
				
				//if the space is occupied by another player, 
				if(!spaceClearFor(turnPlayer.getGamePosition(), turnPlayer))
				{
					int prevPos = turnPlayer.getGamePosition();
					//we give the opportunity for breeding if opposite genders
					//You've found a potential mate! Wanna get squirrelly?
					
					//In order to breed: (1) genders must be opposite (2) both players must have passed Proceed once
					//					(3)8/(365*4+1) chance
					
					//bounce-off, as we never have 2 squirrels in the same space
					bounceToNeighboringSpot(turnPlayer);
					int newPos = turnPlayer.getGamePosition();
					JOptionPane.showMessageDialog(gameDisplay, "Position " + prevPos + " was occupied, so " + turnPlayer.getName()
							+ " bounced into position " + newPos + ".");
				}
				
				
				GameSpace space = myBoard.getGameSpaceAt(turnPlayer.getGamePosition());
				if(space instanceof LiveTrap)
				{
					((LiveTrap) space).applyEvent(turnPlayer, gameDisplay);
				}
				//apply the square's actions on this player
				//if it's a residence space, the person should be offered the chance to:
				//(1) claim as theirs if it's not owned and they have number of food units required
				//(2) forage
				//(3) rest in drey (if it's yours)
				//(4) bury food
				
				System.out.println("Game.giveTurn: " + turnPlayer.getName() + " " + turnPlayer.getGamePosition());
			}
			while(turnPlayer.getNumDoublesRolled() > 0);
		}
		else
		{
			JOptionPane.showMessageDialog(gameDisplay, turnPlayer.getName() + " is no longer with us. RIP.");
		}
		
		
		//COMMENT FOR DEBUGGING:
		//System.out.println("end of turn");
	}
	
	private void bounceToNeighboringSpot(SquirrelPlayer player)
	{
		Random rand = new Random();
		ArrayList<Integer> nearbyOpenSpots = new ArrayList<Integer>();
		
		int currPos = player.getGamePosition();
		
		//do not allow the position to go backward behind the go spot
		for(int offset = 1; offset < 4; offset++)
		{
			int posOffset = (currPos + offset) % myBoard.getNumSpaces();
			if(spaceClearFor(posOffset, player))
				nearbyOpenSpots.add(Integer.valueOf(offset));
			
			if(currPos - offset >= 0)
			{
				if(spaceClearFor(currPos - offset, player))
					nearbyOpenSpots.add(Integer.valueOf(-1 * offset));
			}
		}
		
		int chosenOffset = nearbyOpenSpots.get(rand.nextInt(nearbyOpenSpots.size()));
		
		executeMove(chosenOffset, player);
	}
	
	//update the move by changing the player's stored position and updating this on the game display
	private void executeMove(int rollResult, SquirrelPlayer player)
	{
		//passProceed stores whether the player has completed a lap around the board
		boolean passProceed = player.advanceSpaces(rollResult, myBoard.getNumSpaces());
		
		//update the move on the game display
		gameDisplay.updateMyPosition(player);
		
		player.incrementMoves();
		
		if(passProceed)
		{
			passProceed(player);
		}
	}
	
	//process a turn for player when player is in a live trap; return true if he/she remains in trap, false otherwise
	private boolean giveTrapTurn(SquirrelPlayer player)
	{
		player.incrementTrapTurns();
		if(player.getTrapTurns() > player.getTotalTrapTurns())
		{
			player.setTrappedStatus(false);
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has been released from the trap and may now resume regular turns.");
			return false;
		}
		else
		{
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " is stuck in a live trap and skips turn " + player.getTrapTurns() +
					" of " + player.getTotalTrapTurns() + ".");
			return true;
		}
	}
	
	//process a turn for player when player is in Animal Control custody;
	//return true if he/she remains in custody, false otherwise
	private boolean giveImpoundTurn(SquirrelPlayer player)
	{
		if(player.getImpoundTurns() >= IMPOUND_ESCAPE_LIMIT)
		{
			player.setImpoundStatus(false);
			player.setFoodUnits(0);
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + 
					" has been freed by the Free Nibblers movement. All of" +
					" your on-hand food was jettisoned in your escape.");
			return false;
		}
		else
		{
			boolean escape;
			
			if(player.isUserPlayer())
			{
				String premise = player.getName() + " is in Animal Control custody. " +
						"Please select how you will proceed.";
				
				//allow player to decide to escape or wait
				Object[] choices = {"Attempt to escape", "Exchange all food for freedom"};
				escape = JOptionPane.showOptionDialog(gameDisplay, premise, player.getName() + " in Animal Control custody!", 
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]) == 0;
			}
			else
			{
				if(player.getCurrentFood() > 1)
				{
					Random rand = new Random();
					escape = rand.nextBoolean();
				}
				else
					escape = true;
			}
			
			if(escape)
			{
				rollDice(2, player);
				
				//successful escape
				if(player.getNumDoublesRolled() > 0)
				{
					player.zeroOutDoubles();
					player.setImpoundStatus(false);
					return false;
				}
				else	//unsuccessful escape
				{
					player.incrementImpoundTurns();
					return true;
				}
			}
			else	//exchange your food for freedom
			{
				boolean satisfactory = (player.getCurrentFood() >= rollDie()/2);
				player.setFoodUnits(0);
				
				if(satisfactory)
				{
					player.setImpoundStatus(false);
					JOptionPane.showMessageDialog(gameDisplay, "The Squirrel Mafia was satisfied with the food offering " + 
							player.getName() + " made. They have secured " + player.getPossessivePronoun() + " freedom.");
					return false;
				}
				else
				{
					int injurySum = rollDie() + rollDie();
					player.setCurrentHealth(player.getCurrentHealth() - injurySum);
					JOptionPane.showMessageDialog(gameDisplay, "Unfortunately, the Squirrel Mafia was unimpressed with the food offering " + 
							player.getName() + " made. They took all of " + player.getPossessivePronoun() + " food and beat " +
							player.getObjectPronoun() + " for " + injurySum + " health points.");
					player.incrementImpoundTurns();
					return true;
				}
			}
		}
	}
	
	private void passProceed(SquirrelPlayer player)
	{
		player.incrementLapsCompleted();
		
		boolean restoreHealth;
		
		if(player.isUserPlayer())
		{
			Object[] choices = {"Restore health", "Load up on food"};
			restoreHealth = JOptionPane.showOptionDialog(gameDisplay, player.getName() + " passed the PROCEED space. Choose one of the following benefits to aid you on your journey:", player.getName() + " passed PROCEED!", 
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]) == 0;
		}
		else
		{
			if(player.getCurrentHealth() < player.getMyMaxHealth())
			{
				if(player.getMyMaxHealth() < SquirrelPlayer.getAllSquirrelMaxHealth() ||
						((double)player.getCurrentHealth())/player.getMyMaxHealth() < 0.8)
				{
					restoreHealth = true;
				}
				else
				{
					if(player.getCurrentFood() == player.getMaxFoodCapacity())
						restoreHealth = true;
					else
					{
						if(player.getCurrentFood() <= 1)
							restoreHealth = false;
						else
						{
							Random rand = new Random();
							restoreHealth = rand.nextBoolean();
						}
					}
				}
			}
			else
				restoreHealth = false;
		}
		
		if(restoreHealth)
		{
			player.setCurrentHealth(player.getMyMaxHealth());
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has passed PROCEED and has decided to " +
					"restore " + player.getPossessivePronoun() + " health.");
		}
		else
		{
			player.setFoodUnits(player.getMaxFoodCapacity());
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has passed PROCEED and has decided to " +
					"restock " + player.getPossessivePronoun() + " carried food stores.");
		}
		//passProceed method works in 2 parts: (1) allow user to regain health OR find __ food units; 
		// (2) increment number of go-passes for the player (and if appropriate, change to sexually mature)
	}
	
	private void apprehend(SquirrelPlayer player)
	{		
		//if animal control space is open, move player to this space
		if(spaceClearFor(myBoard.getAnimalControlSpaceNum(), player))
		{
			player.setImpoundStatus(true);
			player.setGamePosition(myBoard.getAnimalControlSpaceNum());
			gameDisplay.updateMyPosition(player);
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " is being held at the Animal Control facility.");
		}
		else
		{
			if(spaceClearFor(myBoard.getOverflowSpaceNum(), player))
			{
				player.setImpoundStatus(true);
				player.setGamePosition(myBoard.getOverflowSpaceNum());
				gameDisplay.updateMyPosition(player);
				JOptionPane.showMessageDialog(gameDisplay, player.getName() + " is being held at the Animal Control Overflow facility.");
			}
			else
			{
				JOptionPane.showMessageDialog(gameDisplay, "There was no space at Animal Control facilities. " + player.getName() + " has been put down by Animal Control.");
				player.setCurrentHealth(0);
				player.setGamePosition(-1);
				gameDisplay.updateMyPosition(player);
			}
		}
	}
	
	/*
	 * pre-condition: num < 80
	 * post-condition: returns the string formatted with the appropriate preceding indefinite article (a or an)
	 */
	private String formatIndefiniteArticle(int num)
	{
		switch(num)
		{
			case 8:
			case 11:
			case 18:
				return "an " + num;
			default:
				return "a " + num;
		}
	}
	
	//check if gameboard space at position spaceNum is available for SquirrelPlayer player;
	//returns true if it's not occupied by another player; false otherwise
	private boolean spaceClearFor(int spaceNum, SquirrelPlayer player)
	{
		for(SquirrelPlayer sp : players)
		{
			if(sp.getPlayerID() != player.getPlayerID())
			{
				if(sp.getGamePosition() == spaceNum)
					return false;
			}
		}
		
		return true;
	}
	
	private int rollDice(int numDice, SquirrelPlayer roller)
	{
		int[] rollResults = new int[numDice];
		int sum = 0;
		
		
		for(int i = 0; i < numDice; i++)
		{
			rollResults[i] = rollDie();
			sum += rollResults[i];
		}
		
		if(numDice == 2)
		{
			if(rollResults[0] == rollResults[1])
			{
				roller.incrementDoubles();
			}
			else
			{
				roller.zeroOutDoubles();
			}
		}
		
		displayDiceRoll(rollResults, gameDisplay);
		
		System.out.println("Game.rollDice(): " + roller.getName() + ": " + sum);
		
		return sum;
	}
	
	private boolean gameOver()
	{
		int numPlayersAlive = 0;
		for(SquirrelPlayer sp : players)
		{
			if(sp.isAlive())
			{
				numPlayersAlive++;
			}
		}
		
		return numPlayersAlive < 2;
	}
	
	public static int rollDie()
	{
		Random rand = new Random(System.nanoTime());
		return Math.abs(rand.nextInt()) % 6 + 1;
	}
	
	public static void displayDiceRoll(int[] rolls, GameGUI activeGUI)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		for(int i = 0; i < rolls.length; i++)
		{
			JLabel label_i = new JLabel(new ImageIcon(activeGUI.getClass().getResource("dice_pics/sroll" + rolls[i] + ".jpeg")));
			panel.add(label_i);
		}
		
		activeGUI.add(panel);
		activeGUI.pack();
		activeGUI.setVisible(true);
	}
	
	private static void clearScreen(GameGUI activeGUI)
	{
		activeGUI.repaint();
	}

	/*private static boolean exit()
	{
		return JOptionPane.showConfirmDialog(null, "Are you sure that you want to quit?",
			"EXIT?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}*/
}
