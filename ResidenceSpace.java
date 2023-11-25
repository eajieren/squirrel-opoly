
public class ResidenceSpace extends GameSpace
{
	private SquirrelPlayer owner;
	private int foodCost;
	//consider ints for riskOfComplaint, foodPotential, predatorRisk
	
	public ResidenceSpace(String code, int cost)
	{
		super(code);
		owner = null;
		foodCost = cost;
	}
	
	public SquirrelPlayer getOwner()
	{
		return owner;
	}
	
	public void setOwner(SquirrelPlayer player)
	{
		owner = player;
	}
	
	public int getCost()
	{
		return foodCost;
	}
}
