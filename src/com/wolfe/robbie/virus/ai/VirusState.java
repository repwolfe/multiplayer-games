package com.wolfe.robbie.virus.ai;

import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.virus.Globals;

public class VirusState extends State {
	public eBoardObject currentPlayer;
	public eBoardObject[][] gameBoard;
	
	public VirusState(eBoardObject currentPlayer, eBoardObject[][] gameBoard, boolean isDeadState) {
		this.currentPlayer = currentPlayer;
		copyBoard(gameBoard);
		this.isDeadState = isDeadState;
	}
	
	public VirusState(VirusState other) {
		this.currentPlayer = other.currentPlayer;
		copyBoard(other.gameBoard);
		this.isDeadState = other.isDeadState;
	}
	
	private void copyBoard(eBoardObject[][] gameBoard) {
		this.gameBoard = new eBoardObject[Globals.BOARD_DIMENSIONS][Globals.BOARD_DIMENSIONS];
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				this.gameBoard[i][j] = gameBoard[i][j];
			}
		}
	}
	
	@Override
	public int hashCode() {
		return currentPlayer.hashCode() + gameBoard.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof VirusState)) {
			return false;
		}
		
		VirusState state = (VirusState) o;
		
		if (!currentPlayer.equals(state.currentPlayer)) {
			return false;
		}
		
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (!gameBoard[i][j].equals(state.gameBoard[i][j])) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * For debugging
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Current Player: " + currentPlayer + ": Dead? " + isDeadState + "\n");
		
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				eBoardObject obj = gameBoard[j][i];
				switch (obj) {
				case DUMBPLAYER:
					builder.append('B');
					break;
				case EMPTY:
					builder.append('#');
					break;
				case SMARTPLAYER:
					builder.append('R');
					break;				
				}
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}
}
