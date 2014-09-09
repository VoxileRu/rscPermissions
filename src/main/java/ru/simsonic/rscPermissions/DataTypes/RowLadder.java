package ru.simsonic.rscPermissions.DataTypes;

public class RowLadder extends AbstractRow implements Comparable<RowLadder>
{
	public String climber;
	public EntityType climberType;
	public String ladder;
	public String instance;
	public int rank;
	@Override
	public int compareTo(RowLadder t)
	{
		return rank - t.rank;
	}
	public RowLadder nextNode;
	public RowLadder prevNode;
	public int getLadderTopRank()
	{
		int result = rank;
		for(RowLadder row = nextNode; row != null; row = nextNode)
			result = row.rank;
		return result;
	}
	public int getLadderBottomRank()
	{
		int result = rank;
		for(RowLadder row = prevNode; row != null; row = prevNode)
			result = row.rank;
		return result;
	}
	public RowLadder getActualNode(int userRank)
	{
		RowLadder result = this;
		for(; result.nextNode != null; result = result.nextNode)
			if(result.nextNode.rank > userRank)
				break;
		return result;
	}
	@Override
	public Table getTable()
	{
		return Table.ladders;
	}
}