package com.wolfe.robbie.virus.gameobjects;

import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.virus.Globals;

import processing.core.PApplet;

public class BoardDrawer {
	public static void draw(PApplet g, eBoardObject[][] boardData, Point lastMove,
							int originX, int originY, int width, int height) {
		final int squareWidth = width / Globals.BOARD_DIMENSIONS;
		final int squareHeight = height / Globals.BOARD_DIMENSIONS;
				
		for (int i = 0; i < Globals.BOARD_DIMENSIONS; ++i) {
			for (int j = 0; j < Globals.BOARD_DIMENSIONS; ++j) {
				
				// Highlight last move
				if (lastMove != null && i == lastMove.x && j == lastMove.y) {
					g.stroke(255, 255, 0);
					g.strokeWeight(5);
				}
				else {
					g.stroke(0);
					g.strokeWeight(1);
				}
				
				eBoardObject obj = boardData[i][j];
				
				if (obj.equals(eBoardObject.EMPTY)) {
					g.fill(g.color(255));
				}
				else if (obj.equals(eBoardObject.SMARTPLAYER)) {
					g.fill(g.color(255,0,0));
				}
				else if (obj.equals(eBoardObject.DUMBPLAYER)) {
					g.fill(g.color(0,0,255));
				}
				
				final int xLocation = i*squareWidth + originX;
				final int yLocation = j*squareHeight + originY;
				g.rect(xLocation, yLocation, squareWidth, squareHeight);
			}
		}
		g.stroke(0);
	}
}
