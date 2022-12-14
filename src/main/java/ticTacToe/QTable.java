package ticTacToe;

import java.util.HashMap;

public class QTable extends HashMap<Game, HashMap<Move,Double>> {

	
	public QTable()
	{
		super();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public Double getQValue(Game g, Move m)
	{
		if (containsKey(g))
		{
			HashMap<Move,Double> moves=get(g);
			if (moves.containsKey(m))
				return moves.get(m);
		}
		
		return null;
	}
	
	public void addQValue(Game g, Move m, Double v)
	{
		if (!containsKey(g))
			this.put(g, new HashMap<Move,Double>());
		
		this.get(g).put(m, v);
	}
	
	

}
