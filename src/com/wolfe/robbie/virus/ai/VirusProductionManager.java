package com.wolfe.robbie.virus.ai;

import java.util.LinkedList;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.AINode;
import com.wolfe.robbie.common.ai.ProductionManager;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.virus.Globals;


public class VirusProductionManager implements ProductionManager {

	@Override
	public LinkedList<AINode> createNextMoves(State iState) {
		if (iState.isDeadState) {
			return null;	// TODO
		}
		
		VirusState initialState = (VirusState) iState;
		LinkedList<AINode> moves = new LinkedList<AINode>();
		
		// Look at every spot on the board, see if its adjacent to current player
		// If so, use that as a possible next move
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (isAdjacent(i, j, initialState.gameBoard, initialState.currentPlayer)) {
					// Possible move
					VirusState state = new VirusState(initialState);
					executeMove(i, j, state.gameBoard, state.currentPlayer);
					state.currentPlayer = eBoardObject.nextPlayer(state.currentPlayer);
					state.isDeadState = isGameOver(state.gameBoard);
					
					AINode node = new AINode(state, new Point(i, j));
					moves.add(node);
				}
			}
		}
		
		return moves;
	}
	
	/**
	 * Returns true if the game is over, false otherwise
	 * @param boardData
	 * @return
	 */
	public boolean isGameOver(eBoardObject[][] boardData) {
		boolean noSmartPlayer = true;
		boolean noDumbPlayer = true;
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (boardData[i][j].equals(eBoardObject.SMARTPLAYER)) {
					noSmartPlayer = false;
				}
				else if (boardData[i][j].equals(eBoardObject.DUMBPLAYER)) {
					noDumbPlayer = false;
				}
				
				// If at any point seen both pieces at least once, game not over
				if (!noDumbPlayer && !noSmartPlayer) {
					return false;
				}
			}
		}
		return noSmartPlayer || noDumbPlayer;
	}
	
	/**
	 * Have the current player select the given x and y position to play a move
	 * Only if its valid
	 * 
	 * If moved, return true, else return false
	 * @param x
	 * @param y
	 * @param boardData
	 * @param currentPlayer
	 */
	public boolean playMove(int x, int y, eBoardObject[][] boardData, eBoardObject currentPlayer) {
		if (!Globals.ALLOW_MOVE_ON_ITSELF && boardData[x][y] == currentPlayer) {
			return false;
		}
		if (isAdjacent(x, y, boardData, currentPlayer)) {
			executeMove(x, y, boardData, currentPlayer);
			return true;
		}
		return false;
	}
	
	/**
	 * Actually perform the move
	 * @param x
	 * @param y
	 * @param boardData
	 * @param currentPlayer
	 */
	private void executeMove(int x, int y, eBoardObject[][] boardData, eBoardObject currentPlayer) {
		boardData[x][y] = currentPlayer;
		infect(x, y, boardData, currentPlayer);
	}
	
	/**
	 * Returns true if the given x,y coordinates are adjacent
	 * in any of the 8 directions to a piece of its own
	 * or if x,y is already its own piece
	 * 
	 * False otherwise
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isAdjacent(int x, int y, eBoardObject[][] boardData, eBoardObject player) {
		// Go in all 8 directions, avoiding edge cases and skipping x,y itself
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				if (!Globals.ALLOW_MOVE_ON_ITSELF && (i == 0 && j == 0) ||
					(x+i < 0 || y+j < 0) || 
					(x+i >= Globals.BOARD_DIMENSIONS || y+j >= Globals.BOARD_DIMENSIONS)) {
					continue;
				}
				if (boardData[x+i][y+j].equals(player)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Takes a given piece and infects the pieces above, below,
	 * left and right of it with the same type of piece
	 * @param x
	 * @param y
	 * @param player
	 */
	public void infect(int x, int y, eBoardObject[][] boardData, eBoardObject player) {
		if (y-1 >= 0) {
			boardData[x][y-1] = player;
		}
		if (x-1 >= 0) {
			boardData[x-1][y] = player;
		}
		if (x+1 < Globals.BOARD_DIMENSIONS) {
			boardData[x+1][y] = player;
		}
		if (y+1 < Globals.BOARD_DIMENSIONS) {
			boardData[x][y+1] = player;
		}
	}

}
