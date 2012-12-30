package com.wolfe.robbie.reversi;

import java.util.ArrayList;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.reversi.gameobjects.Board;


import processing.core.PApplet;

@SuppressWarnings("serial")
public class Drawer extends PApplet {
	private ArrayList<GameObject> gameObjects;
	private Board board;
	
	@Override
	public void setup() {
		size(Globals.BOARD_SIZE, Globals.BOARD_SIZE);

		startGame();
	}
	
	private void startGame() {
		gameObjects = new ArrayList<GameObject>();
		board = new Board();
		
		gameObjects.add(board);
	}
	
	@Override
	public void draw() {
		// Draw components
		for (GameObject g: gameObjects) {
			g.draw(this);
		}
	}
	
	@Override
	public void mouseClicked() {
		if (board.isGameOver()) {
			startGame();
		}
		// Playing as computer,
		else if (Globals.PLAY_AS_DUMB_PLAYER) {
			board.clickedMove(mouseX, mouseY);
		}
	}
	
	@Override
	public void keyPressed() {
		board.update();
	}
}
