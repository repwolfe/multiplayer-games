package com.wolfe.robbie.common.ai;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class MiniMax {
	private ProductionManager pm;
	private Heuristic heuristic;
	
	private PriorityQueue<AINode> pq;
	private HashMap<State, List<AINode>> successors;
	
	private int ALPHA = 0;
	private int BETA = 1;
	private int numNodesExpanded = 0;
	
	private boolean usePruning;
	private int plyAmount;
	
	/**
	 * Tool to create moves using minimax algorithm
	 * @param pm the object responsible for creating the moves
	 * @param hs the heuristic which returns the payoff for the current state
	 * @param usePruning if should use alpha beta pruning
	 * @param plyAmount the amount of moves to look ahead
	 */
	public MiniMax(ProductionManager pm, Heuristic hs, boolean usePruning, int plyAmount) {
		this.pm = pm;
		this.heuristic = hs;
		this.usePruning = usePruning;
		this.plyAmount = plyAmount;
		pq = new PriorityQueue<AINode>(25, new Comparator<AINode>() {
			@Override
			public int compare(AINode o1, AINode o2) {
				return o2.state.value - o1.state.value;
			}			
		});
	}
	
	/**
	 * For each move possible from the given state, 
	 * gets the min value determined by the opponent
	 * picking the subsequent worse move for you.
	 * 
	 * Then take the max of all those moves
	 * 
	 * @param currentState
	 * @param plyAmount
	 * @return
	 */
	public Action getNextMove(State currentState) {
		pq.clear();
		List<AINode> nextMoves = pm.createNextMoves(currentState);
		numNodesExpanded = nextMoves.size();
		
		if (usePruning) {
			int[] alphaBeta = new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE };
			successors = new HashMap<State, List<AINode>>();
			successors.put(currentState, nextMoves);
			
			AINode node = new AINode(currentState, null);
			int value = maxValue(node, plyAmount, alphaBeta);
			
			// Find the node with this value
			for (AINode move : nextMoves) {
				if (move.value == value) {
					System.out.println("Number of nodes expanded: "  + numNodesExpanded);
					return move.action;
				}
			}
			
			// Shouldn't ever happen
			System.err.println("Returning null! MAX_VALUE: " + value);
			StringBuilder builder = new StringBuilder();
			for (AINode theMove : nextMoves) {
				builder.append(theMove.action + ":" + theMove.value + ", ");
			}
			System.err.println(builder.toString());
			return null;
		}
		else {
			// No pruning, examine whole tree
			for (AINode node : nextMoves) {
				node.state.value = minValue(node.state, plyAmount);
				pq.add(node);
			}

			System.out.println("Number of nodes expanded: "  + numNodesExpanded);
			
			// Return Action of Node with highest value
			return pq.poll().action;
		}
	}
	
	private int maxValue(AINode node, int depth, int[] alphaBeta) {
		if (depth <= 0 || node.state.isDeadState) {
			// Stop going deeper in tree
			node.value = heuristic.getCurrentHeuristic(node.state) - (plyAmount - depth);		// Better value the sooner the move
			return node.value;
		}
		
		int value = Integer.MIN_VALUE;

		List<AINode> nextMoves = getSuccessors(node.state);
		numNodesExpanded += nextMoves.size();
		
		for (AINode move : nextMoves) {
			value = Math.max(value, minValue(move, depth - 1, alphaBeta));
			node.value = value;
			
			// If at any point ___________, no need to continue expanding
			if (value >= alphaBeta[BETA]) {
				return value;
			}
			alphaBeta[ALPHA] = Math.max(alphaBeta[ALPHA], value);
		}
		
		return value;
	}
	
	private int minValue(AINode node, int depth, int[] alphaBeta) {
		if (depth <= 0 || node.state.isDeadState) {
			// Stop going deeper in tree
			node.value = heuristic.getOpponentHeuristic(node.state) - (plyAmount - depth);		// Better value the sooner the move
			return node.value;
		}
		
		int value = Integer.MAX_VALUE;

		List<AINode> nextMoves = getSuccessors(node.state);
		numNodesExpanded += nextMoves.size();
		
		for (AINode move : nextMoves) {
			value = Math.min(value, maxValue(move, depth - 1, alphaBeta));
			node.value = value;
			
			// If at any point ___________, no need to continue expanding
			if (value <= alphaBeta[ALPHA]) {
				return value;
			}
			alphaBeta[BETA] = Math.max(alphaBeta[BETA], value);
		}
		
		return value;
	}
	/**
	 * Alpha-Beta pruning method of getting the max (best) move from a given state
	 * Perspective of the player, return the best move
	 * 
	 * From the given state, gets all the next moves the other
	 * player could make, and return the value of the maximum move
	 * @param state
	 * @param depth
	 * @param alpha best score for MAX so far
	 * @param beta best score for MIN so far
	 * @return
	 */
	/*
	private int maxValue(AINode node, int depth, int[] alphaBeta) {
		if (depth <= 0 || node.state.isDeadState) {
			// Stop going deeper in tree
			return heuristic.getCurrentHeuristic(node.state);
		}
		
		List<AINode> nextMoves = getSuccessors(node.state);
		numNodesExpanded += nextMoves.size();
		
		for (AINode move : nextMoves) {
			alphaBeta[ALPHA] = Math.max(alphaBeta[ALPHA], minValue(move, depth - 1, alphaBeta));
			
			// If at any point ___________, no need to continue expanding
			if (alphaBeta[ALPHA] >= alphaBeta[BETA]) {
				node.value = alphaBeta[BETA];
				return alphaBeta[ALPHA];
			}
		}
		
		node.value = alphaBeta[BETA];
		return alphaBeta[ALPHA];
	}
	*/
	/**
	 * Alpha-Beta pruning method of getting the min (worst) move from a given state	 * 
	 * Perspective of opponent, return the worst move for the player
	 * 
	 * From the given state, gets all the next moves I could make
	 * and have the other player return the value of the minimum move
	 * @param state
	 * @param depth
	 * @param alpha best score for MAX so far
	 * @param beta best score for MIN so far
	 * @return
	 */
	/*
	private int minValue(AINode node, int depth, int[] alphaBeta) {
		if (depth <= 0 || node.state.isDeadState) {
			// Stop going deeper in tree
			return heuristic.getOpponentHeuristic(node.state);
		}
		
		List<AINode> nextMoves = getSuccessors(node.state);
		numNodesExpanded += nextMoves.size();
		
		for (AINode move : nextMoves) {
			alphaBeta[BETA] = Math.min(alphaBeta[BETA], maxValue(move, depth - 1, alphaBeta));
			
			// If at any point ___________, no need to continue expanding
			if (alphaBeta[BETA] <= alphaBeta[ALPHA]) {
				node.value = alphaBeta[BETA];
				return alphaBeta[BETA];
			}
		}
		node.value = alphaBeta[BETA];
		return alphaBeta[BETA];
	}
	*/
	/*************************************
	 * Non Alpha - Beta Pruning Methods  *
	 *************************************/
	
	/**
	 * Method of getting the max (best) move from a given state
	 * Perspective of the player, return the best move
	 * 
	 * From the given state, gets all the next moves the other
	 * player could make, and return the value of the maximum move
	 * @param state
	 * @param depth
	 * @return
	 */
	private int maxValue(State state, int depth) {
		if (depth <= 0 || state.isDeadState) {
			// Stop going deeper in tree
			return heuristic.getCurrentHeuristic(state);
		}
		
		int value = Integer.MIN_VALUE;
		List<AINode> nextMoves = pm.createNextMoves(state);
		numNodesExpanded += nextMoves.size();
		
		for (AINode node : nextMoves) {
			value = Math.max(value, minValue(node.state, depth - 1));
		}

		return value;
	}
	
	/**
	 * Method of getting the min (worst) move from a given state
	 * Perspective of opponent, return the worst move for the player
	 * 
	 * From the given state, gets all the next moves I could make
	 * and have the other player return the value of the minimum move
	 * @param state
	 * @param depth
	 * @return
	 */
	private int minValue(State state, int depth) {
		if (depth <= 0 || state.isDeadState) {
			// Stop going deeper in tree
			return heuristic.getOpponentHeuristic(state);
		}
		
		int value = Integer.MAX_VALUE;
		List<AINode> nextMoves = pm.createNextMoves(state);
		numNodesExpanded += nextMoves.size();
		
		for (AINode node : nextMoves) {
			value = Math.min(value, maxValue(node.state, depth - 1));
		}
		
		return value;
	}
	
	private List<AINode> getSuccessors(State state) {
		List<AINode> moves = successors.get(state);
		if (moves == null) {
			moves = pm.createNextMoves(state);
			successors.put(state, moves);
		}
		return moves;
	}
}
