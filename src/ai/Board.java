package ai;

import java.util.*;

import org.omg.CORBA.INITIALIZE;

import state.Initialisable;
import graph.Edge;
import graph.Edge.EdgeType;
import graph.Graph;
import ai.AI.Move;

public class Board implements Comparable<Board> {

	public static class PlayerInfo
	{
		public int playerId;
		public int position;
	
		
		
		Move move;
		
		public HashMap<Initialisable.TicketType, Integer> tickets = 
				new HashMap<Initialisable.TicketType, Integer>();
		
		public PlayerInfo()
		{
		}
	}
	
	
	
	private Graph graph;
	private HashMap<Integer, PlayerInfo> players;
	private double score = 0.0;
	private double meanDifference = 0.0;
	private List<Integer> detectiveIds = new ArrayList<Integer>();
	private boolean isMrXMove;
	private int mrXId;
	private List<Double> parentScores =  new ArrayList<Double>();
	private List<Move> moves = new ArrayList<Move>();
	
	
	/**
	 * Board constructor
	 * @param graph The game graph
	 */
	public Board(Graph graph, boolean isMrXMove, HashMap<Integer, PlayerInfo> players, int mrXId, Move move)
	{
		this.players = players;
		this.graph = graph;
		this.isMrXMove = isMrXMove;
		this.mrXId = mrXId;
		this.moves.add(move);
		
		for(int id : players.keySet())
		{
			if(id != this.mrXId)
			{
				detectiveIds.add(id);
			}
		}
	}
	
	public void addMove(Move move)
	{
		this.moves.add(move);
	}
	
	public void addMoveFirst(Move move)
	{
		this.moves.add(0, move);
	}
	
	public List<Move> getMoves()
	{
		return this.moves;
	}
	
	/**
	 * Sets weather the move to make is mr xs
	 * @param val
	 */
	public void setMrXMove(boolean val)
	{
		this.isMrXMove = val;
	}
	
	
	public HashMap<Integer, PlayerInfo> playerInfo()
	{
		return players;
	}
	
	
	public Graph graph()
	{
		return graph;
	}
	
	public int mrXId()
	{
		return this.mrXId;
	}
	
	/**
	 * Sets the score of the board
	 * @param score
	 */
	public void setScore(double score)
	{
		this.score = score;
		if(parentScores.size() == 0)
			parentScores.add(score);
	}
	
	public double getMeanScoreDifference()
	{
		if(parentScores.size() == 1)
			return parentScores.get(0);
		
		double diffSum = 0.0;
		for(int i = 1; i < parentScores.size(); i++)
		{
			diffSum += Math.abs(parentScores.get(i) - parentScores.get(i-1));
		}
		
		if(parentScores.size() == 0)
		{
			Thread.dumpStack();
			System.exit(0);
		}
			
		
		
		
		//System.out.println("Diff: " + diffSum + " " + diffSum / (double) (parentScores.size()-1));
		
		return diffSum / (double) (parentScores.size()-1);
	}
	
	public void addParentScores(List<Double> scores)
	{
		parentScores.addAll(scores);
	}
	
	public List<Double> getParentScores()
	{
		return this.parentScores;
	}
	
	/**
	 * Gets the board score
	 * @return
	 */
	public double getScore()
	{
		return this.score;
	}
	
	public boolean isMrXMove()
	{
		return this.isMrXMove;
	}
	
	
	/**
	 * Get the set of possible moves that mr X can make given the current
	 * state of the game
	 * @return The list of possible outcome states from mr X moving
	 */
	public List<Board> getMrXMoves(boolean usingDoubleMove)
	{
		List<Board> output = new ArrayList<Board>();
		
		PlayerInfo mrXInfo = players.get(this.mrXId);
		List<PlayerInfo> possibleMoves = getPossibleMoves(mrXInfo);
		
		for(int i = 0; i < possibleMoves.size(); i++)
		{
			HashMap<Integer, PlayerInfo> newInfo = new HashMap<Integer, PlayerInfo>();
			newInfo.putAll(players);
			newInfo.put(mrXId, possibleMoves.get(i));
			
			Board newBoard = new Board(graph, !isMrXMove, newInfo, mrXId, possibleMoves.get(i).move);
			output.add(newBoard);
		}
		
		
		// now we add in the double move options
		if(mrXInfo.tickets.get(Initialisable.TicketType.DoubleMove) > 0 && !usingDoubleMove)
		{
			List<Board> doubleMoveBoards = new ArrayList<Board>();
			for(Board board : output)
			{
				List<Board> doubleBoards = board.getMrXMoves(true);
				for(Board b : doubleBoards)
				{
					// we need to add the previous moves to this board and make sure we reduce the 
					// ticket numbers for the double move ticket
					int doubleMoveTicketNum = b.playerInfo().get(mrXId).tickets.get(Initialisable.TicketType.DoubleMove);
					b.playerInfo().get(mrXId).tickets.put(Initialisable.TicketType.DoubleMove, doubleMoveTicketNum--);
					b.addMoveFirst(board.getMoves().get(0));
					b.addMoveFirst(new Move(Initialisable.TicketType.DoubleMove, mrXInfo.position));
					
				}
				doubleMoveBoards.addAll(doubleBoards);
			}
			
			output.addAll(doubleMoveBoards);
		}

		return output;
	}
	
	
	/**
	 * Function to check if the detectives have won
	 * @return True if the detectives have won
	 */
	public boolean isDetectiveWin()
	{		
		int mrLocation = players.get(mrXId).position;
		for(int key : players.keySet())
		{
			if(key != mrXId)
			{
				if(mrLocation == players.get(key).position)
				{
					return true;
				}
			}
		}
		
		// check if mr x can move
		if(getPossibleMoves(players.get(mrXId)).size() == 0)
			return true;
		
		return false;
	}
	
	
	
	/**
	 * Function to check if mr X has won
	 * @return
	 */
	public boolean isMrXWin()
	{
		boolean allStuck = true;
		for(int key : players.keySet())
		{
			if(key != mrXId)
			{
				if(getPossibleMoves(players.get(key)).size() > 0)
					allStuck = false;
			}
		}		
		return allStuck;
	}
	
	
	/**
	 * Function to get the possible moves made by the detectives
	 * given the current state of the game
	 * @return The set of possible outcome boards
	 */
	public List<Board> getDetectiveMoves()
	{		
		// get the set of possible moves
		Map<Integer, List<PlayerInfo>> possibleMoves = new HashMap<Integer, List<PlayerInfo>>();
		
		// for each player get the set of locations that they can move to
		for(int key : detectiveIds)
		{
			List<PlayerInfo> moves = getPossibleMoves(players.get(key));
			possibleMoves.put(key, moves);
		}
		
		// get the combinations of possible moves
		return getPermutations(possibleMoves);
	}
	
	
	public List<Board> getPermutations(Map<Integer, List<PlayerInfo>> input)
	{
		List<Board> output = new ArrayList<Board>();
		
		// create the indices set
		Map<Integer, Integer> listSizes = new HashMap<Integer, Integer>();
		for(int key : input.keySet())
		{
			int listSize = input.get(key).size();
			listSizes.put(key, listSize);
		}
	
		// initialise the indices
		HashMap<Integer, Integer> perm = new HashMap<Integer,Integer>();
		List<Integer> keys = new ArrayList<Integer>(input.keySet());
		
		for(int key : keys)
		{
			perm.put(key, 0);
		}
		
		
		Board b = createBoard(perm, input);
		output.add(b);
		
		//System.out.println("List Sizes: " + listSizes);
	
		int numPlayers = keys.size();
		boolean finished = false;
		while(!finished)
		{	
			int smallestIndex = numPlayers-1;
			int smallestKey = keys.get(smallestIndex);
			int endval = perm.get(smallestKey);
			perm.put(smallestKey, endval+1);
			
			
			
			
			// now we do the shifting
			for(int i = smallestIndex; i >= 0; i--)
			{
				int key = keys.get(i);
				int val = perm.get(key);
				if(val >= listSizes.get(key))
				{
					
					//System.out.println("Test " + val + " " + listSizes.get(key));
					// end condition
					if(i == 0) 
					{
						finished = true;
						break;
					}
					
					int lessKey = keys.get(i-1);
					perm.put(lessKey, perm.get(lessKey)+1);
					perm.put(key, 0);
				}
			}	
			
			if(!finished)
			{
				Map<Integer, Integer> newPerm = new HashMap<Integer, Integer>();
				newPerm.putAll(perm);
				Board bn = createBoard(newPerm, input);
				output.add(bn);
				
			}	
			
			//System.out.println(perm);
	
			
		}
		
		
		
		//System.exit(1);
		return output;
	}
	
	
	
	/**
	 * Function to create a board given a combination of player moves
	 * @param indices
	 * @param players
	 * @return
	 */
	public Board createBoard(Map<Integer, Integer> indices, Map<Integer, List<PlayerInfo>> playerOptions)
	{
		HashMap<Integer, PlayerInfo> boardPlayers = new HashMap<Integer, PlayerInfo>();
		boardPlayers.putAll(this.players);
		
		for(int key : indices.keySet())
		{
			List<PlayerInfo> playerChoices = playerOptions.get(key);
			
			PlayerInfo info = null;
			
			// if there are no options just keep the detective still
			if(playerChoices.size() == 0)
			{
				info = this.players.get(key);
			}
			else
			{
				info = playerChoices.get(indices.get(key));
			}
			
			boardPlayers.put(key, info);
		}
		
		Board board = new Board(this.graph, !this.isMrXMove, 
				boardPlayers, this.mrXId, 
				boardPlayers.get(mrXId).move);
		
			
		return board;
	}
	
	
	
	
	/**
	 * Function to get the set of next possible player states
	 * @param player
	 * @return
	 */
	public List<PlayerInfo> getPossibleMoves(PlayerInfo player)
	{
		List<PlayerInfo> output = new ArrayList<PlayerInfo>();
		
		// get the connecting edges
		List<Edge> edges = graph.edges(Integer.toString(player.position));
		for(Edge e : edges)
		{
			// is the move possible
			if(!isMovePossible(player, e)) continue;
			
			// make the move by creating a new player info, setting the new
			// location and updating the tickets
			PlayerInfo newInfo = new PlayerInfo();
			newInfo.playerId = player.playerId;
			newInfo.position = Integer.parseInt(e.connectedTo(Integer.toString(player.position)));
			newInfo.tickets.putAll(player.tickets);
			
			
			
			Initialisable.TicketType ticketType = convertEdgeTypeToTicketType(e.type());
			
			newInfo.move = new Move(ticketType, newInfo.position);
			
			
			int ticketNum = player.tickets.get(ticketType);
			newInfo.tickets.put(convertEdgeTypeToTicketType(e.type()), ticketNum-1);
			
			// add to the list
			output.add(newInfo);
		}
		
		return output;	
	}
	
	
	
	public boolean isMovePossible(PlayerInfo player, Edge e)
	{
		if(!hasEnoughTickets(player, e))
			return false;
		
		if(locationIsOccupied(player, e))
			return false;
		
		return true;
	}
	
	public boolean locationIsOccupied(PlayerInfo player, Edge e)
	{
		int otherLocation = Integer.parseInt(
				e.connectedTo(Integer.toString(player.position)));
		
		if(!isMrXMove)
			return false;
		
		for(int key : this.players.keySet())
		{
			PlayerInfo other = this.players.get(key);
			int location = other.position;
			
			// check for same player
			if(player.playerId == other.playerId) continue;
			if(other.playerId == mrXId) continue;
			
			if(location == otherLocation)
				return true;
			
		}
		
		return false;
		
	}
	
	public boolean hasEnoughTickets(PlayerInfo player, Edge e)
	{
		Initialisable.TicketType type = convertEdgeTypeToTicketType(e.type());
		if(player.tickets.get(type) > 0)
			return true;
		else
			return false;		
	}
	
	
	public Initialisable.TicketType convertEdgeTypeToTicketType(Edge.EdgeType edgeType)
	{
		Initialisable.TicketType ticketType = null;

		switch(edgeType)
		{
		case Underground: 
			ticketType = Initialisable.TicketType.Underground;
			break;
		case Taxi: 
			ticketType = Initialisable.TicketType.Taxi;
			break;
		case Bus: 
			ticketType = Initialisable.TicketType.Bus;
			break;
		default:
			break;
		}
		
		return ticketType;
	}

	@Override
	public int compareTo(Board o) {
		return (int) (this.score - o.score);
	}
	
	

}
