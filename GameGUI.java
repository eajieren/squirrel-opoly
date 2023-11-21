import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameGUI extends JFrame implements KeyListener
{
	private static final int F_WIDTH = 800, F_HEIGHT = 800;
	private static final int HORIZ_BORDER = 50, VERT_BORDER = 50;
	private static final String GAME_NAME = "Squirrel-opoly";
	
	private final int boardSpaceDim;
	
	public GameGUI(int numSpaces)
	{
		super(GAME_NAME);
		
		boardSpaceDim = (F_WIDTH - (2 * HORIZ_BORDER)) / (numSpaces / 4);
		//set customizable settings of Frame
		setPreferredSize(new Dimension(F_WIDTH, F_HEIGHT));
		//setResizable(false);
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
	
	public void paint(Graphics phics)
	{
		//colors white square with black border
		phics.setColor(Color.white);
		phics.fillRect(HORIZ_BORDER, VERT_BORDER, F_WIDTH - 2*HORIZ_BORDER, F_HEIGHT - 2*VERT_BORDER);
		phics.setColor(Color.black);
		phics.drawRect(HORIZ_BORDER, VERT_BORDER, F_WIDTH - 2*HORIZ_BORDER, F_HEIGHT - 2*VERT_BORDER);
		
		//colors smaller nested green square with black border
		phics.setColor(Color.green);
		phics.fillRect(HORIZ_BORDER + boardSpaceDim, VERT_BORDER + boardSpaceDim, F_WIDTH - 2*(HORIZ_BORDER + boardSpaceDim), F_HEIGHT - 2*(VERT_BORDER + boardSpaceDim));
		phics.setColor(Color.black);
		phics.drawRect(HORIZ_BORDER + boardSpaceDim, VERT_BORDER + boardSpaceDim, F_WIDTH - 2*(HORIZ_BORDER + boardSpaceDim), F_HEIGHT - 2*(VERT_BORDER + boardSpaceDim));
		
		//draw lines separating the board spaces
		for(int x = boardSpaceDim; x < F_WIDTH - 2*HORIZ_BORDER; x += boardSpaceDim)
		{
			phics.drawLine(HORIZ_BORDER + x,VERT_BORDER,HORIZ_BORDER + x,VERT_BORDER + boardSpaceDim);
			phics.drawLine(HORIZ_BORDER + x,F_HEIGHT-VERT_BORDER-boardSpaceDim,HORIZ_BORDER + x,F_HEIGHT-VERT_BORDER);
			
			int y = x;
			phics.drawLine(HORIZ_BORDER,VERT_BORDER + y,HORIZ_BORDER + boardSpaceDim,VERT_BORDER + y);
			phics.drawLine(F_WIDTH-HORIZ_BORDER-boardSpaceDim,VERT_BORDER + y,F_WIDTH-HORIZ_BORDER,VERT_BORDER + y);
		}
	}
	
	/********************************************************************************************/
	//set the 3 functions to satisfy the KeyListener interface required functions
	/********************************************************************************************/
	public void keyPressed(KeyEvent ke){
		//filler to satisfy the KeyListener interface
	}
	
	//closes this Frame when the user presses ENTER
	public void keyReleased(KeyEvent ke)
	{
		if(ke.getKeyCode() == KeyEvent.VK_ENTER)
		{
			/*System.out.println("Key Released");
			dispose();
			
			String[] userInfo = stDisplay.intro();
			
			stDisplay.homeNestMenu(userInfo[0], userInfo[1]);
			add(stDisplay);
			pack();
			setVisible(true);*/
		}
	}
	
	public void keyTyped(KeyEvent ke){
		//filler to satisfy the KeyListener interface
	}
	/********************************************************************************************/
	
	private static boolean exit(JFrame frame)
	{
		return JOptionPane.showConfirmDialog(frame, "Are you sure that you want to quit " + GAME_NAME + "?",
			"EXIT?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
}
