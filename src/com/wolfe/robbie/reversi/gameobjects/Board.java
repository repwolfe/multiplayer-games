package com.wolfe.robbie.reversi.gameobjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import processing.core.PApplet;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.ai.MiniMax;
import com.wolfe.robbie.reversi.Globals;
import com.wolfe.robbie.reversi.ai.ReversiHeuristic;
import com.wolfe.robbie.reversi.ai.ReversiProductionManager;
import com.wolfe.robbie.reversi.ai.ReversiState;

/**
 * Contains all the information for the game board
 * @author Robbie
 *
 */
public class Board implements GameObject {
	private Piece[][] boardPieces;
	
	private int currentPlayer;
	private boolean gameOver;
	
	private ReversiProductionManager moveManager;
	private MiniMax artificialIntelligence;
	
	private Map<Point, Piece> potentialMoves;
	
	public Board() {
		initializeBoard();
	}
	
	private void initializeBoard() {
		Piece.init(Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS);
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
		
		potentialMoves = new HashMap<Point, Piece>();
		moveManager.makePotentialMoves(potentialMoves, boardPieces, currentPlayer);
	}
	
	@SuppressWarnings("unused")
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
	 * Checks if no legal moves left for a player
	 * @return
	 */
	private boolean checkIsGameOver() {
		gameOver = moveManager.isGameOver(potentialMoves);
		return gameOver;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}

	/**
	 * The "dumb" player (ie: You), or another computer with potentially worse heuristics
	 */
	private void dumbPlayersMove() {
		if (Globals.DUMB_PLAYER_USES_AI) {
			aiMove();
		}
		else {
			randomlyPlacePiece(currentPlayer);
		}
		currentPlayer = Piece.SMARTPLAYER;
		moveManager.makePotentialMoves(potentialMoves, boardPieces, currentPlayer);
	}
	
	/**
	 * The computer player, who usually uses AI
	 */
	private void smartPlayersMove() {
		if (Globals.SMART_PLAYER_USES_AI) {
			aiMove();
		}
		else {
			randomlyPlacePiece(currentPlayer);
		}
		currentPlayer = Piece.DUMBPLAYER;
		moveManager.makePotentialMoves(potentialMoves, boardPieces, currentPlayer);
	}
	
	/**
	 * Generate an AI Move and execute it
	 */
	private void aiMove() {
		Point nextMove = (Point) artificialIntelligence.getNextMove(getCurrentState());
		Piece move = potentialMoves.remove(nextMove);
		moveManager.playMove(move, boardPieces, currentPlayer);
	}
	
	/**
	 * Pick a random potential move and execute it
	 * @param currentPlayer
	 */
	private void randomlyPlacePiece(int currentPlayer) {
		Set<Entry<Point, Piece>> moves = potentialMoves.entrySet();
		int i = 0;
		int randNum = Globals.random.nextInt(moves.size());
		Piece move = null;
		for (Entry<Point, Piece> potential : moves) {
			if (i++ == randNum) {
				move = potentialMoves.remove(potential.getKey());
				break;
			}
		}
		moveManager.playMove(move, boardPieces, currentPlayer);
	}
	
	@Override
	public void draw(PApplet g) {		
		// Draw
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				boardPieces[i][j].draw(g);
			}
		}
		
		if (gameOver) {
			g.fill(0, 170);
			g.rect(0, 0, g.width, g.height);
			
			g.fill(255);
			g.text("Game Over", g.width/2-35, g.height/2);
			g.text("Winner is: " + getWinner(), g.width/2 - 95, g.height/2 + 50);
		}
		
		// Animate any pieces converting
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				boardPieces[i][j].update();
			}
		}
	}
	
	/**
	 * Prints the winner based off of who has more pieces
	 * @return
	 */
	private String getWinner() {
		int numDumb = 0, numSmart = 0;
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				int type = boardPieces[i][j].getType();
				if (type == Piece.DUMBPLAYER) {
					++numDumb;
				}
				else if (type == Piece.SMARTPLAYER) {
					++numSmart;
				}
			}
		}
		if (numDumb > numSmart) {
			return Piece.getPlayerColor(Piece.DUMBPLAYER) + " with " + numDumb + " pieces";
		}
		else if (numSmart > numDumb) {
			return Piece.getPlayerColor(Piece.SMARTPLAYER) + " with " + numSmart + " pieces";
		}
		return "Tie";
	}

	/**
	 * Handle mouseclicks, if clicked on a potential move then execute it
	 * @param mouseX
	 * @param mouseY
	 */
	public void clickedMove(int mouseX, int mouseY) {
		if (currentPlayer != Piece.DUMBPLAYER) {
			return;
		}
		final int squareWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int squareHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		
		int whichX = mouseX / squareWidth;
		int whichY =  mouseY / squareHeight;
		
		Point point = new Point(whichX, whichY);
		Piece move = potentialMoves.remove(point);
		
		if (move != null) {
			moveManager.playMove(move, boardPieces, currentPlayer);
			currentPlayer = Piece.SMARTPLAYER;
			moveManager.makePotentialMoves(potentialMoves, boardPieces, currentPlayer);
		}
	}
	
	/**
	 * A snapshot of the game
	 * @return
	 */
	private ReversiState getCurrentState() {
		return new ReversiState(currentPlayer, boardPieces, potentialMoves, gameOver);
	}

}
