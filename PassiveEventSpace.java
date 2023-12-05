import javax.swing.JOptionPane;

public class PassiveEventSpace extends EventSpace
{
	private String myQuip;
	
	public PassiveEventSpace(String code, String quip, int spcNum)
	{
		super(code, spcNum);
		myQuip = quip;
	}
	
	public void applyEvent(SquirrelPlayer player, Game currentGame)
	{
		JOptionPane.showMessageDialog(currentGame.getDisplay(),
				player.getName() + " has landed on the " + getCode() + " space. " + myQuip);
	}
}
