package com.wolfe.robbie.mill.gameobjects;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.mill.Globals;

import processing.core.PApplet;
import processing.core.PConstants;

public class Node implements GameObject {
	private static int MAX_NEIGHBOURS = 4;
	
	public Piece containedPiece;
	
	private int numNeighbours = 0;
	private Node[] neighbours = new Node[MAX_NEIGHBOURS];
	
	private int gridX, gridY;
	
	public Node(int gridX, int gridY) {
		this.gridX = gridX;
		this.gridY = gridY;
		containedPiece = null;
	}
	
	public Node(Node other) {
		this.gridX = other.gridX;
		this.gridY = other.gridY;
		this.numNeighbours = other.numNeighbours;
		//this.neighbours = 
		containedPiece = null;		// Don't copy contained piece
	}
	
	/**
	 * Two nodes are equal if their grid coordinates are equal
	 * and their contained object is equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Node)) {
			return false;
		}
		Node n = (Node) other;
		return gridX == n.gridX && gridY == n.gridY;// TODO && containedPiece.equals(obj); ??
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < numNeighbours; ++i) {
			Node n = neighbours[i];
			hash += n.gridX + n.gridY;
		}
		hash += gridX + gridY;
		return hash;
	}
	
	@Override
	public String toString() {
		return "[" + gridX + "," + gridY + "]";
	}

	public void addNeighbour(Node n) {
		neighbours[numNeighbours++] = n;
	}
	
	public Node[] getNeighbours() {
		return Arrays.copyOfRange(neighbours, 0, numNeighbours);
	}
	
	public static boolean areAligned(Node n1, Node n2) {
		return n1.gridX == n2.gridX || n1.gridY == n2.gridY;
	}
	
	/**
	 * Sees if the given node is one of its neighbours
	 * or one of its neighbours neighbours
	 * and there's no pieces in between
	 * @param other
	 * @return
	 */
	public boolean isTwoOrLessAwayWithoutObstacles(Node other) {
		for (int i = 0; i < numNeighbours; ++i) {
			// Ignore this neighbour if it already has something in it (can't go through it)
			if (neighbours[i].containedPiece != null) {
				continue;
			}
			if (neighbours[i].equals(other)) {
				return true;
			}
			for (int j = 0; j < neighbours[i].numNeighbours; ++j) {
				if (neighbours[i].neighbours[j].equals(other)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getXLocation() {
		return getLocation(gridX);
	}	

	public int getYLocation() {
		return getLocation(gridY);
	}
	
	private int getLocation(int coord) {
		final int gridSize = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		return (coord * gridSize) + gridSize / 2; 
	}
	
	public int getGridX() {
		return gridX;
	}
	
	public int getGridY() { 
		return gridY;
	}
	
	public Point getGridLocation() {
		return new Point(gridX, gridY);
	}
	
	/**
	 * For a given Node, return all the nodes (including itself)
	 * in its particular row.
	 * 
	 * List is in no particular order
	 * @return
	 */
	public List<Node> getRow() {
		return getRowOrColumn(true);
	}
	
	/**
	 * For a given Node, return all the nodes (including itself)
	 * in its particular row.
	 * 
	 * List is in no particular order
	 * @return
	 */
	public List<Node> getColumn() {
		return getRowOrColumn(false);
	}
	
	/**
	 * For a giveN Node, returns all the nodes (including itself)
	 * in its particular row or column
	 * 
	 * @param getRow if false get column
	 * @return
	 */
	private List<Node> getRowOrColumn(boolean getRow) {
		LinkedList<Node> result = new LinkedList<Node>();
		result.addFirst(this);
		
		for (int i = 0; i < numNeighbours; ++i) {
			Node node = neighbours[i];
			if (getRow) {
				// Neighbour has same row
				if (node.gridY == gridY) {
					result.addFirst(node);
				}
			}
			else {
				// Neighbour has same column
				if (node.gridX == gridX) {
					result.addFirst(node);
				}
			}
		}
		
		if (result.size() != 3) {
			if (result.size() != 2) {		// Either no neighbour in row or has more than 3 in a row/column
				System.err.println("ERROR this shouldn't happen");
				return null;
			}
			
			// This is at the edge of row/column, get neighbours' neighbour
			Node n = result.getFirst();
			
			for (int i = 0 ; i < n.numNeighbours; ++i) {
				Node node = n.neighbours[i];
				if (getRow) {
					// Neighbour has same row, as long as its not this piece
					if (node.gridY == gridY && node.gridX != gridX) {
						result.addFirst(node);
					}
				}
				else {
					// Neighbour has same column
					if (node.gridX == gridX && node.gridY != gridY) {
						result.addFirst(node);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns true if one of this Node's neighbours is free to be shifted to
	 * @return
	 */
	public boolean hasFreeNeighbour() {
		for (int i = 0; i < numNeighbours; ++i) {
			if (neighbours[i].containedPiece == null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all the neighbours that this Node has that don't
	 * contain anything
	 * @return
	 */
	public List<Node> getFreeNeighbours() {
		List<Node> result = new LinkedList<Node>();
		
		for (int i = 0; i < numNeighbours; ++i) {
			if (neighbours[i].containedPiece == null) {
				result.add(neighbours[i]);
			}
		} 
		
		return result;
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void draw(PApplet g) {
		final int gridSize = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		int objSize = gridSize / 8;
		final int margin = (gridSize - objSize) / 2;

		g.fill(0);
		g.ellipseMode(PConstants.CORNER);
		g.ellipse(gridX * gridSize + margin, gridY * gridSize + margin, objSize, objSize);
	}
	
	/**
	 * Piece will call this to draw itself
	 * @param g
	 * @param type
	 */
	public void drawPiece(PApplet g) {
		final int gridSize = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		int objSize = gridSize / 2;
		final int margin = (gridSize - objSize) / 2;
		if (containedPiece.type.equals(eBoardObject.DUMBPLAYER)) {
			g.fill(255);
		}
		else if (containedPiece.type.equals(eBoardObject.SMARTPLAYER)) {
			g.fill(0);
		}
		g.ellipse(gridX * gridSize + margin, gridY * gridSize + margin, objSize, objSize);
	}
	
	/**
	 * Draws the line between two points
	 * @param g
	 * @param n1
	 * @param n2
	 */
	public static void drawConnection(PApplet g, Node n1, Node n2) {
		final int gridSize = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int margin = gridSize / 2;
		g.line(n1.gridX * gridSize + margin, n1.gridY * gridSize + margin, n2.gridX * gridSize + margin, n2.gridY * gridSize + margin);
	}
}
