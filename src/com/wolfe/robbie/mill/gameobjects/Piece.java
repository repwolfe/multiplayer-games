package com.wolfe.robbie.mill.gameobjects;

import java.util.List;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.eBoardObject;

import processing.core.PApplet;

public class Piece implements GameObject {
	private Node location;
	private int creationIndex;
	
	public eBoardObject type;
	public boolean selected = false;
	
	public Piece(Node loc, eBoardObject type, int creationIndex) {
		setLocation(loc);
		this.type = type;
		this.creationIndex = creationIndex;
	}
	
	public Piece(Piece other) {
		//Node copy = new Node(other.location);		// THIS WONT WORK!!!!!!!!!!!!!!!!
		//setLocation(copy);
		this.type = other.type;
		this.creationIndex = other.creationIndex;
	}
	
	@Override
	public String toString() {
		return location.toString();
	}
	
	@Override 
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Piece)) {
			return false;
		}
		Piece other = (Piece) o;
		
		if (other.creationIndex != creationIndex ||
			!other.type.equals(type)) {
			return false;
		}
		
		return location.equals(other.location);
	}
	
	public void setLocation(Node loc) {
		if (location != null) {
			// Make old location empty
			location.containedPiece = null;
		}
		location = loc;
		
		if (loc != null) {
			loc.containedPiece = this;
		}
	}
	
	public Node getLocation() {
		return location;
	}
	
	/**
	 * Shifts this Piece to the given Node
	 * @param to
	 * @return
	 */
	public boolean shiftPiece(Node to) {
		// Shifting to an existing piece or a non existant node
		if (to == null || to.containedPiece != null) {
			return false;
		}
		
		// Moving to itself
		if (location.equals(to)) {
			return false;
		}
		
		// Not shifting horizontally or vertically
		if (!Node.areAligned(location, to)) {
			return false;
		}
		
		// Not a neighbour
		if (!location.isTwoOrLessAwayWithoutObstacles(to)) {
			return false;
		}
		
		// Legal shift
		setLocation(to);
		return true;
	}
	
	/**
	 * Removes a piece from the board
	 */
	public void removePiece() {
		setLocation(null);
	}
	
	/**
	 * Returns rue if this piece has a possible move
	 * @return
	 */
	public boolean hasMoveAvailable() {
		return location.hasFreeNeighbour();
	}
	
	public List<Node> getFreeNeighbours() {
		return location.getFreeNeighbours();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(PApplet g) {
		if (selected) {
			g.stroke(255, 255, 0);
			g.strokeWeight(2);
		}
		
		location.drawPiece(g);

		// Reset
		g.stroke(0);
		g.strokeWeight(1);
		
		//if (Globals.DEBUG) {
			g.fill(255,0,0);
			g.text(String.valueOf(creationIndex), location.getXLocation(), location.getYLocation());
		//}
	}
}
