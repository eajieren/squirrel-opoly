
public abstract class EventSpace extends GameSpace
{
	public EventSpace(String code)
	{
		super(code);
	}
	
	public abstract void applyEvent(SquirrelPlayer player, GameGUI display);
}
