package com.wolfe.robbie.reversi.ai;

import com.wolfe.robbie.common.ai.Heuristic;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.reversi.gameobjects.Piece;
import com.wolfe.robbie.virus.Globals;

/**
 * Calculates a rank for the current state of how good it is for the current player
 * @author Robbie
 *
 */
public class ReversiHeuristic implements Heuristic {

	/**
	 * Returns the value of the board for the current player
	 */
	@Override
	public int getCurrentHeuristic(State s) {
		ReversiState state = (ReversiState) s;
		return countOpponentPieces(state.boardPieces, Piece.getEnemyType(state.currentPlayer));
	}

	/**
	 * Returns the value of the board for the opponent 
	 * of the current player
	 */
	@Override
	public int getOpponentHeuristic(State s) {
		ReversiState state = (ReversiState) s;
		return countOpponentPieces(state.boardPieces, state.currentPlayer);
	}

	/**
	 * Returns the difference between the number of pieces and the number
	 * of the opponent
	 * 
	 * ie: The lower the better
	 * @param opponent
	 * @return
	 */
	private int countOpponentPieces(Piece[][] board, int opponent) {
		int numOpponent = 0;
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (board[i][j].getType() == opponent) {
					++numOpponent;
				}
			}
		}
		
		return (Globals.BOARD_DIMENSIONS * Globals.BOARD_DIMENSIONS) - numOpponent;
	}
}
