package com.wolfe.robbie.common;

public enum eBoardObject {
	EMPTY, DUMBPLAYER, SMARTPLAYER;
	
	public static eBoardObject nextPlayer(eBoardObject currentPlayer) {
		if (currentPlayer.equals(DUMBPLAYER)) {
			return SMARTPLAYER;
		}
		return DUMBPLAYER;
	}
}
