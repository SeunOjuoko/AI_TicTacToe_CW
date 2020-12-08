package ticTacToe;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * A policy iteration agent. You should implement the following methods:
 * (1) {@link PolicyIterationAgent#evaluatePolicy}: this is the policy evaluation step from your lectures
 * (2) {@link PolicyIterationAgent#improvePolicy}: this is the policy improvement step from your lectures
 * (3) {@link PolicyIterationAgent#train}: this is a method that should runs/alternate (1) and (2) until convergence. 
 * 
 * NOTE: there are two types of convergence involved in Policy Iteration: Convergence of the Values of the current policy, 
 * and Convergence of the current policy to the optimal policy.
 * The former happens when the values of the current policy no longer improve by much (i.e. the maximum improvement is less than 
 * some small delta). The latter happens when the policy improvement step no longer updates the policy, i.e. the current policy 
 * is already optimal. The algorithm should stop when this happens.
 * 
 * @author ae187
 *
 */
public class PolicyIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states according to the current policy (policy evaluation). 
	 */
	HashMap<Game, Double> policyValues=new HashMap<Game, Double>();

	/**
	 * This stores the current policy as a map from {@link Game}s to {@link Move}. 
	 */
	HashMap<Game, Move> curPolicy=new HashMap<Game, Move>();

	double discount=0.9;

	/**
	 * The mdp model used, see {@link TTTMDP}
	 */
	TTTMDP mdp;

	/**
	 * loads the policy from file if one exists. Policies should be stored in .pol files directly under the project folder.
	 */
	public PolicyIterationAgent() {
		super();
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();


	}


	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public PolicyIterationAgent(Policy p) {
		super(p);

	}

	/**
	 * Use this constructor to initialise a learning agent with default MDP paramters (rewards, transitions, etc) as specified in 
	 * {@link TTTMDP}
	 * @param discountFactor
	 */
	public PolicyIterationAgent(double discountFactor) {

		this.discount=discountFactor;
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Use this constructor to set the various parameters of the Tic-Tac-Toe MDP
	 * @param discountFactor
	 * @param winningReward
	 * @param losingReward
	 * @param livingReward
	 * @param drawReward
	 */
	public PolicyIterationAgent(double discountFactor, double winningReward, double losingReward, double livingReward, double drawReward)
	{
		//discountFactor = 10.0;
		//winningReward = -50.0;
		//losingReward 
		//livingReward =-1.00;
		//drawReward = 0.0;
		this.discount=discountFactor;
		this.mdp=new TTTMDP(winningReward, losingReward, livingReward, drawReward);
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Initialises the {@link #policyValues} map, and sets the initial value of all states to 0 
	 * (V0 under some policy pi ({@link #curPolicy} from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.policyValues.put(g, 0.0);

	}

	/**
	 *  You should implement this method to initially generate a random policy, i.e. fill the {@link #curPolicy} for every state. Take care that the moves you choose
	 *  for each state ARE VALID. You can use the {@link Game#getPossibleMoves()} method to get a list of valid moves and choose 
	 *  randomly between them. 
	 */
	public void initRandomPolicy()
	{
		//This iterates through the game states within the hashmap called PolicyValues
		for(Game g: policyValues.keySet()) { 
			//This goes through all the possible that the hashmap holds
			for(Move m: g.getPossibleMoves() ) {		//This
				//This generates a random value 
				Random rand = new Random();
				Move randomMove = g.getPossibleMoves().get(rand.nextInt(g.getPossibleMoves().size()));
				//The Hashmap p is created for 
				HashMap<Game, Move> p = new HashMap<>();
				//The random move that is played is updated into the new current Polic hashmap
				curPolicy.put(g, randomMove);
			}

		}
	}


	/**
	 * Performs policy evaluation steps until the maximum change in values is less than {@code delta}, in other words
	 * until the values under the current policy converge. After running this method, 
	 * the {@link PolicyIterationAgent#policyValues} map should contain the values of each reachable state under the current policy. 
	 * You should use the {@link TTTMDP} {@link PolicyIterationAgent#mdp} provided to do this.
	 *
	 * @param delta
	 */
	protected void evaluatePolicy(double delta)
	{
		//This goes through all the policy values as an object game called g 
		for(Game g: policyValues.keySet()) {	
			//The biggestQ value is set to the lowest value
			double biggestQ = -10000;		 
			//This stores the stores old previous Q value to the lowest value
			double prevQ = -10000;						 
			double change = -10000;
			double diff = 0;
			//This begins with a while statement begins that compares the change value with delta
			while(change > delta) {
				///This retrieves the game value from the specific Move m value f
				Move m = curPolicy.get(g);
					//This counts the total Qs starting at 0
					double totalQ = 0;
					for(TransitionProb s: mdp.generateTransitions(g,m)) { 
						Game sPrime = s.outcome.sPrime;
						double reward = s.outcome.localReward;
						double probability = s.prob;
						double valueQ = probability*(reward+(policyValues.get(sPrime)));
						totalQ += valueQ;
					}
					//This conditional statement finds the biggest Q values
					if (totalQ > biggestQ) {
						//Any totalQ value that greater than the biggest Q is 
						biggestQ = totalQ;
					}
					//The difference is found between the policy 
					diff = Math.abs(policyValues.get(g)-biggestQ);
					policyValues.put(g,biggestQ);
		
					}
					//The greatest difference in policy values will be stored as change 
					if (diff > change) {
						//Once the difference is greater than change, then this value will be stored as change
						change = diff;

				}
				//Update the difference in value 
				//Measure the maximum change in value
				//Value of S and update the value and store the biggest difference
			}

		}	

	



	/**This method should be run AFTER the {@link PolicyIterationAgent#evaluatePolicy} train method to improve the current policy according to 
	 * {@link PolicyIterationAgent#policyValues}. You will need to do a single step of expectimax from each game (state) key in {@link PolicyIterationAgent#curPolicy} 
	 * to look for a move/action that potentially improves the current policy. 
	 * 
	 * @return true if the policy improved. Returns false if there was no improvement, i.e. the policy already returned the optimal actions.
	 */
	protected boolean improvePolicy()
	{
		//The boolean begins with initial variable being set to false 
		boolean i = false;
		//This iterates through all the policy values hashmap
		for(Game g: policyValues.keySet()) {
			//This goes through the possible moves for each game within the Hashmap 
			for(Move m: g.getPossibleMoves() ) { 
				//The total values of Q begin at 0
				double totalQ = 0;
				//The variables for Q values that are sPrime, localReward and probability are initialised 
				for(TransitionProb s: mdp.generateTransitions(g,m)) { 
					Game sPrime = s.outcome.sPrime;
					double reward = s.outcome.localReward;
					double prob = s.prob;
					double valueQ = prob*(reward+(policyValues.get(sPrime)));
					totalQ += valueQ;
				}
				//This conditional loop updates the current Hashmap 
				if (totalQ > policyValues.get(g)) {
					//Updates the policy values by putting the game and totalQ into the Hashmap
					policyValues.put(g, totalQ); 
					//Updates the current policy by putting the game
					curPolicy.put(g,m);
					//After the policies are updated the boolean is set to true
					i = true;
				}
			}
		}
		//The boolean i returned to illustrate whether the Hashmaps were updated 
		return i;
	}

	/**
	 * The (convergence) delta
	 */
	double delta=0.00001;

	/**
	 * This method should perform policy evaluation and policy improvement steps until convergence (i.e. until the policy
	 * no longer changes), and so uses your 
	 * {@link PolicyIterationAgent#evaluatePolicy} and {@link PolicyIterationAgent#improvePolicy} methods.
	 */
	public void train()
	{
		//This calls the method for evaluate policy
		evaluatePolicy(delta);
		//Calls a while statement for improve policy 
		while(improvePolicy()); {}
		System.out.println(curPolicy);
		super.policy = new Policy(curPolicy);

	}

	public static void main(String[] args) throws IllegalMoveException
	{
		/**
		 * Test code to run the Policy Iteration Agent agains a Human Agent.
		 */
		PolicyIterationAgent pi=new PolicyIterationAgent();

		HumanAgent h=new HumanAgent();

		Game g=new Game(pi, h, h);

		g.playOut();

	}

}
