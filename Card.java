
public class Card
{
	private String myMessage;
	private int myNum;
	private CardSpace.Type myType;
	private boolean isNumRelative;
	
	public Card(String message, int num, CardSpace.Type type)
	{
		myMessage = message;
		myNum = num;
		myType = type;
		isNumRelative = false;
	}
	
	public Card(String message, int num, CardSpace.Type type, boolean relative)
	{
		myMessage = message;
		myNum = num;
		myType = type;
		isNumRelative = relative;
	}
	
	public String getMessage()
	{
		return myMessage;
	}
	
	public int getNum()
	{
		return myNum;
	}
	
	public CardSpace.Type getType()
	{
		return myType;
	}
	
	public boolean isNumRelative()
	{
		return isNumRelative;
	}
}
