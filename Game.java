
import java.awt.BorderLayout;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class Game
{
	private final String RES_CLAIM = "CLAIM PROPERTY", RES_FORAGE = "FORAGE",
			RES_BURY = "HIDE/BURY FOOD", RES_REST = "REST", RES_RAID = "TAKE-OVER PROPERTY";
	
	private final int NUM_PLAYERS = 4, IMPOUND_ESCAPE_LIMIT = 3;
	private GameBoard myBoard;
	private GameGUI gameDisplay;
	private ArrayList<SquirrelPlayer> players;
	private String claimStr, raidStr;
	
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
		
		displayFinalStats();
	}
	
	public GameGUI getDisplay()
	{
		return gameDisplay;
	}
	
	public GameBoard getBoard()
	{
		return myBoard;
	}
	
	private void displayFinalStats()
	{
		int[] points = calculatePoints();
		
		String results = "FINAL SCORES:\n";
		for(int i = 0; i < players.size(); i++)
		{
			results += players.get(i).getName();
			results += ": ";
			results += points[i];
			results += " points\n";
		}
		
		JOptionPane.showMessageDialog(gameDisplay, results);
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
	
	//pre-condition: turnPlayer is a live player (health points > 0)
	private void giveTurn(SquirrelPlayer turnPlayer)
	{
		System.out.println("ENTER giveTurn: " + turnPlayer.getName() + " turn# " + turnPlayer.getNumMoves());
		int choiceIndex;
		do
		{
			Object[] choices = {"OK", "REVIEW RULES", "EXIT"};
			choiceIndex = JOptionPane.showOptionDialog(gameDisplay, "It's " + turnPlayer.getName() + "\'s turn." +
					getFoodHealthStatus(turnPlayer), "Proceed to " + turnPlayer.getName() + "\'s turn",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]);
			if(choiceIndex == 1)
			{
				displayRules();
			}
			
			if(choiceIndex == 2)
			{
				if(GameGUI.exit(gameDisplay))
					System.exit(0);
			}
		}
		while(choiceIndex != 0);
			
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
			
			int doubles_before = turnPlayer.getNumDoublesRolled();
			
			//get the roll for this player with 2 dice
			int rollResult = rollDice(2, turnPlayer);
			
			int doubles_after = turnPlayer.getNumDoublesRolled();
			
			String doubleWarning = getDoubleWarning(turnPlayer, doubles_before, doubles_after);
				
			JOptionPane.showMessageDialog(gameDisplay, turnPlayer.getName() + " rolls " +
					formatIndefiniteArticle(rollResult) + ".");
			if(!(doubleWarning.equals("")))
			{
				JOptionPane.showMessageDialog(gameDisplay, doubleWarning, "WARNING!",
						JOptionPane.WARNING_MESSAGE);
			}
			
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
					//we give the opportunity for breeding if opposite genders
					//You've found a potential mate! Wanna get squirrelly?
					
					//In order to breed: (1) genders must be opposite (2) both players must have passed Proceed once
					//					(3)8/(365*4+1) chance
				
				bounce(turnPlayer);
			}
				
			GameSpace space = myBoard.getGameSpaceAt(turnPlayer.getGamePosition());
			if(space instanceof EventSpace)
				((EventSpace)space).applyEvent(turnPlayer, this);
			else	//this is a residence space; provide residence options
				takeResidenceActions(turnPlayer, space);
		}
		while(turnPlayer.getNumDoublesRolled() > 0 && turnPlayer.isAlive());
		
		System.out.println("EXIT giveTurn: " + turnPlayer.getName());
	}
	
	public String getDoubleWarning(SquirrelPlayer turnPlayer, int doubles_before, int doubles_after)
	{
		String doubleWarning = "";
		if(doubles_after > doubles_before && doubles_after < 3)
		{
			doubleWarning += turnPlayer.getName() + " rolled a double and will thus receive an extra roll " +
					"for this turn. ";
			if(doubles_after == 2)
			{
				doubleWarning += "This is " + turnPlayer.getPossessivePronoun() + " second straight " +
						"double rolled. ";
			}
			
			doubleWarning += (turnPlayer.isUserPlayer()) ? "You" : turnPlayer.getSubjectPronoun(true);
			
			doubleWarning += 
					" should proceed with caution,\nas such oddities draw attention. After rolling 3 " +
					"straight doubles, a squirrel will be apprehended by Animal Control.";
		}
		
		return doubleWarning;
	}
	
	public void takeResidenceActions(SquirrelPlayer player, GameSpace space)
	{
		String choice = getResidenceSpaceOption(player);
		int additionalInfoIndex = choice.indexOf(" (");
		if(additionalInfoIndex != -1)
			choice = choice.substring(0, additionalInfoIndex);
		
		switch(choice)
		{
			//add an option for upgrading spaces to main drey
			//if(player.getDreys().size() > 1 && player.getMainDrey().getNum() != player.getGamePosition())
		
			case RES_CLAIM:
				ResidenceSpace r_spc = (ResidenceSpace) space;
				r_spc.setOwner(player);
				player.addDrey(r_spc, gameDisplay);
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
	
	private String getFoodHealthStatus(SquirrelPlayer player)
	{
		return "\nFOOD: " + player.getCurrentFood() + "/" + player.getMaxFoodCapacity() +
		"\nHEALTH: " + player.getCurrentHealth() + "/" + player.getMyMaxHealth();
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
			{
				options = unownedCanBuy;
				options[0] = RES_CLAIM + " (Cost: " + spc.getCost() + " food)";
			}
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
				int boost = (player.getDreys().get(0).getSpaceNum() == player.getGamePosition()) ? 3 : 2;
				options[2] = RES_REST + " (+" + boost + " health points)";
				
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
					{
						options = foreign;
						options[2] = RES_RAID + " (Cost: " + (player.getCurrentFood() + 1)/2 + " food)";
					}
					else
						options = foreignPeace;
				}
				
				if((rand.nextDouble() < 0.2) && (player.getCurrentFood() > player.getMaxFoodCapacity()/2) && 
						(player.getCurrentHealth() > (3 * player.getMyMaxHealth() / 4)))
					optionsIndex = 2;
				else
					optionsIndex = 0;
			}	
		}

		if(player.isUserPlayer())
		{
			do
			{
				String welcomeStr = "";
				if(player.inDrey())
					welcomeStr = "your drey at ";
				else
				{
					if(spc.getOwner() != null)
						welcomeStr = spc.getOwner().getName() + "\'s drey at ";
				}
				
				optionsIndex = JOptionPane.showOptionDialog(gameDisplay, player.getName() + ", welcome to " +
					welcomeStr + spc.getCode() + "! Please choose an option at this space: ", "Welcome to " + 
					spc.getCode(), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			}
			while(optionsIndex < 0 || optionsIndex >= options.length);
		}
		else
		{
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " has arrived at " + spc.getCode());
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
			prevOwner.loseDrey(resSpc, gameDisplay);
			resSpc.setOwner(player);
			player.addDrey(resSpc, gameDisplay);
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
	
	public int getNearbyOpenUnownedSpaceNum(int currentPos)
	{
		int offset = 1;
		do
		{
			int vacantPlus = (currentPos + offset) % myBoard.getNumSpaces(),
					vacantMinus = (currentPos - offset + myBoard.getNumSpaces()) % myBoard.getNumSpaces();
			
			if(isEmpty(vacantPlus) && myBoard.getGameSpaceAt(vacantPlus) instanceof ResidenceSpace &&
					((ResidenceSpace)(myBoard.getGameSpaceAt(vacantPlus))).getOwner() == null)
				return vacantPlus;
			
			if(isEmpty(vacantMinus) && myBoard.getGameSpaceAt(vacantMinus) instanceof ResidenceSpace &&
					((ResidenceSpace)(myBoard.getGameSpaceAt(vacantMinus))).getOwner() == null)
				return vacantMinus;
			
			offset++;
		}
		while(offset < myBoard.getNumSpaces()/2);
		
		return -19;
	}
	
	public int getNearbyOpenResSpaceNum(int currentPos)
	{
		int offset = 1;
		do
		{
			int vacantPlus = (currentPos + offset) % myBoard.getNumSpaces(),
					vacantMinus = (currentPos - offset + myBoard.getNumSpaces()) % myBoard.getNumSpaces();
			
			if(isEmpty(vacantPlus) && myBoard.getGameSpaceAt(vacantPlus) instanceof ResidenceSpace)
				return vacantPlus;
			
			if(isEmpty(vacantMinus) && myBoard.getGameSpaceAt(vacantMinus) instanceof ResidenceSpace)
				return vacantMinus;
			
			offset++;
		}
		while(offset < myBoard.getNumSpaces()/2);
		
		return currentPos;
	}
	
	private boolean isEmpty(int location)
	{
		return gameDisplay.isEmptyAt(location);
	}
	
	private void bounce(SquirrelPlayer turnPlayer)
	{
		int prevPos = turnPlayer.getGamePosition();
		String prevLocName = myBoard.getLocationName(turnPlayer.getGamePosition());
		
		//bounce-off, as we never have 2 squirrels in the same space
		bounceToNeighboringSpot(turnPlayer);
		int newPos = turnPlayer.getGamePosition();
		String newLocName = myBoard.getLocationName(turnPlayer.getGamePosition());
		String direction;
		if((newPos - prevPos) > -4 && (newPos - prevPos) < 0)
			direction = "backward";
		else
			direction = "forward";
		String plural = (Math.abs(newPos - prevPos) == 1) ? " " : "s ";
		
		JOptionPane.showMessageDialog(gameDisplay, prevLocName + " was occupied, so " + 
				turnPlayer.getName() + " bounced " + direction + " " + Math.abs(newPos - prevPos) +
				" space" + plural + "to " + newLocName + ".");
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
		
		//if the player completed a lap around the board, do the operations for the pass
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
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + " is stuck in a live trap and " +
					"skips turn " + player.getTrapTurns() + " of " + player.getTotalTrapTurns() + "." +
					player.getSubjectPronoun(true) + " also loses 1 health point due to exposure and " +
					"anxiety from being confined.");
			player.setCurrentHealth(player.getCurrentHealth() - 1, this);
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
			String foodImpact = "";
			if(player.getCurrentFood() > 0)
			{
				foodImpact += player.getSubjectPronoun(true) + " jettisoned " + player.getPossessivePronoun() +
						" " + player.getCurrentFood() + " unit";
				foodImpact += (player.getCurrentFood() == 1) ? " " : "s ";
				foodImpact += "of food in order to facilitate " + player.getPossessivePronoun() + " escape.";
				player.setFoodUnits(0);
			}
			
			JOptionPane.showMessageDialog(gameDisplay, player.getName() + 
					" has been freed from Animal Control by the mercy of the Free Nibblers movement. " +
					foodImpact);
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
				Object[] choices = {"Attempt to escape", "Exchange all food (" + player.getCurrentFood() + " food unit(s)) for freedom"};
				escape = JOptionPane.showOptionDialog(gameDisplay, premise, player.getName() +
						" in Animal Control custody!", JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]) == 0;
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
					JOptionPane.showMessageDialog(gameDisplay, player.getName() + " tried and escaped from " +
							"Animal Control custody!");
					return false;
				}
				else	//unsuccessful escape
				{
					int illness = rand.nextInt(2) + 1;
					String encouragement = (illness >= player.getCurrentHealth()) ? "" :
						" Don't give up, little squirrel!";
					JOptionPane.showMessageDialog(gameDisplay, player.getName() + " tried to escape from " +
					"Animal Control custody but was unsuccessful. Due to the poor conditions in the Animal " +
					"Control facilities, " + player.getSubjectPronoun(false) + " lost " + illness + 
					" health point(s) during this turn." + encouragement);
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
			int healthRestore = Math.min(5, player.getMyMaxHealth() - player.getCurrentHealth());
			String plural = (healthRestore == 1) ? "" : "s";
			Object[] choices = {"Restore health (+" + healthRestore + " health point" + plural + ")", "Fill up on food"};
			restoreHealth = JOptionPane.showOptionDialog(gameDisplay, player.getName() +
					" passed the PROCEED space with the following status measures:" +
					getFoodHealthStatus(player) + "\nChoose one of the following benefits to aid you " +
					"on your journey:", player.getName() + " passed PROCEED!", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, choices, choices[0]) == 0;
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
	
	//display rules in a modal window
	private void displayRules()
	{
		JPanel panel = new JPanel();
		
		String allRules = "";
		ArrayList<String> rulesByLine = Squirrelopoly.textFileToArrayList("src/GameRules.txt");
		for(int i = 0; i < rulesByLine.size(); i++)
		{
			allRules += rulesByLine.get(i);
			allRules += "\n";
		}
		
		//read from GameRules.txt
		JTextArea textArea = new JTextArea(allRules);
		textArea.setEditable(false);
		textArea.setSize(new Dimension(500, 500));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.setPreferredSize(new Dimension(15, 0));;
		//scrollPane.setVerticalScrollBarPolicy();
		//panel.add(scrollPane, BorderLayout.CENTER);
		
		final JDialog frame = new JDialog(gameDisplay, "Rules", true);
		frame.setPreferredSize(new Dimension(500, 500));
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setLocationRelativeTo(gameDisplay);
		frame.setVisible(true);
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
