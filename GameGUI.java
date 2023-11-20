import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameGUI extends JFrame implements KeyListener
{
	private static final int F_WIDTH = 700, F_HEIGHT = 700;
	private static final String GAME_NAME = "Squirrel-opoly";
	//private ShadowTailDisplay stDisplay;
	
	public GameGUI()
	{
		super(GAME_NAME);
		
		//stDisplay = new ShadowTailDisplay();
		
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
