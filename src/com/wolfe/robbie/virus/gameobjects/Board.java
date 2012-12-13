package com.wolfe.robbie.virus.gameobjects;

import java.util.LinkedList;
import java.util.List;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.AINode;
import com.wolfe.robbie.common.ai.MiniMax;
import com.wolfe.robbie.virus.Globals;
import com.wolfe.robbie.virus.ai.VirusHeuristic;
import com.wolfe.robbie.virus.ai.VirusProductionManager;
import com.wolfe.robbie.virus.ai.VirusState;

import processing.core.PApplet;


public class Board implements GameObject {
	
	private eBoardObject[][] boardData;
	
	private boolean gameOver = false;
	
	private eBoardObject currentPlayer = eBoardObject.DUMBPLAYER;
	private VirusProductionManager moveManager;
	private MiniMax artificialIntelligence;
	
	private Point lastMove = null;
	
	private List<AINode> moves = new LinkedList<AINode>();
		
	public Board() {
		boardData = new eBoardObject[Globals.BOARD_DIMENSIONS][Globals.BOARD_DIMENSIONS];
		moveManager = new VirusProductionManager();
		artificialIntelligence = new MiniMax(moveManager, new VirusHeuristic(), Globals.USE_ALPHA_BETA_PRUNING, Globals.PLY_AMOUNT);
		
		// Initialize board
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				boardData[i][j] = eBoardObject.EMPTY;
			}
		}
		
		randomlyPlacePieces(3, eBoardObject.SMARTPLAYER);
		randomlyPlacePieces(3, eBoardObject.DUMBPLAYER);
	}

	@Override
	public void update() {
		// Do next persons move, if they are waiting to go
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
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	/**
	 * Game is over if no colours of one of the players
	 * @return
	 */
	private boolean checkIsGameOver() {
		gameOver = moveManager.isGameOver(boardData);
		return gameOver;
	}
	
	private void dumbPlayersMove() {
		randomlyPlacePiece(currentPlayer);
		currentPlayer = eBoardObject.SMARTPLAYER;
		if (Globals.DEBUG) {
			moves = moveManager.createNextMoves(getCurrentState());
		}
	}
	
	/**
	 * Use the artificial intelligence to pick the next move
	 */
	private void smartPlayersMove() {
		if (Globals.SMART_PLAYER_USES_AI) {
			Point nextMove = (Point) artificialIntelligence.getNextMove(getCurrentState());
			moveManager.playMove(nextMove.x, nextMove.y, boardData, currentPlayer);
			lastMove = nextMove;
		}
		else {
			randomlyPlacePiece(currentPlayer);
		}
		currentPlayer = eBoardObject.DUMBPLAYER;
	}
	
	/**
	 * Randomly places a piece of type player on the board
	 * assumes it will infect
	 * @param player
	 */
	private void randomlyPlacePiece(eBoardObject player) {
		randomlyPlacePieces(1, player, false);
	}
	
	/**
	 * Start of game, initialize player
	 * @param numPieces
	 * @param player
	 */
	private void randomlyPlacePieces(int numPieces, eBoardObject player) {
		randomlyPlacePieces(numPieces, player, true);
	}
	
	/**
	 * If its the start of the game, allow placing anywhere except
	 * on an existing player, and don't infect
	 * 
	 * If not start of game, allow placing anywhere adjacent to
	 * one of your pieces, except a piece you already own, and
	 * infect adjacent cells
	 * @param numPieces
	 * @param player
	 * @param start
	 */
	private void randomlyPlacePieces(int numPieces, eBoardObject player, boolean start) {
		for (int i = 0; i < numPieces; ++i) {
			int x, y;
			eBoardObject obj;
			
			do {
				x = Globals.random.nextInt(Globals.BOARD_DIMENSIONS);
				y = Globals.random.nextInt(Globals.BOARD_DIMENSIONS);
				obj = boardData[x][y];
			}
			while ((start && !obj.equals(eBoardObject.EMPTY)) || 
				   (!start && Globals.ALLOW_MOVE_ON_ITSELF == false && obj.equals(player)) || 
				   (!start && moveManager.isAdjacent(x, y, boardData, player) == false));
			
			boardData[x][y] = player;
			
			if (!start) {
				moveManager.infect(x, y, boardData, player);
				lastMove = new Point(x, y);
			}
		}
	}	
	
	/**
	 * When playing as dumb player, checks which square was clicked
	 * @param mouseX
	 * @param mouseY
	 */
	public void clickedMove(int mouseX, int mouseY) {
		if (!currentPlayer.equals(eBoardObject.DUMBPLAYER)) {
			return;		// Not your turn
		}
		
		final int squareWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int squareHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		
		int whichX = mouseX / squareWidth;
		int whichY =  mouseY / squareHeight;
		
		if (moveManager.playMove(whichX, whichY, boardData, currentPlayer)) {
			currentPlayer = eBoardObject.SMARTPLAYER;
			lastMove = new Point(whichX, whichY);
			
			// Generate the next possible moves DEBUG
			moves = moveManager.createNextMoves(getCurrentState());
		}
	}

	@Override
	public void draw(PApplet g) {	
		BoardDrawer.draw(g, boardData, lastMove, 0, 0, Globals.BOARD_SIZE, Globals.BOARD_SIZE);
		
		int x = Globals.BOARD_SIZE, y = 0;
		int dim = Globals.BOARD_SIZE * 2 / 5;
		for (AINode node : moves) {
			VirusState state = (VirusState) node.state;
			BoardDrawer.draw(g, state.gameBoard, (Point) node.action, x, y, dim, dim);
			g.strokeWeight(8);
			g.line(x, y, x, y + dim);
			g.line(x, y + dim, x + dim, y + dim);
			x += dim;
			if (x >= (Globals.BOARD_SIZE * 3) - 5) {
				x = Globals.BOARD_SIZE;
				y += dim;
			}
		}
		if (gameOver) {
			g.fill(0, 170);
			g.rect(0, 0, g.width, g.height);
			
			g.fill(255);
			g.text("Game Over", g.width/2-35, g.height/2);
		}
	}
	
	private VirusState getCurrentState() {
		return new VirusState(currentPlayer, boardData, gameOver);
	}
}
