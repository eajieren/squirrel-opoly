
public class ResidenceSpace extends GameSpace
{
	private SquirrelPlayer owner;
	private int foodCost, numForagingVisits, hiddenFood, foragingPotential;
	private double humanSensitivityToHabitation, foragingSuccessProb, predatorRisk;
	//consider ints for riskOfComplaint, foodPotential, predatorRisk
	
	public ResidenceSpace(String code, int spcNum, int cost)
	{
		super(code, spcNum);
		owner = null;
		foodCost = cost;
		numForagingVisits = hiddenFood = 0;
		foragingPotential = cost;
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
	
	public void incrementForagingVisits()
	{
		numForagingVisits++;
	}
	
	public int getNumForagingVisits()
	{
		return numForagingVisits;
	}
	
	public int getNumHiddenFoodUnits()
	{
		return hiddenFood;
	}
	
	//removes food from player; adds it to the hiddenFood counter for this space
	public void hideFood(SquirrelPlayer player, int contribution)
	{
		if(contribution <= player.getCurrentFood())
		{
			player.addFoodUnits(-1 * contribution);
			hiddenFood += contribution;
		}
	}
}
