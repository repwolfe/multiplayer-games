package com.wolfe.robbie.mill.ai;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.AINode;
import com.wolfe.robbie.common.ai.ProductionManager;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.mill.Globals;
import com.wolfe.robbie.mill.gameobjects.Node;
import com.wolfe.robbie.mill.gameobjects.Piece;
import com.wolfe.robbie.mill.gameobjects.Board.GameStage;


public class MillProductionManager implements ProductionManager {	
	@Override
	public LinkedList<AINode> createNextMoves(State iState) {
		if (iState.isDeadState) {
			return null;	// TODO
		}
		
		MillState initialState = (MillState) iState;
		LinkedList<AINode> moves = new LinkedList<AINode>();
		
		if (initialState.currentStage.equals(GameStage.ADDING)) {
			Collection<Node> nodes = initialState.getNodes();
			for (Node node : nodes) {
				if (node.containedPiece == null) {
					// Empty spot, can place here
					moves.add(createMove(initialState, null, node));
				}
			}
		}
		else if (initialState.currentStage.equals(GameStage.SHIFTING)) {
			for (int i = initialState.currentFirstLast[Globals.FIRST]; i < initialState.currentFirstLast[Globals.LAST]; ++i) {
				// For each piece on the board, get all the possible spots we can move to
				Piece currentPiece = initialState.currentPieces[i];
				
				List<Node> possibleMoves = currentPiece.getFreeNeighbours();
				
				for (Node node : possibleMoves) {
					// Empty spot, can shift it here
					moves.add(createMove(initialState, currentPiece, node));
				}
			}
		}
		
		return moves;
	}
	
	/**
	 * From a given State, create a copy and execute a move using currentPiece and n
	 * 
	 * Has to copy both the State as well as the move arguments since they belong to the original
	 * state
	 * @param initialState
	 * @param currentPiece
	 * @param n
	 * @return
	 */
	private AINode createMove(MillState initialState, Piece currentPiece, Node n) {
		MillState newState = new MillState(initialState);
		
		Point fromLocation = null;
		Piece fromPiece = null;
		if (currentPiece != null) {
			fromLocation = currentPiece.getLocation().getGridLocation();
			
			// Copy 'from' argument to have same node location as copied state
			fromPiece = new Piece(currentPiece);
			fromPiece.setLocation(newState.getNode(fromLocation));
		}

		// Copy 'to' argument to have same node as copied state
		Point toLocation = n.getGridLocation();
		Node toNode = newState.getNode(toLocation);
		
		MillMove move = new MillMove(fromLocation, toLocation, newState.currentPlayer);

		// Execute move
		if (initialState.currentStage.equals(GameStage.ADDING)) {
			addNewPiece(toNode, newState.currentPlayer, 
						newState.currentPieces, newState.currentFirstLast, 
						newState.otherPieces, newState.otherFirstLast);
		}
		else if (initialState.currentStage.equals(GameStage.SHIFTING)) {
			shiftPiece(fromPiece, toNode, newState.otherPieces, newState.otherFirstLast);
		}
		
		newState.swapPlayer();
		
		// Check if next stage
		if (shouldSwitchToNextStage(newState.currentStage, newState.currentFirstLast, newState.otherFirstLast)) {
			newState.currentStage = GameStage.SHIFTING;
		}
		
		// Check if game over
		newState.isDeadState = checkIsGameOver(newState.currentStage, 
											   newState.currentFirstLast, newState.otherFirstLast, 
											   newState.currentPieces, newState.otherPieces);
		
		AINode aiNode = new AINode(newState, move);
		return aiNode;
	}
	
	/**
	 * Executes a move with the given MillMove object
	 * @param move
	 * @param currentStage
	 */
	public void playMove(MillMove move, GameStage currentStage, eBoardObject currentPlayer,
						 Piece[] currentPieces, int[] currentFirstLast,
						 Piece[] otherPieces, int[] otherFirstLast) {
		if (currentStage.equals(GameStage.ADDING)) {
			addNewPiece(move.toNode, currentPlayer,
						currentPieces, currentFirstLast,
						otherPieces, otherFirstLast);
		}
		else if (currentStage.equals(GameStage.SHIFTING)) {
			shiftPiece(move.fromPiece, move.toNode, otherPieces, otherFirstLast);
		}
	}
	
	/**
	 * Checks if the game is over based off the given state information
	 * @param currentStage
	 * @param currentFirstLast
	 * @param otherFirstLast
	 * @param currentPieces
	 * @param otherPieces
	 * @return
	 */
	public boolean checkIsGameOver(GameStage currentStage, int[] currentFirstLast, int[] otherFirstLast,
								   Piece[] currentPieces, Piece[] otherPieces) {
		boolean gameOver = false;
		
		if (currentStage.equals(GameStage.ADDING)) {
			return false;		// Can't lose in this stage
		}
		
		// No pieces left after placement is done
		if (currentFirstLast[Globals.FIRST] == currentFirstLast[Globals.LAST] ||
			otherFirstLast[Globals.FIRST] == otherFirstLast[Globals.LAST]) {
			return true;
		}

		// If the above wasn't true
		boolean currentCanMove = false;
		for (int i = currentFirstLast[Globals.FIRST]; i < currentFirstLast[Globals.LAST]; ++i) {
			if (currentPieces[i].hasMoveAvailable()) {
				currentCanMove = true;
				break;
			}
		}
		if (!currentCanMove) {
			gameOver = true;
		}
		else {
			boolean otherCanMove = false;
			for (int i = otherFirstLast[Globals.FIRST]; i < otherFirstLast[Globals.LAST]; ++i) {
				if (otherPieces[i].hasMoveAvailable()) {
					otherCanMove = true;
					break;
				}
			}
			
			if (!otherCanMove) {
				gameOver = true;
			}
		}
		
		return gameOver;
	}
	
	/**
	 * Places a new piece at the given node.
	 * Checks for a mill
	 * @param n
	 * @param currentPlayer
	 * @param currentPieces
	 * @param currentFirstLast
	 * @param otherPieces
	 * @param otherFirstLast
	 */
	public void addNewPiece(Node n, eBoardObject currentPlayer, 
							Piece[] currentPieces, int[] currentFirstLast,
							Piece[] otherPieces, int[] otherFirstLast) {
		Piece p = new Piece(n, currentPlayer, currentFirstLast[Globals.LAST]);
		currentPieces[currentFirstLast[Globals.LAST]++] = p;
		checkForMill(n, otherPieces, otherFirstLast);
	}
	
	/**
	 * Tries to shift the selected piece to the Node n
	 * If successful, returns true
	 * else false
	 * 
	 * Checks for a mill that was formed
	 * @param selectedPiece
	 * @param n
	 * @param otherPieces
	 * @param otherFirstLast
	 * @return
	 */
	public boolean shiftPiece(Piece selectedPiece, Node n, Piece[] otherPieces, int[] otherFirstLast) {
		if (selectedPiece.shiftPiece(n)) {
			checkForMill(n, otherPieces, otherFirstLast);
			return true;
		}
		return false;
	}
	
	/**
	 * If the given newPosition forms a mill, remove the earliest created piece
	 * from opponentsPieces
	 * @param newPosition
	 * @param opponentsPieces
	 * @param opponentsFirstLast
	 */
	public void checkForMill(Node newPosition, Piece[] otherPieces, int[] otherFirstLast) {
		// If condition here to see if a mill exists
		List<Node> row = newPosition.getRow();
		List<Node> column = newPosition.getColumn();
		
		eBoardObject piece = newPosition.containedPiece.type;
		
		boolean rowMill = true;
		boolean columnMill = true;
		
		// See if all in row or column the same as newPosition 
		for (Node rowNode : row) {
			if (rowNode.containedPiece == null || !rowNode.containedPiece.type.equals(piece)) {
				rowMill = false;
				break;
			}
		}
		
		for (Node columnNode : column) {
			if (columnNode.containedPiece == null || !columnNode.containedPiece.type.equals(piece)) {
				columnMill = false;
				break;
			}
		}
		
		if (rowMill || columnMill) {
			// If a mill was formed, remove the earliest created piece
			otherPieces[otherFirstLast[Globals.FIRST]++].removePiece();
		}
	}
	
	public boolean shouldSwitchToNextStage(GameStage currentStage, int[] currentFirstLast, int[] otherFirstLast) {
		if (currentStage.equals(GameStage.ADDING)) {
			if (currentFirstLast[Globals.LAST] == Globals.MAX_PIECES && otherFirstLast[Globals.LAST] == Globals.MAX_PIECES) {
				return true;
			}
		}
		return false;
	}
}
