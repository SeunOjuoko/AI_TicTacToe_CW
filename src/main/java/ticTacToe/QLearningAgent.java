package ticTacToe;

import java.util.HashMap;
import java.util.List;

/**
 * A Q-Learning agent with a Q table, i.e. a table of Q-Values. This tables is implemented as a {@link java.util.HashMap} from {@link Game}
 * objects to another HashMap from Moves to Q-Values. It could be implemented in many other ways too, but I thought this seemed the simplest.
 *  
 *  
 * @author ae187
 *
 */

public class QLearningAgent extends Agent {
	
	/**
	 * The learning rate, between 0 and 1.
	 */
	double alpha=0.1;
	
	/**
	 * The number of episodes to train for
	 */
	int numEpisodes=100;
	
	double discount=0.9;
	
	
	/**
	 * This is the Q-Table. To get an value for an (s,a) pair, i.e. a (game, move) pair, you can do
	 * qTable.get(game).get(move) which return the Q(game,move) value stored. Be careful with 
	 * cases where there is currently no value. You can use the containsKey method to check if the mapping is there.
	 * 
	 */
	
	QTable qTable=new QTable();
	
	
	/**
	 * This is the Reinforcement Learning environment that this agent will interact with when it is training.
	 * By default, the opponent is the random agent which should make your q learning agent learn the same policy 
	 * as your value iteration & policy iteration agents.
	 */
	TTTEnvironment env=new TTTEnvironment();
	
	
	/**
	 * Construct a Q-Learning agent that learns from interactions with {@code opponent}.
	 * @param opponent the opponent agent that this Q-Learning agent will interact with to learn.
	 * @param learningRate This is the rate at which the agent learns. Alpha from your lectures.
	 * @param numEpisodes The number of episodes (games) to train for
	 */
	public QLearningAgent(Agent opponent, double learningRate, int numEpisodes, double discount)
	{
		env=new TTTEnvironment(opponent);
		this.alpha=learningRate;
		this.numEpisodes=numEpisodes;
		this.discount=discount;
		initQTable();
		train();
	}
	
	/**
	 * Initialises all valid q-values -- Q(g,m) -- to 0.
	 *  
	 */
	
	protected void initQTable()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
		{
			List<Move> moves=g.getPossibleMoves();
			for(Move m: moves)
				this.qTable.addQValue(g, m, 0.0);
			
		}
		
	}
	
	/**
	 * Uses default parameters for the opponent (a RandomAgent) and the learning rate (0.2). Use other constructor to set these manually.
	 */
	public QLearningAgent()
	{
		this(new RandomAgent(), 0.1, 100, 0.9);
		
	}
	
	
	/**
	 *  Implement this method. It should play N = this.numEpisodes episodes of Tic Tac Toe with the TTTEnvironment, updating Q-Values according 
	 *  to the Q-Learning algorithm as and where required. The agent plays according to an epsilon-greedy policy where with the probability epsilon the
	 *  agent explores, and with probability 1-epsilon, it exploits.
	 */
	
	public void train()
	{
		
		
		
	}
	
	/**
	 *
	 * @return the policy currently inherent in the QTable
	 */
	public Policy extractPolicy()
	{
		return null;
		
	}
	
	
	
	


	
}
