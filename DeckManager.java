import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class DeckManager
{
	private ArrayDeque<Card>[] deckArray;
	public DeckManager(ArrayList<String> cardInputs)
	{
		deckArray = new ArrayDeque[3];
		for(int i = 0; i < 3; i++)
		{
			deckArray[i] = new ArrayDeque<Card>();
		}
		
		assignToDecks(cardInputs);
	}
	
	//takes the card at the front of the deque; puts it at the back; returns its value
	public Card getTopCard(int deckIndex)
	{
		if(deckIndex < 0 || deckIndex >= deckArray.length)
			return null;
		
		deckArray[deckIndex].add(deckArray[deckIndex].remove());
		
		return deckArray[deckIndex].peekLast();
	}
	
	private void assignToDecks(ArrayList<String> cardInput)
	{
		Random rand = new Random();
		ArrayList<Card>[] tempLists = new ArrayList[3];
		for(int i = 0; i < tempLists.length; i++)
			tempLists[i] = new ArrayList<Card>();
		
		for(String cardStr : cardInput)
		{
			StringTokenizer chopper = new StringTokenizer(cardStr, "/");
			String code = chopper.nextToken();
			String msg = chopper.nextToken();
			int num = Integer.valueOf(chopper.nextToken());
			boolean relative = false;
			if(chopper.hasMoreTokens())
				relative = true;
			
			switch(code)
			{
				case "AT":
					tempLists[0].add(new Card(msg, num, CardSpace.Type.TROVE_OF_ACORNS));
					break;
				case "4LE":
					tempLists[1].add(new Card(msg, num, CardSpace.Type.FLEET_OF_PAW, relative));
					break;
				case "DoD":
					tempLists[2].add(new Card(msg, num, CardSpace.Type.DOC_OR_DANGER));
					break;
				default:
					break;
			}
		}
		
		//shuffle the decks while adding them to an ArrayDeque
		for(int i = 0; i < tempLists.length; i++)
		{
			while(tempLists[i].size() > 0)
			{
				int index = rand.nextInt(tempLists[i].size());
				deckArray[i].add(tempLists[i].remove(index));
			}
		}
		
		System.out.println(deckArray[0].size() + " " + deckArray[1].size() + " " + deckArray[2].size());
	}
}
