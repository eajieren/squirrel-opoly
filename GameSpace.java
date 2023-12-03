
public abstract class GameSpace
{
	private String spaceCode;
	private int mySpaceNum;
	
	public GameSpace(String code, int spcNum)
	{
		spaceCode = code;
		mySpaceNum = spcNum;
	}
	
	public String getCode()
	{
		return spaceCode;
	}
	
	public int getSpaceNum()
	{
		return mySpaceNum;
	}
}
