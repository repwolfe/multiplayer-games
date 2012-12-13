package com.wolfe.robbie.mill.gameobjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.wolfe.robbie.common.GameObject;
import com.wolfe.robbie.common.Point;
import com.wolfe.robbie.common.eBoardObject;
import com.wolfe.robbie.common.ai.MiniMax;
import com.wolfe.robbie.mill.Globals;
import com.wolfe.robbie.mill.ai.MillHeuristic;
import com.wolfe.robbie.mill.ai.MillMove;
import com.wolfe.robbie.mill.ai.MillProductionManager;
import com.wolfe.robbie.mill.ai.MillState;
import com.wolfe.robbie.mill.ai.MillHeuristic.Type;

import processing.core.PApplet;


public class Board implements GameObject {
	public enum GameStage { ADDING, SHIFTING };
	
	private HashMap<Point, Node> nodes;
	
	private Piece[] dumbPieces;
	private int[] dumbFirstLast;
	private Piece[] smartPieces;
	private int[] smartFirstLast;
	
	private boolean gameOver;
	
	private eBoardObject currentPlayer;
	private Piece[] currentPieces;
	private int[] currentFirstLast;
	private Piece[] otherPieces;
	private int[] otherFirstLast;
	
	private Piece selectedPiece;
	
	private GameStage currentStage;
	
	private MillProductionManager moveManager;
	private MiniMax dumbAI, smartAI;
	private MillHeuristic smartHeuristic;
	public Board() {
		initializeBoard();
	}
	
	private void initializeBoard() {
		dumbPieces = new Piece[Globals.MAX_PIECES];
		dumbFirstLast = new int[] { 0, 0 };
		smartPieces = new Piece[Globals.MAX_PIECES];
		smartFirstLast = new int[] { 0, 0 };
		
		currentPlayer = eBoardObject.DUMBPLAYER;
		currentPieces = dumbPieces;
		currentFirstLast = dumbFirstLast;
		otherPieces = smartPieces;
		otherFirstLast = smartFirstLast;
		
		if (Globals.SMART_PLAYER_GOES_FIRST) {
			switchPlayer();
		}
		
		gameOver = false;
		
		selectedPiece = null;
		
		currentStage = GameStage.ADDING;
		
		nodes = createNodes();
		
		moveManager = new MillProductionManager();
		smartHeuristic = new MillHeuristic(Type.SMART);
		dumbAI = new MiniMax(moveManager, new MillHeuristic(Type.DUMB), Globals.USE_ALPHA_BETA_PRUNING, Globals.PLY_AMOUNT);
		smartAI = new MiniMax(moveManager, smartHeuristic, Globals.USE_ALPHA_BETA_PRUNING, Globals.PLY_AMOUNT);
	}
	
	public static HashMap<Point, Node> createNodes() {
		HashMap<Point, Node> theNodes = new HashMap<Point, Node>();
		
		// Create nodes
		Node n00 = new Node(0,0);	theNodes.put(new Point(0,0), n00);
		Node n30 = new Node(3,0);	theNodes.put(new Point(3,0), n30);
		Node n60 = new Node(6,0);	theNodes.put(new Point(6,0), n60);
		Node n11 = new Node(1,1);	theNodes.put(new Point(1,1), n11);
		Node n31 = new Node(3,1);	theNodes.put(new Point(3,1), n31);
		Node n51 = new Node(5,1);	theNodes.put(new Point(5,1), n51);
		Node n22 = new Node(2,2);	theNodes.put(new Point(2,2), n22);
		Node n32 = new Node(3,2);	theNodes.put(new Point(3,2), n32);
		Node n42 = new Node(4,2);	theNodes.put(new Point(4,2), n42);
		Node n03 = new Node(0,3);	theNodes.put(new Point(0,3), n03);
		Node n13 = new Node(1,3);	theNodes.put(new Point(1,3), n13);
		Node n23 = new Node(2,3);	theNodes.put(new Point(2,3), n23);
		Node n43 = new Node(4,3);	theNodes.put(new Point(4,3), n43);
		Node n53 = new Node(5,3);	theNodes.put(new Point(5,3), n53);
		Node n63 = new Node(6,3);	theNodes.put(new Point(6,3), n63);
		Node n24 = new Node(2,4);	theNodes.put(new Point(2,4), n24);
		Node n34 = new Node(3,4);	theNodes.put(new Point(3,4), n34);
		Node n44 = new Node(4,4);	theNodes.put(new Point(4,4), n44);
		Node n15 = new Node(1,5);	theNodes.put(new Point(1,5), n15);
		Node n35 = new Node(3,5);	theNodes.put(new Point(3,5), n35);
		Node n55 = new Node(5,5);	theNodes.put(new Point(5,5), n55);
		Node n06 = new Node(0,6);	theNodes.put(new Point(0,6), n06);
		Node n36 = new Node(3,6);	theNodes.put(new Point(3,6), n36);
		Node n66 = new Node(6,6);	theNodes.put(new Point(6,6), n66);
		
		// Create connections
		makeNeighbours(n00, n30); makeNeighbours(n00, n03);
		makeNeighbours(n30, n31); makeNeighbours(n30, n60);
		makeNeighbours(n60, n63);
		makeNeighbours(n11, n31); makeNeighbours(n11, n13);
		makeNeighbours(n31, n32); makeNeighbours(n31, n51);
		makeNeighbours(n51, n53);
		makeNeighbours(n22, n32); makeNeighbours(n22, n23);
		makeNeighbours(n32, n42);
		makeNeighbours(n42, n43);
		makeNeighbours(n03, n13);
		makeNeighbours(n03, n06);
		makeNeighbours(n13, n23); makeNeighbours(n13, n15);
		makeNeighbours(n23, n24);
		makeNeighbours(n43, n53); makeNeighbours(n43, n44);
		makeNeighbours(n53, n63); makeNeighbours(n53, n55);
		makeNeighbours(n63, n66);
		makeNeighbours(n24, n34);
		makeNeighbours(n34, n44); makeNeighbours(n34, n35);
		makeNeighbours(n15, n35);
		makeNeighbours(n35, n55); makeNeighbours(n35, n36);
		makeNeighbours(n06, n36);
		makeNeighbours(n36, n66);
		
		return theNodes;
	}
	
	private static void makeNeighbours(Node n1, Node n2) {
		n1.addNeighbour(n2);
		n2.addNeighbour(n1);
	}
	
	private void switchPlayer() {
		otherPieces = currentPieces;
		otherFirstLast = currentFirstLast;
		
		if (currentPlayer.equals(eBoardObject.DUMBPLAYER)) {
			currentPlayer = eBoardObject.SMARTPLAYER;
			currentPieces = smartPieces;
			currentFirstLast = smartFirstLast;
		}
		else if (currentPlayer.equals(eBoardObject.SMARTPLAYER)) {
			currentPlayer = eBoardObject.DUMBPLAYER;
			currentPieces = dumbPieces;
			currentFirstLast = dumbFirstLast;
		}
	}
	
	private Node getNode(int x, int y) {
		return nodes.get(new Point(x, y));
	}

	private void checkIfShiftingStage() {
		if (moveManager.shouldSwitchToNextStage(currentStage, currentFirstLast, otherFirstLast)) {
			currentStage = GameStage.SHIFTING;
		}
	}

	@Override
	public void update() {
		if (!gameOver && !checkIsGameOver()) {
			// Dumb players turn and you don't make its moves for it
			if (currentPlayer.equals(eBoardObject.DUMBPLAYER) && !Globals.PLAY_AS_DUMB_PLAYER) {
				dumbPlayersMove();
			}
			else if (currentPlayer.equals(eBoardObject.SMARTPLAYER)) {
				smartPlayersMove();
			}
		}
		
		if (Globals.DEBUG) {
			System.out.println("---------------------------------------");
			smartHeuristic.countMillsAndPotentialMills(otherPieces, otherFirstLast, eBoardObject.nextPlayer(currentPlayer), true);
			smartHeuristic.countMillsAndPotentialMills(currentPieces, currentFirstLast, currentPlayer, true);
			System.out.println("---------------------------------------");
		}
	}
	
	/**
	 * Checks if no legal moves left for a player or if a player runs out of pieces
	 * @return
	 */
	private boolean checkIsGameOver() {
		gameOver = moveManager.checkIsGameOver(currentStage, dumbFirstLast, smartFirstLast, dumbPieces, smartPieces);
		
		return gameOver;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}

	public void clickedMove(int mouseX, int mouseY) {
		if (!currentPlayer.equals(eBoardObject.DUMBPLAYER)) {
			return;
		}
		final int squareWidth = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		final int squareHeight = Globals.BOARD_SIZE / Globals.BOARD_DIMENSIONS;
		
		int whichX = mouseX / squareWidth;
		int whichY =  mouseY / squareHeight;
		
		Node n = getNode(whichX, whichY);
		
		// Clicked on a non existent node
		if (n == null) {
			return;
		}
		
		if (currentStage.equals(GameStage.ADDING) && currentFirstLast[Globals.LAST] != Globals.MAX_PIECES) {
			if (n.containedPiece == null) {
				moveManager.addNewPiece(n, currentPlayer, currentPieces, currentFirstLast, otherPieces, otherFirstLast);				
				switchPlayer();
			}
			checkIfShiftingStage();
		}
		else if (currentStage.equals(GameStage.SHIFTING)) {
			// Nothing selected and clicked on our node
			if (selectedPiece == null) {
				if (n.containedPiece != null && n.containedPiece.type.equals(currentPlayer)) {
					n.containedPiece.selected = true;
					selectedPiece = n.containedPiece;
				}
			}
			// Attempt to shift selected piece
			else {
				if (moveManager.shiftPiece(selectedPiece, n, otherPieces, otherFirstLast)) {
					switchPlayer();
				}
				selectedPiece.selected = false;
				selectedPiece = null;
			}
		}
	}
	
	private void dumbPlayersMove() {
		if (Globals.DUMB_PLAYER_USES_AI) {
			MillMove nextMove = (MillMove) dumbAI.getNextMove(getCurrentState());
			executeAIMove(nextMove);
		}
		else {
			randomMove();
		}
		switchPlayer();
	}
	
	private void smartPlayersMove() {
		if (Globals.SMART_PLAYER_USES_AI) {
			MillMove nextMove = (MillMove) smartAI.getNextMove(getCurrentState());
			executeAIMove(nextMove);
		}
		else {
			randomMove();
		}
		switchPlayer();
	}
	
	private void executeAIMove(MillMove move) {
		if (move.from != null) {
			move.fromPiece = nodes.get(move.from).containedPiece;
		}
		move.toNode = nodes.get(move.to);
		moveManager.playMove(move, currentStage, currentPlayer, currentPieces, currentFirstLast, otherPieces, otherFirstLast);
		checkIfShiftingStage();
	}
	
	private void randomMove() {
		Node n = null;
		if (currentStage.equals(GameStage.ADDING) && currentFirstLast[Globals.LAST] != Globals.MAX_PIECES) {
			do {
				n = getNode(Globals.random.nextInt(Globals.BOARD_DIMENSIONS), Globals.random.nextInt(Globals.BOARD_DIMENSIONS));
			}
			while (n == null || n.containedPiece != null);
			
			Piece p = new Piece(n, currentPlayer, currentFirstLast[Globals.LAST]);
			currentPieces[currentFirstLast[Globals.LAST]++] = p;
			
			checkIfShiftingStage();
		}
		else if (currentStage.equals(GameStage.SHIFTING)) {
			boolean shifted = false;
			do {
				n = getNode(Globals.random.nextInt(Globals.BOARD_DIMENSIONS), Globals.random.nextInt(Globals.BOARD_DIMENSIONS));
				Node to = null;
				if (n != null && n.containedPiece != null && n.containedPiece.type.equals(currentPlayer)) {
					to = getNode(Globals.random.nextInt(Globals.BOARD_DIMENSIONS), Globals.random.nextInt(Globals.BOARD_DIMENSIONS));
					shifted = n.containedPiece.shiftPiece(to);
				}
				n = to;
			}
			while (!shifted);
		}
		
		moveManager.checkForMill(n, otherPieces, otherFirstLast);
	}

	@Override
	public void draw(PApplet g) {
		if (gameOver) {
			g.fill(0, 170);
			g.rect(0, 0, g.width, g.height);
			
			g.fill(255);
			g.text("Game Over", g.width/2-35, g.height/2);
		}
		HashSet<Node> nodesDrawn = new HashSet<Node>();
		final Set<Entry<Point, Node>> entries = nodes.entrySet();
		for (Entry<Point, Node> entry : entries) {
			Node n = entry.getValue();
			if (!nodesDrawn.contains(n)) {
				n.draw(g);
				Node[] neighbours = n.getNeighbours();
				for (Node neighbour : neighbours) {
					Node.drawConnection(g, n, neighbour);
					neighbour.draw(g);
					//nodesDrawn.add(neighbour);
				}
				nodesDrawn.add(n);
			}			
		}
		
		for (int i = dumbFirstLast[Globals.FIRST]; i < dumbFirstLast[Globals.LAST]; ++i) {
			dumbPieces[i].draw(g);
		}
		
		for (int i = smartFirstLast[Globals.FIRST]; i < smartFirstLast[Globals.LAST]; ++i) {
			smartPieces[i].draw(g);
		}
	}
	
	private MillState getCurrentState() {
		return new MillState(currentPlayer,
							 currentPieces, otherPieces, 
							 currentFirstLast, otherFirstLast, 
							 currentStage, gameOver);
	}
}
