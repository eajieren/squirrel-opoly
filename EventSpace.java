
public abstract class EventSpace extends GameSpace
{
	public EventSpace(String code, int spcNum)
	{
		super(code, spcNum);
	}
	
	public abstract void applyEvent(SquirrelPlayer player, Game currentGame);
}
