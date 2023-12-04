
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
	private final String RES_CLAIM = "CLAIM PROPERTY", RES_FORAGE = "FORAGE",
			RES_BURY = "HIDE/BURY FOOD", RES_REST = "REST", RES_RAID = "TAKE-OVER PROPERTY";
	
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
		
		int currentPlayerID = getFirstPlayerID();
		gameDisplay.setActiveTurnPlayer(currentPlayerID);
		
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
			int[] points = calculatePoints();
			
			String results = "";
			for(int i = 0; i < players.size(); i++)
			{
				results += players.get(i).getName();
				results += ": ";
				results += points[i];
				results += " points\n";
			}
			
			JOptionPane.showMessageDialog(gameDisplay, results);
		}
		
	}
	
	public GameGUI getDisplay()
	{
		return gameDisplay;
	}
	
	public GameBoard getBoard()
	{
		return myBoard;
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
		System.out.println(turnPlayer.getName() + " turn# " + turnPlayer.getNumMoves());
		boolean exit;
		do
		{
			Object[] choices = {"OK", "EXIT"};
			exit = JOptionPane.showOptionDialog(gameDisplay, "It's " + turnPlayer.getName() + "\'s turn.\nFOOD: " + turnPlayer.getCurrentFood() + "/" + turnPlayer.getMaxFoodCapacity() + "\nHEALTH: " + turnPlayer.getCurrentHealth() + "/" + turnPlayer.getMyMaxHealth(), "Proceed to " + turnPlayer.getName() + "\'s turn", 
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
				
			int eatingFrequency = 5, baseMetabolism = 2;
			if((turnPlayer.getNumMoves() + 1) % eatingFrequency == 0)
			{
				if(turnPlayer.getCurrentFood() >= baseMetabolism)
					turnPlayer.addFoodUnits(-1 * baseMetabolism);
				else
				{
					turnPlayer.setFoodUnits(0);
					
					if(myBoard.getGameSpaceAt(turnPlayer.getGamePosition()) instanceof ResidenceSpace)
					{
						JOptionPane.showMessageDialog(gameDisplay, turnPlayer.getName() + " is out of food and getting hungry. " +
							turnPlayer.getSubjectPronoun(true) + " will spend a turn foraging.");
						
						forage(turnPlayer);
						if(turnPlayer.getCurrentFood() > 0)
							turnPlayer.incrementMoves();
						
						break;
					}
				}
			}
				
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
			if(space instanceof EventSpace)
				((EventSpace)space).applyEvent(turnPlayer, this);
			else	//this is a residence space; provide residence options
				takeResidenceActions(turnPlayer, space);
		}
		while(turnPlayer.getNumDoublesRolled() > 0);
	}
	
	public void takeResidenceActions(SquirrelPlayer player, GameSpace space)
	{
		String choice = getResidenceSpaceOption(player);
		
		switch(choice)
		{
			//add an option for upgrading spaces to main drey
			//if(player.getDreys().size() > 1 && player.getMainDrey().getNum() != player.getGamePosition())
		
			case RES_CLAIM:
				ResidenceSpace r_spc = (ResidenceSpace) space;
				r_spc.setOwner(player);
				player.addDrey(r_spc);
				player.addFoodUnits(-1 * r_spc.getCost());
				JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has taken up residence at " +
						r_spc.getCode());
				break;
			case RES_FORAGE:
				forage(player);
				break;
			case RES_BURY:
				bury(player);
				break;
			case RES_REST:
				rest(player);
				break;
			default:	//case RES_RAID
				raid(player);
				break;
		}
	}
	
	private String getResidenceSpaceOption(SquirrelPlayer player)
	{
		//add "No Action" option; make RES_CLAIM and RES_BURY available dependent on whether they can be done:
		//if you have sufficient food or any food, respectively
		Random rand = new Random();
		String[] options;
		ResidenceSpace spc = (ResidenceSpace)(myBoard.getGameSpaceAt(player.getGamePosition()));
		
		String[] unownedCanBuy = {RES_CLAIM, RES_FORAGE, RES_BURY},
				unownedNoBuy = {RES_FORAGE, RES_BURY},
				domestic = {RES_FORAGE, RES_BURY, RES_REST},
				foreign = {RES_FORAGE, RES_BURY, RES_RAID},
				foreignPeace = {RES_FORAGE, RES_BURY};
		int optionsIndex;
		
		if(spc.getOwner() == null)
		{
			System.out.println(spc.getCode() + " " + spc.getCost());
			if(player.isUserPlayer() && player.getCurrentFood() < spc.getCost())
				options = unownedNoBuy;
			else
				options = unownedCanBuy;
			
			//simplistic decision-making for NPCs:
			//75% chance to claim the spot if you have price to buy/25% to forage
			//if you don't, have the price, then 90% chance to forage/10% to bury
			if(player.getCurrentFood() >= spc.getCost())
				optionsIndex = (rand.nextDouble() < 0.75) ? 0 : 1;
			else
				optionsIndex = (rand.nextDouble() < 0.9) ? 1 : 2;
		}
		else
		{
			if(spc.getOwner().getPlayerID() == player.getPlayerID())
			{
				options = domestic;
				
				//all determinations are deterministic if you're in your own space
				if(player.getCurrentHealth() < player.getMyMaxHealth())
					optionsIndex = 2;
				else
				{
					if(player.getCurrentFood() < player.getMaxFoodCapacity())
						optionsIndex = 0;
					else
						optionsIndex = 1;
				}
			}
			else
			{
				if(!(player.isUserPlayer()))
					options = foreign;
				else
				{
					//if you have more than half food-max and over 3/4 health max, you can raid
					if(player.getCurrentFood() > player.getMaxFoodCapacity()/2 && 
							player.getCurrentHealth() > (3 * player.getMyMaxHealth() / 4))
						options = foreign;
					else
						options = foreignPeace;
				}
				
				if(rand.nextDouble() < 0.9)
					optionsIndex = 0;
				else
					optionsIndex = 2;
			}	
		}

		if(player.isUserPlayer())
		{
			do
			{
				optionsIndex = JOptionPane.showOptionDialog(gameDisplay, "Please choose an option at this space: ", "Welcome to " + 
					spc.getCode(), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			}
			while(optionsIndex < 0 || optionsIndex >= options.length);
		}
		
		System.out.println(player.getName() + " optionsIndex: " + optionsIndex + " " + options[optionsIndex]);
		
		return options[optionsIndex];
	}
	
	private void rest(SquirrelPlayer player)
	{
		//make sure this is this player's drey
		if(player.inDrey())	//or player is pregnant/nursing and in partner drey
		{
			int healthBoost = 2;
			
			//if you're in the home drey, add another point to healthBoost
			if(player.getDreys().get(0).getSpaceNum() == player.getGamePosition())
				healthBoost++;
			
			player.setCurrentHealth(player.getCurrentHealth() + healthBoost, this);
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has rested in " +
					player.getPossessivePronoun() + " drey and regained " + healthBoost + " health points.");
		}
	}
	
	private void raid(SquirrelPlayer player)
	{
		ResidenceSpace resSpc = (ResidenceSpace)(myBoard.getGameSpaceAt(player.getGamePosition()));
		
		if(resSpc.getOwner() != null)
		{
			SquirrelPlayer prevOwner = resSpc.getOwner();
			prevOwner.loseDrey(resSpc);
			resSpc.setOwner(player);
			player.addDrey(resSpc);
			player.addFoodUnits(-1 * (player.getCurrentFood() + 1)/2);
			
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has raided " +
					prevOwner.getName() + "\'s drey and taken it over.");
		}
	}
	
	private void bury(SquirrelPlayer player)
	{
		if(player.getCurrentFood() > 0)
		{
			GameSpace spc = myBoard.getGameSpaceAt(player.getGamePosition());
			if(spc instanceof ResidenceSpace)
			{
				player.addFoodUnits(-1);
				((ResidenceSpace) spc).hideFood(player, 1);
				
				JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has buried food at " +
						spc.getCode());
			}
		}
	}
	
	private void forage(SquirrelPlayer player)
	{
		System.out.println("Forage");
		GameSpace current = myBoard.getGameSpaceAt(player.getGamePosition());
		if(!(current instanceof EventSpace))	//if this is a residence space:
		{
			System.out.println("Forage-if");
			Random rand = new Random();
			
			//see who owns it
			ResidenceSpace livingSpot = (ResidenceSpace) current;
			
			int foragingFinds;
			
			//if the space is unowned
			if(livingSpot.getOwner() == null)
			{
				foragingFinds = rand.nextInt(2);
			}
			else
			{
				//if the space belongs to player
				if(livingSpot.getOwner().getPlayerID() == player.getPlayerID())
				{
					if(livingSpot.getCost() > 1)
						foragingFinds = rand.nextInt(livingSpot.getCost());
					else
					{
						if(rand.nextDouble() < 0.5)
							foragingFinds = rand.nextInt(2);
						else
							foragingFinds = 0;
					}
					
					//add a unit of hidden food to the amount foraged
					if(livingSpot.getNumHiddenFoodUnits() > 0)
					{
						livingSpot.hideFood(player, -1);
					}
				}
				else	//it's someone else's space
				{
					foragingFinds = rand.nextInt(2);
				}
			}
			
			player.addFoodUnits(foragingFinds);
			livingSpot.incrementForagingVisits();
			String descriptor = (foragingFinds > 0) ? " a successful " : " an unsuccessful ";
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " had" + descriptor +
					"foraging expedition:\n+" + foragingFinds + " food units");
		}
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
			Random rand = new Random();
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
					int illness = rand.nextInt(3);
					JOptionPane.showMessageDialog(gameDisplay, player.getName() + " lost " + illness + " health point(s) during " + player.getPossessivePronoun() + " recent turn in Animal Control.");
					player.setCurrentHealth(player.getCurrentHealth() - illness, this);
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
					JOptionPane.showMessageDialog(gameDisplay, "Unfortunately, the Squirrel Mafia was unimpressed with the food offering " + 
							player.getName() + " made. They took all of " + player.getPossessivePronoun() + " food and beat " +
							player.getObjectPronoun() + " for " + injurySum + " health points.");
					player.setCurrentHealth(player.getCurrentHealth() - injurySum, this);
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
			player.setCurrentHealth(player.getCurrentHealth() + 5, this);
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has passed PROCEED and has decided to " +
					"restore " + player.getPossessivePronoun() + " health.");
		}
		else
		{
			player.setFoodUnits(player.getMaxFoodCapacity());
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has passed PROCEED and has decided to " +
					"restock " + player.getPossessivePronoun() + " carried food stores.");
		}
	}
	
	public void apprehend(SquirrelPlayer player)
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
				player.setCurrentHealth(0, this);
			}
		}
	}
	
	private int[] calculatePoints()
	{
		int[] scores = new int[players.size()];
		
		for(int i = 0; i < players.size(); i++)
		{
			SquirrelPlayer player_i = players.get(i);
			scores[i] = 0;
			
			scores[i] += (player_i.getNumLapsCompleted() * 5);
			scores[i] += (player_i.getDreys().size());
			
			if(player_i.isAlive())
				scores[i] += 10;
		}
		//num laps completed * 5
		//num dreys * 1
		//num children * 5 (if male)
		//num children * 10 (if female)
		
		//if you're the last one alive, +10-15 points
		
		return scores;
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
}
