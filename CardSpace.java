import javax.swing.JOptionPane;

public class CardSpace extends EventSpace
{
	enum Type{
		TROVE_OF_ACORNS,
		FLEET_OF_PAW,
		DOC_OR_DANGER;
		
		public String toString()
		{
			switch(this)
			{
				case TROVE_OF_ACORNS:
					return "Trove of Acorns";
				case FLEET_OF_PAW:
					return "Fleet of Paw";
				default:
					return "Doc or Danger";
			}
		}
	}
	
	private Type myCardSpaceType;
	private DeckManager myDecks;
	
	public CardSpace(String code, int spcNum, Type cardSpaceType, DeckManager decks)
	{
		super(code, spcNum);
		myCardSpaceType = cardSpaceType;
		
		myDecks = decks;
	}
	
	/*public DeckManager getDeckManager()
	{
		return myDecks;
	}*/
	
	public void applyEvent(SquirrelPlayer player, Game currentGame)
	{
		GameGUI display = currentGame.getDisplay();
		
		//based on the Type, choose a Deck from which to draw these cards
		Card cardDrawn = drawCard();
		
		displayCard(cardDrawn, currentGame, player);
		
		//get the type of this space
		switch(myCardSpaceType)
		{
			case TROVE_OF_ACORNS:
				player.addFoodUnits(cardDrawn.getNum());
				break;
			case FLEET_OF_PAW:
				if(cardDrawn.getMessage().equals("Apprehended by Animal Control"))
				{
					System.out.println("Apprehended");
					currentGame.apprehend(player);
					break;
				}
				
				int destination;
				if(cardDrawn.getMessage().equals("Go to nearby unowned or unoccupied residence space"))
				{
					destination = currentGame.getNearbyOpenUnownedSpaceNum(player.getGamePosition());
					
					//if there's no open and un-owned space, send the player to the nearest unoccupied space
					if(destination == -19)
						destination = currentGame.getNearbyOpenResSpaceNum(player.getGamePosition());
				}
				else
				{
					//check if this card isNumRelative; if it is, calculate the moveTo space based on the player's current position
					destination = cardDrawn.getNum();
					if(cardDrawn.isNumRelative())
						destination += player.getGamePosition();
					
					//once the moveTo space is calculated, figure out if it's open; if it's not, find 
					if(!(display.isEmptyAt(destination)))
					{
						//try to find the nearest empty space BEHIND the desired destination spot
						//as long as you don't cross PROCEED while going backward
						int alternate = destination - 1;
						while(alternate >= 0)
						{
							if(display.isEmptyAt(alternate))
								break;
							else
								alternate--;
						}
						
						//if you don't find an empty space before crossing proceed, increment forward
						//from destination to find an empty space
						if(alternate < 0)
						{
							alternate = destination + 1;
							while(!(display.isEmptyAt(alternate % display.getNumSpaces())))
							{
								alternate++;
							}
						}
						
						JOptionPane.showMessageDialog(display, "Space " + destination + " was occupied, so " +
								player.getName() + " was sent to space " + alternate + " instead.");
						
						destination = alternate;
					}
				}
				
					
				//move the player to this position
				player.moveTo(destination, display);
				
				if(currentGame.getBoard().getGameSpaceAt(destination) instanceof ResidenceSpace)
					currentGame.takeResidenceActions(player, currentGame.getBoard().getGameSpaceAt(destination));
				break;
			default:
				player.setCurrentHealth(player.getCurrentHealth() + cardDrawn.getNum(), currentGame);
				break;
		}
		
		//apply the num to player
		
		//display the happenings via JOptionPane
		
	}
	
	private Card drawCard()
	{
		int deckIndex = getDeckIndex();
		
		return myDecks.getTopCard(deckIndex);
	}
	
	private void displayCard(Card currentCard, Game currentGame, SquirrelPlayer player)
	{
		String impact = "";
		
		if(currentCard.getType() == CardSpace.Type.TROVE_OF_ACORNS ||
				currentCard.getType() == CardSpace.Type.DOC_OR_DANGER)
		{
			if(currentCard.getNum() > 0)
				impact += "+";
			impact += currentCard.getNum();
			impact += (currentCard.getType() == CardSpace.Type.TROVE_OF_ACORNS) ? " food" : " health points";
		}
		
		String message = currentCard.getMessage();
		if(message.indexOf("Park") != -1)
			message = message.replace("Park", currentGame.getBoard().getLocationName(currentCard.getNum()));
		if(message.indexOf("Human Home") != -1)
			message = message.replace("Human Home", currentGame.getBoard().getLocationName(currentCard.getNum()));
		
		JOptionPane.showMessageDialog(currentGame.getDisplay(), player.getName() + " landed on a " +
				getCode() + " space and drew a card:\n\n" + message
				+ "\n" + impact);
	}
	
	private int getDeckIndex()
	{
		switch(myCardSpaceType)
		{
			case TROVE_OF_ACORNS:
				return 0;
			case FLEET_OF_PAW:
				return 1;
			default:
				return 2;
		}
	}
}
