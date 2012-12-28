package com.wolfe.robbie.reversi.gameobjects;

import processing.core.PApplet;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.Action;
import com.wolfe.robbie.common.ai.MiniMax;
import com.wolfe.robbie.reversi.Globals;
import com.wolfe.robbie.reversi.ai.ReversiHeuristic;
import com.wolfe.robbie.reversi.ai.ReversiProductionManager;
import com.wolfe.robbie.reversi.ai.ReversiState;

public class Board implements GameObject {
	
	private eBoardObject currentPlayer;
	private boolean gameOver;

	private Point selectedPiece;
	
	private ReversiProductionManager moveManager;
	private MiniMax artificialIntelligence;
	
	public Board() {
		initializeBoard();
	}
	
	private void initializeBoard() {
		currentPlayer = eBoardObject.DUMBPLAYER;		
		gameOver = false;		
		selectedPiece = null;
		
		moveManager = new ReversiProductionManager();
		artificialIntelligence = new MiniMax(moveManager, new ReversiHeuristic(), Globals.USE_ALPHA_BETA_PRUNING, Globals.PLY_AMOUNT);
	}
	
	private void switchPlayer() {
		if (currentPlayer.equals(eBoardObject.DUMBPLAYER)) {
			currentPlayer = eBoardObject.SMARTPLAYER;
			//currentPieces = smartPieces;
		}
		else if (currentPlayer.equals(eBoardObject.SMARTPLAYER)) {
			currentPlayer = eBoardObject.DUMBPLAYER;
			//currentPieces = dumbPieces;
		}
	}
	
	@Override
	public void update() {
		if (!gameOver && !checkIsGameOver()) {
			// Dumb players turn and you don't make its moves for it
			if (currentPlayer.equals(eBoardObject.DUMBPLAYER) && !Globals.PLAY_AS_DUMB_PLAYER) {
				dumbPlayersMove();
			}
			else if (currentPlayer.equals(eBoardObject.SMARTPLAYER)) {
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
			randomMove();
		}
		switchPlayer();
	}
	
	private void smartPlayersMove() {
		if (Globals.SMART_PLAYER_USES_AI) {
			Action nextMove = artificialIntelligence.getNextMove(getCurrentState());
			//executeAIMove(nextMove);
		}
		else {
			randomMove();
		}
		switchPlayer();
	}
	
	private void randomMove() {
		
	}

	@Override
	public void draw(PApplet g) {
		if (gameOver) {
			g.fill(0, 170);
			g.rect(0, 0, g.width, g.height);
			
			g.fill(255);
			g.text("Game Over", g.width/2-35, g.height/2);
		}
		final int squareWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int squareHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
				
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				/*
				// Highlight last move
				if (lastMove != null && i == lastMove.x && j == lastMove.y) {
					g.stroke(255, 255, 0);
					g.strokeWeight(5);
				}
				else {*/
					g.stroke(0);
					g.strokeWeight(1);
				//}
				/*
				eBoardObject obj = boardData[i][j];
				
				if (obj.equals(eBoardObject.EMPTY)) {
					g.fill(g.color(255));
				}
				else if (obj.equals(eBoardObject.SMARTPLAYER)) {
					g.fill(g.color(255,0,0));
				}
				else if (obj.equals(eBoardObject.DUMBPLAYER)) {
					g.fill(g.color(0,0,255));
				}
				*/
				
				final int xLocation = i*squareWidth;
				final int yLocation = j*squareHeight;
				g.fill(g.color(0,200,0));
				g.rect(xLocation, yLocation, squareWidth, squareHeight);
			}
		}
		g.stroke(0);
	}

	public void clickedMove(int mouseX, int mouseY) {
		if (!currentPlayer.equals(eBoardObject.DUMBPLAYER)) {
			return;
		}
		final int squareWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int squareHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		
		int whichX = mouseX / squareWidth;
		int whichY =  mouseY / squareHeight;
	}
	
	private ReversiState getCurrentState() {
		return new ReversiState(/*currentPlayer,
							 currentPieces, otherPieces, 
							 currentFirstLast, otherFirstLast, 
							 currentStage, gameOver*/);
	}

}
