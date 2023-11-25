import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameGUI extends JFrame implements KeyListener
{
	private static final int F_WIDTH = 799, F_HEIGHT = 799;
	private static final int HORIZ_BORDER = 53, VERT_BORDER = 53;
	private static final String GAME_NAME = "Squirrel-opoly";
	private static final Color[] COLORS = {Color.BLUE, Color.GRAY, Color.ORANGE, Color.MAGENTA};
	private final Color DEFAULT_BACKGROUND_COLOR;
	
	private final int BOARD_SPACE_DIM, NUM_SPACES;
	private boolean inPlay;
	private int[] positions = {0,0,0,0};
	
	public GameGUI(int numSpaces)
	{
		super(GAME_NAME);
		DEFAULT_BACKGROUND_COLOR = this.getBackground();
		
		BOARD_SPACE_DIM = (F_WIDTH - (2 * HORIZ_BORDER)) / (numSpaces / 4 + 1);
		NUM_SPACES = numSpaces;
		inPlay = false;
		//set customizable settings of Frame
		setPreferredSize(new Dimension(F_WIDTH, F_HEIGHT));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		GameGUI gui = this;
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if(exit(gui))
				{
					System.exit(0);
				}
         	}
        });
	}
	
	public boolean gameInPlay()
	{
		return inPlay;
	}
	
	public void setGameInPlay(boolean playStatus)
	{
		inPlay = playStatus;
	}
	
	public void paint(Graphics phics)
	{
		if(inPlay)
		{
			freshStart(phics);
			System.out.println("1");
			drawBoard(phics);
			System.out.println("2");
			drawFigures(phics);
			System.out.println("3");
		}
	}
	
	public void updateMyPosition(SquirrelPlayer player)
	{
		positions[player.getPlayerID()] = player.getGamePosition();
		System.out.println(player.getName() + " moves to " + player.getGamePosition());
		repaint();
	}
	
	private void freshStart(Graphics phics)
	{
		phics.setColor(DEFAULT_BACKGROUND_COLOR);
		phics.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void drawBoard(Graphics phics)
	{
		//colors white square with black border
		phics.setColor(Color.white);
		phics.fillRect(HORIZ_BORDER, VERT_BORDER, F_WIDTH - 2*HORIZ_BORDER, F_HEIGHT - 2*VERT_BORDER);
		phics.setColor(Color.black);
		phics.drawRect(HORIZ_BORDER, VERT_BORDER, F_WIDTH - 2*HORIZ_BORDER, F_HEIGHT - 2*VERT_BORDER);
		
		//colors smaller nested green square with black border
		phics.setColor(Color.green);
		phics.fillRect(HORIZ_BORDER + BOARD_SPACE_DIM, VERT_BORDER + BOARD_SPACE_DIM, F_WIDTH - 2*(HORIZ_BORDER + BOARD_SPACE_DIM), F_HEIGHT - 2*(VERT_BORDER + BOARD_SPACE_DIM));
		phics.setColor(Color.black);
		phics.drawRect(HORIZ_BORDER + BOARD_SPACE_DIM, VERT_BORDER + BOARD_SPACE_DIM, F_WIDTH - 2*(HORIZ_BORDER + BOARD_SPACE_DIM), F_HEIGHT - 2*(VERT_BORDER + BOARD_SPACE_DIM));
		
		//draw lines separating the board spaces
		for(int x = BOARD_SPACE_DIM; x < F_WIDTH - 2*HORIZ_BORDER; x += BOARD_SPACE_DIM)
		{
			phics.drawLine(HORIZ_BORDER + x,VERT_BORDER,HORIZ_BORDER + x,VERT_BORDER + BOARD_SPACE_DIM);
			phics.drawLine(HORIZ_BORDER + x,F_HEIGHT-VERT_BORDER-BOARD_SPACE_DIM,HORIZ_BORDER + x,F_HEIGHT-VERT_BORDER);
			
			int y = x;
			phics.drawLine(HORIZ_BORDER,VERT_BORDER + y,HORIZ_BORDER + BOARD_SPACE_DIM,VERT_BORDER + y);
			phics.drawLine(F_WIDTH-HORIZ_BORDER-BOARD_SPACE_DIM,VERT_BORDER + y,F_WIDTH-HORIZ_BORDER,VERT_BORDER + y);
		}
	}
	
	private void drawFigures(Graphics phics)
	{
		/*Random rand = new Random();
		int randSpace = Math.abs(rand.nextInt()) % NUM_SPACES;
		System.out.println("Space #: " + randSpace);*/
		
		System.out.println("GameGUI.drawFigures():");
		for(int playerID = 0; playerID < 4; playerID++)
		{
			int[] ulc = spaceToCoordPair(positions[playerID]);
			
			System.out.println("\tPlayerID " + playerID + " @ (" + ulc[0] + ", " + ulc[1] + ")");
			phics.setColor(COLORS[playerID]);
			phics.fillRect(HORIZ_BORDER + ulc[0] + 1, VERT_BORDER + ulc[1] + 1, BOARD_SPACE_DIM - 2, BOARD_SPACE_DIM - 2);
		}
	}
	
	private int[] spaceToCoordPair(int spaceNum)
	{
		int wholeQuotient = spaceNum / (NUM_SPACES/4);
		
		//x and y pixel coordinates of the upper left-hand corner of the space
		int ulc_x = 0, ulc_y = 0;
		switch(wholeQuotient)
		{
			case 0:
				ulc_x = spaceNum * BOARD_SPACE_DIM;
				ulc_y = 0;
				break;
			case 1:
				ulc_x = (NUM_SPACES/4) * BOARD_SPACE_DIM;
				ulc_y = (spaceNum - NUM_SPACES/4) * BOARD_SPACE_DIM;
				break;
			case 2:
				ulc_x = (3*NUM_SPACES/4 - spaceNum) * BOARD_SPACE_DIM;
				ulc_y = (NUM_SPACES/4) * BOARD_SPACE_DIM;
				break;
			case 3:
				ulc_x = 0;
				ulc_y = (NUM_SPACES - spaceNum) * BOARD_SPACE_DIM;
				break;
			default:
				ulc_x = 0;
				ulc_y = 0;
				break;
		}
		
		return new int[]{ulc_x, ulc_y};
	}
	
	/********************************************************************************************/
	//set the 3 functions to satisfy the KeyListener interface required functions
	/********************************************************************************************/
	public void keyPressed(KeyEvent ke){
		//filler to satisfy the KeyListener interface
	}
	
	//closes this Frame when the user presses ESCAPE
	public void keyReleased(KeyEvent ke)
	{
		/*if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(exit(this))
			{
				System.exit(0);
			}
			/*System.out.println("Key Released");
			dispose();
			
			String[] userInfo = stDisplay.intro();
			
			stDisplay.homeNestMenu(userInfo[0], userInfo[1]);
			add(stDisplay);
			pack();
			setVisible(true);*/
		//}
	}
	
	public void keyTyped(KeyEvent ke){
		//filler to satisfy the KeyListener interface
	}
	/********************************************************************************************/
	
	public static boolean exit(JFrame frame)
	{
		return JOptionPane.showConfirmDialog(frame, "Are you sure that you want to quit " + GAME_NAME + "?",
			"EXIT?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
}
