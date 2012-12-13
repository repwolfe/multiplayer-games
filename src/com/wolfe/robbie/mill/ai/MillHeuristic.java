package com.wolfe.robbie.mill.ai;

import java.util.HashSet;
import java.util.List;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.Heuristic;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.mill.Globals;
import com.wolfe.robbie.mill.gameobjects.Node;
import com.wolfe.robbie.mill.gameobjects.Piece;


public class MillHeuristic implements Heuristic {
	public enum Type { DUMB, SMART };
	
	private static final int PLUS_INFINITY = Integer.MAX_VALUE - 1;
	private static final int MINUS_INFINITY = Integer.MIN_VALUE + 1;
	
	private Type type;
	public MillHeuristic(Type type) {
		this.type = type;
	}

	@Override
	public int getCurrentHeuristic(State s) {
		MillState state = (MillState) s;
		
		if (state.isDeadState) {
			if (state.currentFirstLast[Globals.LAST] == state.currentFirstLast[Globals.FIRST]) {
				return MINUS_INFINITY;		// You lost
			}
			return PLUS_INFINITY;			// You won
		}
		
		int countPieces = countPieces(true, state);
		
		if (type.equals(Type.SMART)) {
			return countPieces + countMillsAndPotentialMills(state.currentPieces, state.currentFirstLast, state.currentPlayer);
		}
		else if (type.equals(Type.DUMB)) {
			return countPieces;
		}
		return 0;
	}

	@Override
	public int getOpponentHeuristic(State s) {
		MillState state = (MillState) s;
		
		if (state.isDeadState) {
			if (state.currentFirstLast[Globals.LAST] == state.currentFirstLast[Globals.FIRST]) {
				return PLUS_INFINITY;		// He won
			}
			return MINUS_INFINITY;			// He lost
		}
		
		int countPieces = countPieces(false, state);
		
		if (type.equals(Type.SMART)) {
			return countPieces + countMillsAndPotentialMills(state.otherPieces, state.otherFirstLast, eBoardObject.nextPlayer(state.currentPlayer));
		}
		else if (type.equals(Type.DUMB)) {
			return countPieces;
		}
		return 0;
	}
	
	/**
	 * This is the less smart heuristic, which simply
	 * returns the difference between the number of pieces.
	 * 
	 * If current, then a higher number of current pieces is better
	 * If !current, then a higher number of other pieces is better
	 * @param current
	 * @param state
	 * @return
	 */
	private int countPieces(boolean current, MillState state) {
		int numCurrent = state.currentFirstLast[Globals.LAST] - state.currentFirstLast[Globals.FIRST];
		int numOther = state.otherFirstLast[Globals.LAST] - state.otherFirstLast[Globals.FIRST];
		
		if (current) {
			// Ranking for current player: If more current than other, greater number. The great the number, the better
			return numCurrent - numOther;
		}
		else {
			// Ranking for opponent: If more other than current, greater number.
			return numOther - numCurrent;
		}		
	}

	/**
	 * Looks at each node and sees how many threes in a row are formed
	 * and how many twos in a row are formed, horizontally and vertically
	 * 
	 * Multiplies each by a coefficient and adds them
	 * @param pieces
	 * @param firstLast
	 * @return
	 */
	private int countMillsAndPotentialMills(Piece[] pieces, int[] firstLast, eBoardObject currentPlayer) {
		return countMillsAndPotentialMills(pieces, firstLast, currentPlayer, false);
	}
	
	public int countMillsAndPotentialMills(Piece[] pieces, int[] firstLast, eBoardObject currentPlayer, boolean printResult) {
		final int twoBonus = 10;
		final int threeBonus = 20;
		
		HashSet<Point> processedRows = new HashSet<Point>();
		HashSet<Point> processedColumns = new HashSet<Point>();
		
		int numPotentialMills = 0;
		int numMills = 0;
		
		for (int i = firstLast[Globals.FIRST]; i < firstLast[Globals.LAST]; ++i) {
			Node n = pieces[i].getLocation();
			Point location = n.getGridLocation();
			
			if (!processedRows.contains(location)) {				
				List<Node> row = n.getRow();
				
				int numInRow = 0;
				for (Node node : row) {
					processedRows.add(node.getGridLocation());
					if (node.containedPiece != null) {
						if (node.containedPiece.type.equals(currentPlayer)) {
							++numInRow;
						}
						else {
							// Another piece in the way, stop processing
							numInRow = 0;
							break;
						}
					}
				}
				if (numInRow == 3) {
					++numMills;
				}
				else if (numInRow == 2) {
					++numPotentialMills;
				}
			}
			if (!processedColumns.contains(location)) {				
				List<Node> column = n.getColumn();
				
				int numInColumn = 0;
				for (Node node : column) {
					processedColumns.add(node.getGridLocation());
					if (node.containedPiece != null) {
						if (node.containedPiece.type.equals(currentPlayer)) {
							++numInColumn;
						}
						else {
							// Another piece in the way, stop processing
							numInColumn = 0;
							break;
						}
					}
				}
				if (numInColumn == 3) {
					++numMills;
				}
				else if (numInColumn == 2) {
					++numPotentialMills;
				}
			}
		}

		if (printResult) {
			System.out.println("Current Player " + currentPlayer + ". Num Twos: " + numPotentialMills + " Num Threes: " + numMills);
		}
		
		return twoBonus * numPotentialMills + threeBonus * numMills;
	}

}
