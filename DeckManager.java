import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class DeckManager
{
	private ArrayDeque<Card> troveOfAcorns, fleetOfPaw, docOrDanger;
	private ArrayDeque<Card>[] deckArray;
	public DeckManager(ArrayList<String> cardInputs)
	{
		troveOfAcorns = new ArrayDeque<Card>();
		fleetOfPaw = new ArrayDeque<Card>();
		docOrDanger = new ArrayDeque<Card>();
		deckArray = new ArrayDeque[3];
		for(int i = 0; i < 3; i++)
		{
			deckArray[i] = new ArrayDeque<Card>();
		}
		
		assignToDecks(cardInputs);
	}
	
	private void assignToDecks(ArrayList<String> cardInput)
	{
		for(String cardStr : cardInput)
		{
			StringTokenizer chopper = new StringTokenizer(cardStr, "/");
			String code = chopper.nextToken();
			String msg = chopper.nextToken();
			int num = Integer.valueOf(chopper.nextToken());
			
			switch(code)
			{
				case "AT":
					deckArray[0].add(new Card(msg, num, CardSpace.Type.COMMUNITY_CHEST));
					break;
				case "4LE":
					deckArray[1].add(new Card(msg, num, CardSpace.Type.CHANCE));
					break;
				case "DoD":
					deckArray[2].add(new Card(msg, num, CardSpace.Type.DOC_OR_DANGER));
					break;
				default:
					break;
			}
		}
		
		System.out.println(deckArray[0].size() + " " + deckArray[1].size() + " " + deckArray[2].size());
	}
}
