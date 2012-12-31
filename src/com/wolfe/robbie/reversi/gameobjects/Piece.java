package com.wolfe.robbie.reversi.gameobjects;

import processing.core.PApplet;
import processing.core.PConstants;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.reversi.Globals;

/**
 * Represents a piece on the gameboard
 * Has a location and color, and animates when switching to a different color
 * @author Robbie
 *
 */
public class Piece implements GameObject {
	// Player Types
	private static int EMPTY 		= -1;
	public static int DUMBPLAYER 	= 0;
	public static int SMARTPLAYER 	= 1;
	public static int POTENTIAL		= 2;
	
	// Graphics stuff
	private static int PieceLocationSize = 100;
	private static int PieceSize = (int) (PieceLocationSize * 0.9);
	private static int[] colors;
	private static String[] colorNames;
	
	// Location, based off of piece sizes and graphics locations
	private int gridX, gridY, squareX, squareY, locationX, locationY;
	
	private int color;
	private int playerType;
	
	// For animating
	private boolean converting = false;
	private int offset = 0;
	
	// All the Pieces share the same size and potential colors
	public static void init(int pieceLocationSize) {
		PieceLocationSize = pieceLocationSize;
		PieceSize = (int) (PieceLocationSize * 0.7);
		colors = new int[3];
		colorNames = new String[2];
		colors[DUMBPLAYER] 	= 255; 	colorNames[DUMBPLAYER] 	= "White";
		colors[SMARTPLAYER] = 0; 	colorNames[SMARTPLAYER] = "Black";
	}
	
	public static int getEnemyType(int type) {
		return (type == Piece.DUMBPLAYER ? Piece.SMARTPLAYER : Piece.DUMBPLAYER);
	}
	
	public static String getPlayerColor(int type) {
		return colorNames[type];
	}
	
	public Piece(int x, int y) {
		init(x, y);
		setType(EMPTY);
	}
	
	public Piece(int x, int y, int type) {
		init(x, y);
		setType(type);
	}
	
	public Piece(Piece other) {
		init(other.gridX, other.gridY);
		setType(other.playerType);
	}

	private void init(int x, int y) {
		gridX 		= x;
		gridY 		= y;
		squareX 	= gridX * PieceLocationSize;
		squareY 	= gridY * PieceLocationSize;
		locationX 	= squareX + (PieceLocationSize / 2);
		locationY 	= squareY + (PieceLocationSize / 2);
	}
	
	/**
	 * Two pieces are equal if they have the same location and type
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj instanceof Piece == false) {
			return false;
		}
		Piece other = (Piece) obj;
		return other.gridX == gridX && other.gridY == gridY && other.playerType == playerType;
	}
	
	public final int getX() {
		return gridX;
	}
	
	public final int getY() {
		return gridY;
	}
	
	public int getType() {
		return playerType;
	}
	
	public void makePotential(int type) {
		playerType = POTENTIAL;
		color = colors[type];
	}
	
	public void setType(int type) {
		playerType = type;
		if (type != EMPTY) {
			color = colors[type];
		}
	}
	
	public void makeEmpty() {
		playerType = EMPTY;
	}

	@Override
	public void update() {
		if (converting) {
			color += offset * 8;
			if (color >= 255 || color <= 0) {
				if (color > 255) {
					color = 255;
				}
				else if (color < 0) {
					color = 0;
				}
				offset = 0;
				converting = false;
			}
		}
	}
	
	public void convertPiece() {
		converting = true;
		playerType = getEnemyType(playerType);
		offset = (color == 255 ? -1 : 1);
	}

	@Override
	public void draw(PApplet g) {
		g.stroke(0);
		g.strokeWeight(1);
		g.fill(g.color(0,200,0));
		g.rect(squareX, squareY, PieceLocationSize, PieceLocationSize);
		
		if (playerType == EMPTY) {
			return;
		}
		g.ellipseMode(PConstants.CENTER);
		if (playerType == POTENTIAL) {
			g.fill(g.color(color, 80));
		}
		else {
			g.fill(g.color(color));
		}
		g.ellipse(locationX, locationY, PieceSize, PieceSize);
	}

}
