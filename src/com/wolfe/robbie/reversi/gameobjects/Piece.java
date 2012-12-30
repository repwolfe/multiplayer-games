package com.wolfe.robbie.reversi.gameobjects;

import processing.core.PApplet;
import processing.core.PConstants;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.reversi.Globals;

public class Piece implements GameObject {
	private static int EMPTY 		= -1;
	public static int DUMBPLAYER 	= 0;
	public static int SMARTPLAYER 	= 1;
	private static int PieceLocationSize = 100;
	private static int PieceSize = (int) (PieceLocationSize * 0.9);
	private static int[] colors;
	
	private int squareX, squareY, locationX, locationY;
	private int color;
	private int playerType;
	
	public static void init(PApplet g, int pieceLocationSize) {
		PieceLocationSize = pieceLocationSize;
		PieceSize = (int) (PieceLocationSize * 0.7);
		colors = new int[2];
		if (Globals.random.nextBoolean()) {
			colors[DUMBPLAYER] = g.color(255);
			colors[SMARTPLAYER] = g.color(0);
		}
		else {
			colors[DUMBPLAYER] = g.color(0);
			colors[SMARTPLAYER] = g.color(255);
		}
	}
	
	public Piece(int x, int y) {
		init(x, y);
		playerType = EMPTY;
	}
	
	public Piece(int x, int y, int type) {
		init(x, y);
		playerType = type;
		color = colors[playerType];
	}
	
	private void init(int x, int y) {
		squareX = x * PieceLocationSize;
		squareY = y * PieceLocationSize;
		locationX = squareX + (PieceLocationSize / 2);
		locationY = squareY + (PieceLocationSize / 2);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
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
		g.fill(color);
		g.ellipse(locationX, locationY, PieceSize, PieceSize);
	}

}
