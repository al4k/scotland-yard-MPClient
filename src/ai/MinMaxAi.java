package ai;

import game.TextOutput;

import java.util.*;

import state.Initialisable;
import util.*;

public class MinMaxAi extends AI {

	/**
	 * Class to hold the alpha and beta values
	 */
	private class PruneValues
	{
		public PruneValues(double alpha, double beta)
		{
			this.alpha = alpha;
			this.beta = beta;
		}
		
		public double alpha;
		public double beta;
	}
	
	
	private int ply;
	private BoardScorerBase scorer;
	private int evals = 0;	
	private int maxEvaluations = -1;
	private boolean useAlphaBeta = true;
	private double alphaBetaTolerance = 0.0;
	private boolean useRandomChoice = false;
	private double randomChoiceTolerance = 0.0;
	
	private boolean useQuiescenceExpansion = true;
	private int quiescenceDepthLimit = 3;
	private double quiesenceThreshold = 5.0;
	private  boolean useQuiescentChoice = true;
	
	
	private int depestDepth = 0;
	private long timeLimit = -1;
	private long startTime = 0;
	private long endtime = 0;

	List<Move> moves = new ArrayList<Move>();
		
	
	

	
	/**
	 * Function to get the best move for the player
	 * @param int playerId The id of the player moving
	 * @return Move the move being proposed
	 */
	public Move getMove(int playerId)
	{
		evals = 0;
		startTime = System.currentTimeMillis();
		endtime = startTime + timeLimit;
		
		if(scorer == null)
		{
			TextOutput.printError("Board Scorer Has Not Been Set\n");
			System.exit(1);
		}
		
				
		// initialise the tree with the current state
		Board currentState = getCurrentState(playerId);
		scorer.score(currentState);
		TreeNode<Board> root = new TreeNode<Board>(currentState);
		Tree<Board> tree = new Tree<Board>(root);
		
		
		// if there are no stored moves (from double or secret tickets) get a new one
		if(moves.size() == 0)
		{
			Board b = max(root, tree, ply, Double.MIN_VALUE, Double.MAX_VALUE);
			moves = b.getMoves();
			//printTreePath(tree); // un-comment to print the tree
		}
				
		// get the next move then remove it from the stack
		Move currentMove = moves.get(0);
		moves.remove(0);
				
		return currentMove;
	}
	
	
	/**
	 * This function is used to check if a board is terminal. That is, the tree will
	 * not be expanded from this node
	 * @param board The board we are testing
	 * @param maximising If we are maximising or minimising
	 * @param depth The current depth of the tree expansion
	 * @return If the node is terminal or not
	 */
	private boolean isTerminal(Board board, boolean maximising, int depth)
	{
		// we have reached the time limit
		if(timeLimit != -1 && System.currentTimeMillis() > endtime)
			return true;
		
		
		if(depth < depestDepth)
		{
			depestDepth = depth;
		}
		
		if(evals >= maxEvaluations && maxEvaluations > -1)
			return true;
		
		
		
		// always stop at a win condition
		if(depth < ply)
			if(board.isDetectiveWin() || board.isMrXWin())
				return true;
		
		
		
		
		// always carry on if depth is less than the standard search params
		if(depth > 0)
			return false;
		
		
		// standard terminal test for depth limit without quiesence
		if(!useQuiescenceExpansion)
		{
			if(depth == 0)
				return true;
			else
				return false;
		}
		else
		{
					
			// stop at quiescent depth limit
			if(depth == -quiescenceDepthLimit)
				return true;
			
			// if the node is quiet return true
			if(isNodeQuiet(board))
				return true;
			
			return false;
		}
	}
	
	
	/**
	 * Function to check if a board is 'quiet' or not. If it is not
	 * it is a candidate for quiescent expansion
	 * @param board The board we are testing
	 * @return If the board is quiet
	 */
	private boolean isNodeQuiet(Board board)
	{
		double meanDiff = board.getMeanScoreDifference();
		if(meanDiff < quiesenceThreshold)
			return true;
		else
			return false;
	}
	
	
	/**
	 * At a certain node of the tree, this function is used to pick the best board 
	 * out of the possible children depending on if we are maximising or minimising
	 * @param boards The list of boards we are checking
	 * @param maximise If we maximise or not
	 * @return The best board
	 */
	private Board chooseBoard(List<Board> boards, boolean maximise)
	{
		if(useRandomChoice)
			return makeRandomChoice(boards, maximise);
		
		if(useQuiescentChoice)
			return makeQuiescentChoice(boards, maximise);
		
		// standard choice
		if(maximise)
			return Collections.max(boards);
		else
			return Collections.min(boards);		
	}
	
	/**
	 * If we are using the quiescence value in our choice method, this function
	 * choose the set of boards that have the best scores, and order them in terms
	 * of their 'quietness'. It will then choose the least 'quiet' board
	 * @param boards The list of boards
	 * @param maximise If we are maximising or minimising
	 * @return The best board
	 */
	private Board makeQuiescentChoice(List<Board> boards, boolean maximise)
	{
		List<Board> choices = new ArrayList<Board>();
		
		if(maximise)
		{
			Board best = Collections.max(boards);
			for(int i = boards.size()-1; i >= 0; i--)
			{
				if(boards.get(i).getScore() == best.getScore())
					choices.add(boards.get(i));
			}
		}
		else
		{
			Board best = Collections.min(boards);
			for(int i = 0; i < boards.size(); i++)
			{
				if(boards.get(i).getScore() == best.getScore())
					choices.add(boards.get(i));
			}
		}
					
		// sort the choices by quiescence
		double maxQ = Double.MIN_VALUE;
		int maxQIndex = 0;
		for(int i = 0; i < choices.size(); i++)
		{
			if(choices.get(i).getMeanScoreDifference() > maxQ)
			{
				maxQ = choices.get(i).getMeanScoreDifference();
				maxQIndex = i;
			}
		}
		
		return choices.get(maxQIndex);
	}
	
	/**
	 * If we are suing randomness in our choice of boards, this function 
	 * will sort the input list by their scores. It will select the set
	 * of boards that are within the set tolerance value of the best board
	 * and make a random choice from them
	 * @param boards The set of boards
	 * @param maximise If we are maximising or not
	 * @return The best board
	 */
	private Board makeRandomChoice(List<Board> boards, boolean maximise)
	{
		List<Board> choices = new ArrayList<Board>();
		
		if(maximise)
		{
			Board best = Collections.max(boards);
			//choices.add(best);
			double threshold = best.getScore() * this.randomChoiceTolerance;
			for(int i = boards.size()-1; i >= 0; i--)
			{
				if(boards.get(i).getScore() >= (best.getScore() - threshold))
					choices.add(boards.get(i));
			}
		}
		else
		{
			Board best = Collections.min(boards);
			//choices.add(best);
			double threshold = best.getScore() * this.randomChoiceTolerance;
			for(int i = 0; i < boards.size(); i++)
			{
				if(boards.get(i).getScore() <= (best.getScore() + threshold))
					choices.add(boards.get(i));
			}
		}
					
		// get a random choice
		int min = 0;
		int max = choices.size() - 1;
		
		int choice = min + (int) (Math.random() * ((max-min) + 1));
		return choices.get(choice);
	}
	
	
	/**
	 * Function to process the maximisation part of the min-max algorithm
	 * @param parent The parent node in the tree
	 * @param tree The tree itself
	 * @param depth The current depth of the tree
	 * @param alpha The current alpha value for the pruning
	 * @param beta The current beta value for the pruning
	 * @return The Best board as a result of the maximisation step
	 */
	public Board max(TreeNode<Board> parent, Tree<Board> tree, int depth, double alpha, double beta)
	{		
		// check for terminal condition
		if(isTerminal(parent.data(), true, depth))
		{
			return parent.data();
		}
		
		PruneValues pruneValues = new PruneValues(alpha, beta);
		
		// get the child boards
		List<Board> inputBoards = parent.data().getMrXMoves(false);
		List<Board> outputBoards = new ArrayList<Board>();
				
		for(int i = 0; i < inputBoards.size(); i++)
		{
			// get the board and set the parents value
			Board board = inputBoards.get(i);
			scorer.score(board);
			
			board.addParentScores(parent.data().getParentScores());
			
			// create the tree node and add it
			TreeNode<Board> node = new TreeNode<Board>(board);
			tree.addNode(parent, node);
				
			
			Board sub = min(node, tree, depth-1, pruneValues.alpha, pruneValues.beta);
			board.setScore(sub.getScore());	
			outputBoards.add(board);
			
			evals++;
			
			if(prune(board.getScore(), pruneValues, true))
				break;
		
		}	
		
		return chooseBoard(outputBoards, true);
	}
	
	
	/**
	 * Function to process the minimisation part of the min-max algorithm
	 * @param parent The parent node in the tree
	 * @param tree The tree itself
	 * @param depth The current depth of the tree
	 * @param alpha The current alpha value for the pruning
	 * @param beta The current beta value for the pruning
	 * @return The Best board as a result of the minimisation step
	 */
	private Board min(TreeNode<Board> parent, Tree<Board> tree, int depth, double alpha, double beta)
	{			
		// check for the terminal conditions (winnning)	
		if(isTerminal(parent.data(), false, depth))
		{
			return parent.data();
		}
		
		// create the object for the alpha and beta values
		PruneValues pruneValues = new PruneValues(alpha, beta);
		
		// get the child boards
		List<Board> inputBoards = parent.data().getDetectiveMoves();
		List<Board> outputBoards = new ArrayList<Board>();
			
		for(int i = 0; i < inputBoards.size(); i++)
		{
			// get the board and set the parents value
			Board board = inputBoards.get(i);
			scorer.score(board);
			board.addParentScores(parent.data().getParentScores());
			
			
			// create the tree node and add it
			TreeNode<Board> node = new TreeNode<Board>(board);
			tree.addNode(parent, node);
			
			Board sub = max(node, tree, depth-1, pruneValues.alpha, pruneValues.beta);
			board.setScore(sub.getScore());	
			outputBoards.add(board);

			evals++;
			
			if(prune(board.getScore(), pruneValues, false))
				break;
				
		}	
		
		return chooseBoard(outputBoards, false);
	}
	
	
	/**
	 * Function to test if we want to prune the tree given the current alpha and beta values	
	 * @param score The score of the board
	 * @param pruneValues The pruning values
	 * @param maximise If we are maximising or minimising
	 * @return If we prune or not
	 */
	private boolean prune(double score, PruneValues pruneValues, boolean maximise)
	{
		if(!useAlphaBeta)
			return false;

		if(maximise)
		{
			pruneValues.alpha = Math.max(pruneValues.alpha, score);
			return pruneCheck(pruneValues.alpha, pruneValues.beta);
		}
		else
		{
			pruneValues.beta = Math.min(pruneValues.beta, score);
			return pruneCheck(pruneValues.alpha, pruneValues.beta);
		}
	}
	
	/**
	 * Function to check if we prune the tree or not. If the alphaBeta tolerance 
	 * is above 0.0 then this will be taken into account in the pruning descision
	 * @param alpha Current alpha value
	 * @param beta Current beta value
	 * @return If we are pruning at a given node
	 */
	private boolean pruneCheck(double alpha, double beta)
	{
		double threshold = alpha * alphaBetaTolerance;
		if(beta > (alpha - threshold))
			return false;
			
		return true;
	}
	
		
	/**
	 * Function to get the current info of the player
	 * @param playerId
	 * @return
	 */
	private Board.PlayerInfo getPlayerInfo(int playerId)
	{
		Board.PlayerInfo info = new Board.PlayerInfo();
		info.playerId = playerId;
		
		info.position = aiReadable.getNodeId(playerId);
		info.tickets.put(Initialisable.TicketType.Bus, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.Bus, playerId));
		info.tickets.put(Initialisable.TicketType.Taxi, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.Taxi, playerId));
		info.tickets.put(Initialisable.TicketType.Underground, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.Underground, playerId));
		info.tickets.put(Initialisable.TicketType.SecretMove, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.SecretMove, playerId));
		info.tickets.put(Initialisable.TicketType.DoubleMove, 
				aiReadable.getNumberOfTickets(Initialisable.TicketType.DoubleMove, playerId));
				
		
		return info;
	}
	
	/**
	 * Set the class that will be scoring the board
	 * @param scorer
	 */
	public void setBoardScorer(BoardScorerBase scorer)
	{
		this.scorer = scorer;
	}
	
	/**
	 * Function to set the depth of the game tree we are going to explore.
	 * This is without any quiescent expansion
	 * @param ply
	 */
	public void setPly(int ply)
	{
		this.ply = ply;
	}
	
	/**
	 * Do we want to use alpha beta pruning on the game tree?
	 * @param val 
	 */
	public void setUseAlphaBetaPruning(boolean val)
	{
		this.useAlphaBeta = val;
	}
	
	/**
	 * Function to get the current state of the game given the player id
	 * @param playerId 
	 * @return The board representing the current state
	 */
	private Board getCurrentState(int playerId)
	{
		// set the players
		HashMap<Integer, Board.PlayerInfo> players = new HashMap<Integer, Board.PlayerInfo>();

		for(int id : aiReadable.getDetectiveIdList())
		{
			players.put(id, getPlayerInfo(id));
		}
				
		for(int id : aiReadable.getMrXIdList())
		{
			players.put(id, getPlayerInfo(id));
		}

		// get the current state and put it as the root
		Board currentState = new Board(aiReadable.getGraph(), true, players, playerId, null);
				
		return currentState;
	}
	
	
	/**
	 * Function to set the tolerance on the alpha beta pruning.
	 * @param val
	 */
	public void setAlphaBetaTolerance(double val)
	{
		this.alphaBetaTolerance = val;
	}
	
	/**
	 * Function to print the chosen path by the ai
	 * @param tree The tree to print
	 */
	private void printTreePath(Tree<Board> tree)
	{
		TreeNode<Board> parent = tree.root();
		
		boolean maxing = true;
		int depth = 0;
		while(!parent.isLeaf())
		{
			System.out.print("Depth: " + depth);
			List<TreeNode<Board>> children = parent.children();
			
			double maxVal = Double.MIN_VALUE;
			double minVal = Double.MAX_VALUE;
			int index = 0;
			
			for(int i = 0; i < children.size(); i++)
			{
				TreeNode<Board> child = children.get(i);
				double v = child.data().getScore();
				System.out.print(" " + v);
				
				if(maxing)
				{
					if(v > maxVal)
					{
						index = i;
						maxVal = v;
					}
				}
				else
				{
					if(v < minVal)
					{
						index = i;
						minVal = v;
					}
				}
			}
			parent = children.get(index);
			
			
			
			if(maxing) System.out.print(" maxing\n");
			else System.out.print(" mining\n");
			
			List<Double> scores = parent.data().getParentScores();
			System.out.print("Parent scores: ");
			for(double d : scores)
			{
				System.out.print(" " + d);
			}
			System.out.print(" Mean: " +parent.data().getMeanScoreDifference() + "\n");
			
			maxing = !maxing;
			depth++;
			
			
		}
	}
	
	/**
	 * Function to decide if we want to use quiescent expansion
	 * @param val If we are using it or not
	 */
	public void setUseQuiescenceExpansion(boolean val)
	{
		this.useQuiescenceExpansion = val;
	}
	
	/**
	 * This function sets the maximum depth at which the quiescence will 
	 * work. For example, if ply = 2 and this value = 2, the total depth
	 * that can be expanded is 2+2=4
	 * @param depth The depth of quiescent expansion
	 */
	public void setQuiescenceDepthLimit(int depth)
	{
		this.quiescenceDepthLimit = depth;
	}
	
	/**
	 * This is the threshold at which a node is considered 'quiet' or not. 
	 * As the tree is expanded, the estimated score from the parent node is 
	 * added to the child. The child then computes the mean difference between 
	 * the set of parent scores. If this differences if above the threshold
	 * the node is quiet
	 * @param threshold The quiescent threshold
	 */
	public void setQuiescenceThreshold(double threshold)
	{
		this.quiesenceThreshold = threshold;
	}
	
	/**
	 * This function allows us to choose the best child board at a given node
	 * by first selecting the best set of nodes, and then ordering them by their 
	 * quietness and choosing the lest quiet of them
	 * @param val
	 */
	public void setUseQuiescentChoice(boolean val)
	{
		this.useQuiescentChoice = val;
	}
	
	/**
	 * Function to set the maximum board evaluations that will be done 
	 * bt the AI. This is useful in limiting the ai to make a move
	 * within a certain time. If you set this to -1 there will be no
	 * limit of evaluations
	 * @param max The max evaluations to be done
	 */
	public void setMaxEvaluations(int max)
	{
		this.maxEvaluations = max;
	}
	
	
	/**
	 * Function to set if we want to use randomness in our choice of
	 * best child node. 
	 * @param val
	 */
	public void setUseRandomChoice(boolean val)
	{
		this.useRandomChoice = val;
	}
	
	/**
	 * This function sets the tolerance for the inclusion of nodes in
	 * the random selection. The best choice of child at a given node is selected.
	 * if another node is within best+(best*tolerance) then it is included as
	 * part of the random selection 
	 * @param tolerance The value of the tolerance
	 */
	public void setRandomChoiceTolerance(double tolerance)
	{
		this.randomChoiceTolerance = tolerance;
	}
	
	/**
	 * Function to set a time limit for the tree building. If this value is set to
	 * -1 there will be no time limit. Note that this time limit will stop tree 
	 * expansion but actual move selection requires more processing. If you use this to
	 * limit your AI to be within a certain time, compensate for this.
	 * @param milliseconds The time limit
	 */
	public void setTimeLimit(long milliseconds)
	{
		timeLimit = milliseconds;
	}
}
