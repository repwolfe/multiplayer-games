package com.wolfe.robbie.reversi.gameobjects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import processing.core.PApplet;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.Point;
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
	
	private HashMap<Point, Piece> potentialMoves;
	
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
		makePotentialMoves();
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
	 * Checks if no legal moves left for a player or if a player runs out of pieces
	 * @return
	 */
	private boolean checkIsGameOver() {
		gameOver = potentialMoves.isEmpty();
		return gameOver;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	private void makePotentialMoves() {
		for (Entry<Point, Piece> pair : potentialMoves.entrySet()) {
			pair.getValue().makeEmpty();
		}
		potentialMoves.clear();
		
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (boardPieces[i][j].getType() == currentPlayer) {
					List<Piece> sandwiches = findSandwichPieces(i, j, currentPlayer);
					for (Piece piece : sandwiches) {
						if (piece.getType() != currentPlayer) {
							piece.makePotential(currentPlayer);
							potentialMoves.put(new Point(piece.getX(), piece.getY()), piece);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Go in all 8 directions, if adjacent to this piece is the other type of piece,
	 * keep going in that direction until reach the end. Returns all the pieces at the end,
	 * which form the other end of the sandwich
	 * @param x
	 * @param y
	 * @param playerType
	 * @Param enemyType
	 */
	private List<Piece> findSandwichPieces(int x, int y, int playerType) {
		List<Piece> sandwiches = new LinkedList<Piece>();
		int enemyType = Piece.getEnemyType(currentPlayer);
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				if (i == 0 && j == 0) {
					// Ignore self
					continue;
				}
				int multiplier = 1;
				int currentType = enemyType;
				Piece move = null;
					
				// Keep going in this direction until hit empty piece or end of board
				while (currentType == enemyType) {
					int newX = x + (i * multiplier);
					int newY = y + (j * multiplier);
					if (newX < 0 || newY < 0 || newX >= Globals.BOARD_DIMENSIONS || newY >= Globals.BOARD_DIMENSIONS) {
						move = null;
						break;
					}
					move = boardPieces[newX][newY];
					currentType = move.getType();
					++multiplier;
				}
				
				// We went at least 1 piece away and found an empty piece
				if (multiplier > 2 && move != null) {
					 sandwiches.add(move);					
				}
			}
		}
		return sandwiches;
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
		makePotentialMoves();
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
		makePotentialMoves();
	}
	
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
		playMove(move);
	}
	
	private void playMove(Piece move) {
		move.setType(currentPlayer);
		Point destination = new Point(move.getX(), move.getY());
		List<Piece> sandwiches = findSandwichPieces(destination.x, destination.y, currentPlayer);
		for (Piece piece : sandwiches) {
			if (piece.getType() != currentPlayer) {
				continue;
			}
			// Go to each piece in between move and piece
			Point current = new Point(piece.getX(), piece.getY());
			int diffX = destination.x - current.x;
			int diffY = destination.y - current.y;
			int offsetX = 0, offsetY = 0;
			
			if (diffX > 0) {
				offsetX = 1;
			}
			else if (diffX < 0) {
				offsetX = -1;
			}
			if (diffY > 0) {
				offsetY = 1;
			}
			else if (diffY < 0) {
				offsetY = -1;
			}
			current.x += offsetX;
			current.y += offsetY;
			while (current.equals(destination) == false) {
				boardPieces[current.x][current.y].convertPiece();
				current.x += offsetX;
				current.y += offsetY;
			}
		}
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
			g.text("Winner is: " + getWinner(), g.width/2 - 35, g.height/2 + 50);
		}
		
		// Update
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				boardPieces[i][j].update();
			}
		}
	}
	
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
			return "Dumb Player";
		}
		else if (numSmart > numDumb) {
			return "Smart Player";
		}
		return "Tie";
	}

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
			playMove(move);
			currentPlayer = Piece.SMARTPLAYER;
			makePotentialMoves();
		}
	}
	
	private ReversiState getCurrentState() {
		return new ReversiState(/*currentPlayer,
							 currentPieces, otherPieces, 
							 currentFirstLast, otherFirstLast, 
							 currentStage, gameOver*/);
	}

}
