package com.wolfe.robbie.virus;

import java.util.Random;

public class Globals {
	public static final Random random = new Random();
	
	public static final int BOARD_SIZE = 300;
	public static final int BOARD_DIMENSIONS = 5;
	
	public static final boolean DEBUG = false;

	public static final boolean USE_ALPHA_BETA_PRUNING = true;
	
	// If true, you are playing against computer. Otherwise it's smart computer vs dumb computer
	public static boolean PLAY_AS_DUMB_PLAYER = false;
	
	public static boolean SMART_PLAYER_USES_AI = true;
	
	public static boolean ALLOW_MOVE_ON_ITSELF = true;
	
	public static int PLY_AMOUNT = 4;
}