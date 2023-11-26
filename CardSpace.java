
public class CardSpace extends EventSpace
{
	enum Type{
		COMMUNITY_CHEST,
		CHANCE,
		DOC_OR_DANGER;
		
		public String toString()
		{
			switch(this)
			{
				case COMMUNITY_CHEST:
					return "Community Chest";
				case CHANCE:
					return "Chance";
				default:
					return "Doc Or Danger";
			}
		}
	}
	
	private Type myCardSpaceType;
	
	public CardSpace(String code, Type cardSpaceType)
	{
		super(code);
		myCardSpaceType = cardSpaceType;
		
		//DeckManager? How to tie this to the proper deck
	}
	
	public void applyEvent(SquirrelPlayer player, GameGUI display)
	{
		//based on the Type, choose a Deck from which to draw these cards
	}
}
