
public class Card
{
	private String myMessage;
	private int myNum;
	private CardSpace.Type myType;
	
	public Card(String message, int num, CardSpace.Type type)
	{
		myMessage = message;
		myNum = num;
		myType = type;
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
}
