
import java.util.ArrayList;

public class Game
{
	private GameBoard myBoard;
	ArrayList<SquirrelPlayer> players;
	
	public Game(GameBoard board, ArrayList<SquirrelPlayer> gamePlayers)
	{
		myBoard = board;
		players = gamePlayers;
	}
}
