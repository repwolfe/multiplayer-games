package com.wolfe.robbie.reversi.gameobjects;

import processing.core.PApplet;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.ai.Action;
import com.wolfe.robbie.common.ai.MiniMax;
import com.wolfe.robbie.reversi.Globals;
import com.wolfe.robbie.reversi.ai.ReversiHeuristic;
import com.wolfe.robbie.reversi.ai.ReversiProductionManager;
import com.wolfe.robbie.reversi.ai.ReversiState;

public class Board implements GameObject {
	private Piece[][] boardPieces;
	
	private int currentPlayer;
	private boolean gameOver;
	
	private ReversiProductionManager moveManager;
	private MiniMax artificialIntelligence;
	
	public Board(PApplet g) {
		initializeBoard(g);
	}
	
	private void initializeBoard(PApplet g) {
		Piece.init(g, Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS);
		boardPieces = new Piece[Globals.BOARD_DIMENSIONS][Globals.BOARD_DIMENSIONS];
		currentPlayer = Piece.DUMBPLAYER;		
		gameOver = false;
		
		moveManager = new ReversiProductionManager();
		artificialIntelligence = new MiniMax(moveManager, new ReversiHeuristic(), Globals.USE_ALPHA_BETA_PRUNING, Globals.PLY_AMOUNT);
		
		// Used for initial pieces on the board, the center 4 pieces
		int halfOne = Globals.BOARD_DIMENSIONS / 2;
		int halfTwo = halfOne - 1;
		
		// Initialize board
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (i == halfTwo && j == halfTwo || i == halfOne && j == halfOne) {
					boardPieces[i][j] = new Piece(i, j, Piece.DUMBPLAYER);
				}
				else if (i == halfTwo && j == halfOne || i == halfOne && j == halfTwo) {
					boardPieces[i][j] = new Piece(i, j, Piece.SMARTPLAYER);
				}
				else {
					boardPieces[i][j] = new Piece(i,j);
				}
			}
		}
	}
	
	@Override
	public void update() {
		if (!gameOver && !checkIsGameOver()) {
			// Dumb players turn and you don't make its moves for it
			if (currentPlayer == Piece.DUMBPLAYER && !Globals.PLAY_AS_DUMB_PLAYER) {
				dumbPlayersMove();
			}
			else if (currentPlayer == Piece.SMARTPLAYER) {
				smartPlayersMove();
			}
		}
	}
	
	/**
	 * Checks if no legal moves left for a player or if a player runs out of pieces
	 * @return
	 */
	private boolean checkIsGameOver() {
		//gameOver = moveManager.checkIsGameOver(/*currentStage, dumbFirstLast, smartFirstLast, dumbPieces, smartPieces*/);
		
		return gameOver;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	private void dumbPlayersMove() {
		if (Globals.DUMB_PLAYER_USES_AI) {
			//MillMove nextMove = (MillMove) dumbAI.getNextMove(getCurrentState());
			//executeAIMove(nextMove);
		}
		else {
			randomlyPlacePiece(currentPlayer);
		}
		currentPlayer = Piece.SMARTPLAYER;
	}
	
	private void smartPlayersMove() {
		if (Globals.SMART_PLAYER_USES_AI) {
			Action nextMove = artificialIntelligence.getNextMove(getCurrentState());
			//executeAIMove(nextMove);
		}
		else {
			randomlyPlacePiece(currentPlayer);
		}
		currentPlayer = Piece.DUMBPLAYER;
	}
	
	private void randomlyPlacePiece(int currentPlayer) {
		
	}
	
	@Override
	public void draw(PApplet g) {
		if (gameOver) {
			g.fill(0, 170);
			g.rect(0, 0, g.width, g.height);
			
			g.fill(255);
			g.text("Game Over", g.width/2-35, g.height/2);
		}
				
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				boardPieces[i][j].draw(g);
			}
		}
		g.stroke(0);
	}

	public void clickedMove(int mouseX, int mouseY) {
		if (currentPlayer != Piece.DUMBPLAYER) {
			return;
		}
		//final int squareWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		//final int squareHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		
		//int whichX = mouseX / squareWidth;
		//int whichY =  mouseY / squareHeight;
	}
	
	private ReversiState getCurrentState() {
		return new ReversiState(/*currentPlayer,
							 currentPieces, otherPieces, 
							 currentFirstLast, otherFirstLast, 
							 currentStage, gameOver*/);
	}

}
