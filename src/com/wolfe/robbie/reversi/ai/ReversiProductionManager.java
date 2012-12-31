package com.wolfe.robbie.reversi.ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.ai.AINode;
import com.wolfe.robbie.common.ai.ProductionManager;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.reversi.Globals;
import com.wolfe.robbie.reversi.gameobjects.Piece;

public class ReversiProductionManager implements ProductionManager {

	@Override
	public List<AINode> createNextMoves(State iState) {
		if (iState.isDeadState) {
			return null;
		}
		
		List<AINode> moves = new LinkedList<AINode>();
		
		return moves;
	}
	
	public boolean isGameOver(Map<Point, Piece> potentialMoves) {
		return potentialMoves.isEmpty();
	}
	
	public void makePotentialMoves(Map<Point, Piece> potentialMoves, Piece[][] boardPieces, int currentPlayer) {
		for (Entry<Point, Piece> pair : potentialMoves.entrySet()) {
			pair.getValue().makeEmpty();
		}
		potentialMoves.clear();
		
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				if (boardPieces[i][j].getType() == currentPlayer) {
					List<Piece> sandwiches = findSandwichPieces(i, j, currentPlayer, boardPieces);
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
	private List<Piece> findSandwichPieces(int x, int y, int playerType, Piece[][] boardPieces) {
		List<Piece> sandwiches = new LinkedList<Piece>();
		int enemyType = Piece.getEnemyType(playerType);
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
	
	public void playMove(Piece move, Piece[][] boardPieces, int currentPlayer) {
		move.setType(currentPlayer);
		Point destination = new Point(move.getX(), move.getY());
		List<Piece> sandwiches = findSandwichPieces(destination.x, destination.y, currentPlayer, boardPieces);
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
}
