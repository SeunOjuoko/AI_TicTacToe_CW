package ticTacToe;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 * @author ae187
 *
 */
public class ValueIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP mdp=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=50;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		mdp=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) {
		super(p);
		
	}

	public ValueIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		mdp=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initialises the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);
		
		
		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		mdp=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	/**
	 
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 * 
	 *
	 */
	//Loop k in an iteration 10 times
	//Loop over each game state game g
	//Loop over moves m in game g
	//Loop over all outcomes from (g.m)
	//Update VI (hashmap)
	//
	public void iterate()
	{
		//This begins by iterating through the Hashmap list (k=50) times
		for(int i = 0; i < k; i++) { 	
			//This goes through all the game state as an object game called g
			for(Game g: valueFunction.keySet()) { 
				//To find the Biggest Q value, the value is originally set to  we set is to the lowest possible value for the
				double biggestQ = -10000;
				//This goes through the list of each possible game moves that the agent can make called m
				for(Move m: g.getPossibleMoves() ) {    
					double totalQ = 0;
					//This loops object generates a list that contains two values of the outcomes for the game and move 
					for(TransitionProb s: mdp.generateTransitions(g,m)) { 
						//The variables for Q values that are sPrime, localReward and prob are initialised 
						Game sPrime = s.outcome.sPrime;
						double reward = s.outcome.localReward;
						double probability = s.prob;
						//This equation works out Q value within the Bellmov equation 
						double valueQ = probability*(reward+(valueFunction.get(sPrime))); 
						//Each Q is stored into the list of Q's called total Q
						totalQ += valueQ;
					}
					//Each loop iteration will have a range of total Q values
					//This conditional statements maintains the biggest Q out the range of total Q values 
					if (totalQ > biggestQ) {	
						//If the total is higher than the biggest Q value, then total Q will be set as the new biggest Q
						biggestQ = totalQ;											
					}
					//This updates the value Function to add the game and biggest Q into the map
					valueFunction.put(g, biggestQ);								
				}
			}
		}
	}
	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	public Policy extractPolicy()
	{
		//This creates an empty Hashmap for the policies
		HashMap<Game, Move> p = new HashMap<>();

		//This creates a policy constructor
		Policy policy = new Policy();				

		//This iterates through the game states within the Hashmap called ValueFunctions
		for(Game g: valueFunction.keySet()) {
			//The Biggest Q value is set to the lowest value again 
			double biggestQ = -10000;
			//Best is set 
			Move best = null;
			//This goes through the list of each possible game moves again
			for(Move m: g.getPossibleMoves() ) {     //maximising 
				//The totalQ begins with 0
				double totalQ = 0;
				//The variables for Q values that are sPrime, localReward and prob are initialised 
				for(TransitionProb s: mdp.generateTransitions(g,m)) { 
					Game sPrime = s.outcome.sPrime;
					double reward = s.outcome.localReward;
					double probability = s.prob;
					//This equation works out Q value within the Bellman equation
					double valueQ = probability*(reward+(valueFunction.get(sPrime)));
					totalQ += valueQ;
				}
				//This conditional statements maintains the biggest Q out the range of total Q values 
				if (totalQ > biggestQ) {
					//This allows the total Q to be saved as the biggest Q
					biggestQ = totalQ;
					//This position at m is set to best move  
					best =  m;
					//This puts the game object and the best move object into the policy hashmap
					p.put(g,best);
					//This initialises the new policy
					policy = new Policy(p); 
				}
			}
		}
		return policy;
	}
	
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();

		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			//System.exit(1);
		}
		
		
		
	}

	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play the agent against a human agent.
		ValueIterationAgent agent=new ValueIterationAgent();
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();
		
		
		

		
		
	}
}
