package com.wolfe.robbie.virus;

import java.util.ArrayList;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.virus.gameobjects.Board;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class Drawer extends PApplet {
	private ArrayList<GameObject> gameObjects;
	
	private Board board;
	
	boolean gameOver = false;
	
	@Override
	public void setup() {
		final int width = Globals.DEBUG ? Globals.BOARD_SIZE * 3 : Globals.BOARD_SIZE;
		final int height = Globals.DEBUG ? Globals.BOARD_SIZE * 2 : Globals.BOARD_SIZE;
		size(width, height);

		startGame();
	}
	
	private void startGame() {
		gameObjects = new ArrayList<GameObject>();
		
		board = new Board();
		gameObjects.add(board);
	}

	@Override
	public void draw() {
		if (gameOver == false) {
			// Draw components
			for (GameObject g: gameObjects) {
				g.draw(this);
			}
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
