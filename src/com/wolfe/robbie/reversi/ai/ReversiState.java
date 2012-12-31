package com.wolfe.robbie.reversi.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.reversi.Globals;
import com.wolfe.robbie.reversi.gameobjects.Piece;

/**
 * Represents a Snapshot of the game, used for AI
 * @author Robbie
 *
 */
public class ReversiState extends State {
	public int currentPlayer;
	public Piece[][] boardPieces;
	public Map<Point, Piece> potentialMoves;
	
	public ReversiState(int currentPlayer, Piece[][] boardPieces, Map<Point, Piece> potentialMoves, boolean isDeadState) {
		this.currentPlayer = currentPlayer;
		copyData(boardPieces, potentialMoves);
		this.isDeadState = isDeadState;
	}

	public ReversiState(ReversiState other) {
		this.currentPlayer = other.currentPlayer;
		copyData(other.boardPieces, other.potentialMoves);
		this.isDeadState = other.isDeadState;
	}
	
	/**
	 * Make a deep copy of the two data structures, so the originals aren't modified
	 * @param boardPieces
	 * @param potentialMoves
	 */
	private void copyData(Piece[][] boardPieces, Map<Point, Piece> potentialMoves) {
		this.boardPieces = new Piece[Globals.BOARD_DIMENSIONS][Globals.BOARD_DIMENSIONS];
		this.potentialMoves = new HashMap<Point, Piece>();
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				Piece copy = new Piece(boardPieces[i][j]);
				Point location = new Point(copy.getX(), copy.getY());
				this.boardPieces[i][j] = copy;
				if (potentialMoves.containsKey(location)) {
					this.potentialMoves.put(location, copy);
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		return currentPlayer + boardPieces.hashCode() + potentialMoves.hashCode();
	}
	
	/**
	 * Two states are equal if all their parts are
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ReversiState)) {
			return false;
		}
		
		ReversiState other = (ReversiState) o;
		
		if (currentPlayer != other.currentPlayer || potentialMoves.size() != other.potentialMoves.size()) {
			return false;
		}
		
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (!boardPieces[i][j].equals(other.boardPieces[i][j])) {
					return false;
				}
			}
		}
		
		for (Entry<Point, Piece> pair : potentialMoves.entrySet()) {
			Piece p = other.potentialMoves.get(pair.getKey());
			if (p == null || !p.equals(pair.getValue())) {
				return false;
			}
		}
		
		return true;
	}
}
