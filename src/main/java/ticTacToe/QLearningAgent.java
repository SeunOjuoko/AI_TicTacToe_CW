package ticTacToe;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A Q-Learning agent with a Q-Table, i.e. a table of Q-Values. This table is implemented in the {@link QTable} class.
 *
 *  The methods to implement are:
 * (1) {@link QLearningAgent#train}
 * (2) {@link QLearningAgent#extractPolicy}
 *
 * Your agent acts in a {@link TTTEnvironment} which provides the method {@link TTTEnvironment#executeMove} which returns an {@link Outcome} object, in other words
 * an [s,a,r,s']: source state, action taken, reward received, and the target state after the opponent has played their move. You may want/need to edit
 * {@link TTTEnvironment} - but you probably won't need to.
 * @author ae187
 */

public class QLearningAgent extends Agent {

	/**
	 * The learning rate, between 0 and 1.
	 */
	double alpha=0.1;

	/**
	 * The number of episodes to train for
	 */
	int numEpisodes=70000;

	/**
	 * The discount factor (gamma)
	 */
	double discount=0.9;


	/**
	 * The epsilon in the epsilon greedy policy used during training.
	 */
	double epsilon=0.1;

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
	 * as your value iteration and policy iteration agents.
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
			{
				this.qTable.addQValue(g, m, 0.0);
				//System.out.println("initing q value. Game:"+g);
				//System.out.println("Move:"+m);
			}

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
	 *  Implement this method. It should play {@code this.numEpisodes} episodes of Tic-Tac-Toe with the TTTEnvironment, updating q-values according
	 *  to the Q-Learning algorithm as required. The agent should play according to an epsilon-greedy policy where with the probability {@code epsilon} the
	 *  agent explores, and with probability {@code 1-epsilon}, it exploits.
	 *  
	 *  At the end of this method you should always call the {@code extractPolicy()} method to extract the policy from the learned q-values. This is currently
	 *  done for you on the last line of the method.
	 *  
	 *  new Q value = sumofprobailtys*(reward + discount*old Qvalue))
	 *  sample = reward +discont*maxQ(sprime
	 *  new Q value = (1-discount)*oldQvaule + discount*sample
	 *  outcome = env.executeMove(m);
	 */


	public void train()
	{
		//This class will now go through the new TTTEnvironment to retrieve the current game states
		Game g = env.getCurrentGameState();
		//This allows for the random function to be called later on within the method 
		Random rand = new Random();
		Move best = null;
		Outcome o = null;
		double prevQ = 0;
		double nextQ;
		double biggestQ = -10000;

		//This conditional statements that iterates through the 70000 episodes through the train 
		for (int i = 0; i < numEpisodes; i++) {
			//As the game is active
			while(g.isTerminal()==false) { 
				//This generates a random number between 0 and 1
				double n = rand.nextDouble();
				//This conditional statement just checks the first part of the code to allow the code to explore before exploiting
				if(best == null) { 
					n = epsilon - 0.01;
				}
				////Exploit: finds the old moves and gets the best move from the older previous moves 
				if(n > epsilon) {
					//finding max from equation, all the possible moves
					for(Move m: env.getPossibleMoves()){ 
						//This gets the previous Q value at game and move instance in that Q table
						prevQ = qTable.get(g).get(m); 
						//This conditional statement stores the biggest Q value 
						if (prevQ > biggestQ) { 
							//Whenever the previous Q value is more than the biggest Q, then that Q value becomes the biggest Q 
							biggestQ = prevQ;
							//The sets the move(m) to best making it the temporary best move
							best = m;
						}
						
						//This adds the game, best and the biggest Q into the Q table 
						qTable.addQValue(g, best, biggestQ);         
					}
				}
				else {
					//This random generator creates random
					Random generator = new Random();
					Move randomMove = g.getPossibleMoves().get(generator.nextInt(g.getPossibleMoves().size()));
					//A try statement is used for trying to find the best move in the Q table
					try {
						o = env.executeMove(randomMove);     
						Game sPrime = o.sPrime;
						double reward = o.localReward;
						Move bestPrime = null;
						double bigQ =-10000;
						//This sequential loop goes through all the possible sPrime moves from env
						for(Move sPrimeM: env.getPossibleMoves()){ 
							//This equation retrieves the sample value at that instance 
							prevQ = qTable.get(sPrime).get(sPrimeM); 
							//This conditional state allows for the big Q to be stored as the Big Q
							if (prevQ > bigQ) {  
								//Any prevQ that is greater than BigQ, will become the new Big Q
								bigQ = prevQ;
								bestPrime = sPrimeM;
							}
						}
						//This equation is required to calculate the sample value
						double sample = reward + discount * bigQ;
						//The sample value allows for the next values of Q to be calculated
						nextQ = (1-discount)*prevQ + discount*sample; 
						//The value of game, best move and the next value of Q
						qTable.addQValue(g, randomMove, nextQ);          
					}
					catch(IllegalMoveException e) {
						e.printStackTrace();
					}
				}

			}
		}
		
		this.policy=extractPolicy();
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the train() & extractPolicy methods");
			//System.exit(1);
		}
	}

	/** Implement this method. It should use the q-values in the {@code qTable} to extract a policy and return it.
	 *
	 * @return the policy currently inherent in the QTable
	 */
	public Policy extractPolicy()
	{
		//This initialises the policy for turning the Hashmap into the policy
		Policy policy = new Policy();
		//This creates a new Hashmap that includes the games and moves 
		HashMap<Game,Move> p = new HashMap<Game,Move>();
		//To count the amount of valueQs the counter begins at 0
		double valueQ = 0;
		//To count the amount of totalQs the counter begins at 0
		double totalQ = 0;
		//This sequential loop goes all the keys (Hashmap) within the Qtable 
		for(Game g: qTable.keySet()) { 
			//The function will be skipped of the game has stopped 
			if(g.isTerminal()) {    
				continue;
			}
			Move best = null;
			double biggestQ = -10000;
			//This goes through all the possible moves within the Hashmap 
			for(Move m: g.getPossibleMoves()){
				//This gets the previous Q value at game and move instance in that Q table
				valueQ = qTable.get(g).get(m); 
				//This conditional state allows for the largest Q to be stored as the biggest Q
				if (valueQ > biggestQ) {  
					//When a value Q is better than biggestQ, then that value Q will be set to biggest Q
					biggestQ = valueQ;
					//While this happens the move from get possible moves becomes the best move too
					best = m;
					//This updates the Hashmap by putting in the best 
					p.put(g,best); 
					//This is now turned into a policy
					policy = new Policy(p);
				}
			}
		}
		//Returns the constructed policy
		return policy;
	}

	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play your agent against a human agent (yourself).
		QLearningAgent agent=new QLearningAgent();

		HumanAgent d=new HumanAgent();

		Game g=new Game(agent, d, d);
		g.playOut();

	}

}