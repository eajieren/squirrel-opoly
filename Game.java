
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
	private final int NUM_PLAYERS = 4;
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
		
		for(int turns = 0; turns < 20; turns++)
		//while(!gameOver())
		{
			giveTurn(players.get((currentPlayerID++) % NUM_PLAYERS));
		}
		
		if(gameOver())
			gameDisplay.setGameInPlay(false);
		
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
			//get the roll for this player with 2 dice
			int rollResult = rollDice(2, turnPlayer);
			
			JOptionPane.showMessageDialog(gameDisplay, turnPlayer.getName() + " rolls " + formatIndefiniteArticle(rollResult) + ".");
			clearScreen(gameDisplay);
			
			if(turnPlayer.getNumDoublesRolled() > 2)
			{
				//send SquirrelPlayer to Animal Control
				break;
			}
			
			//update the move within the Player, on the board, and in the game, checking to see whether it's occupied
			boolean passProceed = turnPlayer.advanceSpaces(rollResult, myBoard.getNumSpaces());
			
			gameDisplay.updateMyPosition(turnPlayer);
			
			//String proceedString = "";
			//if(passProceed)
			//	proceedString = passProceed(turnPlayer);
			//passProceed method works in 3 parts: (1) allow user to regain health OR find __ food units; 
			// (2) increment number of go-passes for the player (and if appropriate, change to sexually mature;
			// AND (3) return a String summarizing what happened as a result of passing Proceed
			
			//announce where the player is on the gameboard
			System.out.println(/*proceedString +*/ turnPlayer.getName() + " has arrived at " + myBoard.getLocationName(turnPlayer.getGamePosition()));
			
			//if the space is occupied by another player, 
			if(!spaceClearFor(turnPlayer.getGamePosition(), turnPlayer))
			{
				//we give the opportunity for breeding if opposite genders
				//You've found a potential mate! Wanna get squirrelly?
				//In order to breed: (1) both players must have passed Proceed once
				//					(2)8/(365*4+1) chance
				
				//bounce-off, as we never have 2 squirrels in the same space
			}
			
			System.out.println("Game.giveTurn: " + turnPlayer.getName() + " " + turnPlayer.getGamePosition());
		}
		while(turnPlayer.getNumDoublesRolled() > 0);
		
		//COMMENT FOR DEBUGGING:
		//System.out.println("end of turn");
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
		return false;
	}
	
	private static int rollDie()
	{
		Random rand = new Random(System.nanoTime());
		return Math.abs(rand.nextInt()) % 6 + 1;
	}
	
	private static void displayDiceRoll(int[] rolls, GameGUI activeGUI)
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
