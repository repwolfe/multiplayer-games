package com.wolfe.robbie.reversi;

import java.util.ArrayList;

import com.smenu.SMenu;
import com.smenu.actn;
import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.reversi.gameobjects.Board;


import processing.core.PApplet;

/**
 * The applet, doesn't do much except tell the board to draw and handle keypresses/mouseclicks
 * @author Robbie
 *
 */
@SuppressWarnings("serial")
public class Drawer extends PApplet {
	private ArrayList<GameObject> gameObjects;
	private Board board;
	
	@Override
	public void setup() {
		size(Globals.BOARD_SIZE, Globals.BOARD_SIZE);

		SMenu m = new SMenu(this);
		m.SMSubM("Difficulty");
			m.SMItem("Easy (Fast)", new actn() {			
				@Override
				public void a() {
					Globals.PLY_AMOUNT = 4;
					startGame();
				}
			});
			
			m.SMItem("Medium", new actn() {			
				@Override
				public void a() {
					Globals.PLY_AMOUNT = 6;
					startGame();
				}
			});
			
			m.SMItem("Hard (Slow)", new actn() {			
				@Override
				public void a() {
					Globals.PLY_AMOUNT = 8;
					startGame();
				}
			});
			m.SMEnd();
		m.SMEnd();
		
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
