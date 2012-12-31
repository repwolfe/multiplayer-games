package com.wolfe.robbie.reversi;

import java.util.Random;

public class Globals {
	public static final Random random = new Random();
	
	public static final int BOARD_SIZE = 500;
	public static final int BOARD_DIMENSIONS = 8;
	
	public static final boolean DEBUG = true;
	
	// If true, you are playing against computer. Otherwise you are computer and player plays randomly
	public static final boolean PLAY_AS_DUMB_PLAYER = true;
	
	public static int PLY_AMOUNT = 4;
	public static final boolean USE_ALPHA_BETA_PRUNING = true;
	public static final boolean DUMB_PLAYER_USES_AI = true;
	public static final boolean SMART_PLAYER_USES_AI = true;
}
