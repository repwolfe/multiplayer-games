package com.wolfe.robbie.virus.ai;

import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.Heuristic;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.virus.Globals;

public class VirusHeuristic implements Heuristic {

	/**
	 * Returns the value of the board for the current player
	 */
	@Override
	public int getCurrentHeuristic(State s) {
		VirusState state = (VirusState) s;
		return countOpponentPieces(state.gameBoard, eBoardObject.nextPlayer(state.currentPlayer));
	}
	
	/**
	 * Returns the value of the board for the opponent 
	 * of the current player
	 */
	@Override
	public int getOpponentHeuristic(State s) {
		VirusState state = (VirusState) s;
		return countOpponentPieces(state.gameBoard, state.currentPlayer);
	}
	
	/**
	 * Returns the difference between the number of pieces and the number
	 * of the opponent
	 * 
	 * ie: The lower the better
	 * @param opponent
	 * @return
	 */
	private int countOpponentPieces(eBoardObject[][] board, eBoardObject opponent) {
		int numOpponent = 0;
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (board[i][j].equals(opponent)) {
					++numOpponent;
				}
			}
		}
		
		return (Globals.BOARD_DIMENSIONS * Globals.BOARD_DIMENSIONS) - numOpponent;
	}
}
