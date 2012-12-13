package com.wolfe.robbie.mill.ai;

import java.util.Collection;
import java.util.HashMap;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.State;
import com.wolfe.robbie.mill.Globals;
import com.wolfe.robbie.mill.gameobjects.Board;
import com.wolfe.robbie.mill.gameobjects.Node;
import com.wolfe.robbie.mill.gameobjects.Piece;
import com.wolfe.robbie.mill.gameobjects.Board.GameStage;


public class MillState extends State {
	// Used to copy Pieces, creates a fresh map of all the nodes
	private HashMap<Point, Node> nodeMap = Board.createNodes();
	
	public Piece[] currentPieces, otherPieces;
	public int[] currentFirstLast, otherFirstLast;
	public eBoardObject currentPlayer;
	public GameStage currentStage;
	
	public MillState(eBoardObject currentPlayer,
					 Piece[] currentPieces, Piece[] otherPieces,
					 int[] currentFirstLast, int[] otherFirstLast,
					 GameStage currentState, boolean isDeadState) {
		
		this.currentPlayer = currentPlayer;
		this.currentStage = currentState;
		this.isDeadState = isDeadState;

		copyArrays(currentPieces, otherPieces, currentFirstLast, otherFirstLast);
	}
	
	public MillState(MillState other) {
		this.currentStage = other.currentStage;
		this.currentPlayer = other.currentPlayer;
		this.isDeadState = other.isDeadState;	

		copyArrays(other.currentPieces, other.otherPieces, other.currentFirstLast, other.otherFirstLast);
	}
	
	private void copyArrays(Piece[] currentPieces, Piece[] otherPieces,
			 				int[] currentFirstLast, int[] otherFirstLast) {
		this.currentPieces = new Piece[Globals.MAX_PIECES];
		this.otherPieces = new Piece[Globals.MAX_PIECES];
		this.currentFirstLast = new int[] { currentFirstLast[Globals.FIRST], currentFirstLast[Globals.LAST] };
		this.otherFirstLast = new int[] { otherFirstLast[Globals.FIRST], otherFirstLast[Globals.LAST] };
		
		for (int i = currentFirstLast[Globals.FIRST]; i < currentFirstLast[Globals.LAST]; ++i) {
			this.currentPieces[i] = copyPiece(currentPieces[i]);
		}
		
		for (int i = otherFirstLast[Globals.FIRST]; i < otherFirstLast[Globals.LAST]; ++i) {
			this.otherPieces[i] = copyPiece(otherPieces[i]);
		}
	}
	
	/**
	 * Takes a piece and copies its information over, then makes the copy
	 * have the same location as the original, except its a different instance of Node
	 * @param other
	 * @return
	 */
	private Piece copyPiece(Piece other) {
		Piece copy = new Piece(other);
		Node otherLocation = other.getLocation();
		
		copy.setLocation(nodeMap.get(new Point(otherLocation.getGridX(), otherLocation.getGridY())));
		
		return copy;
	}
	
	public Collection<Node> getNodes() {
		return nodeMap.values();
	}
	
	public Node getNode(Point p) {
		return nodeMap.get(p);
	}
	
	public void swapPlayer() {
		Piece[] temp1 = otherPieces;
		int[] temp2 = otherFirstLast;
		
		otherPieces = currentPieces;
		otherFirstLast = currentFirstLast;
		
		currentPieces = temp1;
		currentFirstLast = temp2;
		
		currentPlayer = eBoardObject.nextPlayer(currentPlayer);
	}
	
	@Override
	public int hashCode() {
		return currentPieces.hashCode() + otherPieces.hashCode() + currentFirstLast.hashCode() + otherFirstLast.hashCode() + currentPlayer.hashCode() + currentStage.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof MillState)) {
			return false;
		}
		
		MillState state = (MillState) o;
		
		if (!currentStage.equals(state.currentStage) || !currentPlayer.equals(state.currentPlayer)) {
			return false;
		}
		
		if (currentFirstLast[Globals.FIRST] != state.currentFirstLast[Globals.FIRST] ||
			currentFirstLast[Globals.LAST]!= state.currentFirstLast[Globals.LAST]) {
			return false;
		}
		
		if (otherFirstLast[Globals.FIRST] != state.otherFirstLast[Globals.FIRST] ||
			otherFirstLast[Globals.LAST]!= state.otherFirstLast[Globals.LAST]) {
			return false;
		}
		
		for (int i = currentFirstLast[Globals.FIRST]; i < currentFirstLast[Globals.LAST]; ++i) {
			if (!currentPieces[i].equals(state.currentPieces[i])) {
				return false;
			}
		}
		
		for (int i = currentFirstLast[Globals.FIRST]; i < currentFirstLast[Globals.LAST]; ++i) {
			if (!otherPieces[i].equals(state.otherPieces[i])) {
				return false;
			}
		}
		
		return true;
	}
}
