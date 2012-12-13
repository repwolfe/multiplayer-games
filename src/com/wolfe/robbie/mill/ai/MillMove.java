package com.wolfe.robbie.mill.ai;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.Action;
import com.wolfe.robbie.mill.gameobjects.Node;
import com.wolfe.robbie.mill.gameobjects.Piece;

/**
 * Represents a move from a Piece to a different Node (or a new node with no original Piece)
 * @author Robbie
 *
 */
public class MillMove extends Action {
	public Point from;
	public Piece fromPiece;		// Should be set later
	public Point to;
	public Node toNode;			// Should be set later
	public eBoardObject player;
	
	public MillMove(Point from, Point to, eBoardObject player) {
		this.from = from;
		this.to = to;
		this.player = player;
	}
	
	public String toString() {
		return "[From: " + from + " | To: " + to + "]";
	}
}
