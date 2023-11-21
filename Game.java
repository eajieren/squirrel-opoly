
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
	private GameBoard myBoard;
	private GameGUI gameDisplay;
	ArrayList<SquirrelPlayer> players;
	
	public Game(GameBoard board, ArrayList<SquirrelPlayer> gamePlayers, GameGUI display)
	{
		myBoard = board;
		players = gamePlayers;
		gameDisplay = display;
	}
	
	public void play()
	{
		gameDisplay.pack();
		gameDisplay.setVisible(true);
		//reference comment; not meant to be uncommented
		//int[] result = {rollDie(), rollDie(), rollDie()};
		
		
		//int resultSum = rollDice(3, true);
		//System.out.print("Dice Roll Sum = " + resultSum);
	}
	
	private int rollDice(int numDice, boolean isUser)
	{
		int[] rollResults = new int[numDice];
		int sum = 0;
		
		for(int i = 0; i < numDice; i++)
		{
			rollResults[i] = rollDie();
			sum += rollResults[i];
		}
		
		if(isUser)
			displayDiceRoll(rollResults, gameDisplay);
		
		return sum;
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

	private static boolean exit()
	{
		return JOptionPane.showConfirmDialog(null, "Are you sure that you want to quit?",
			"EXIT?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
}
