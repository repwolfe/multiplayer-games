package com.wolfe.robbie.common.ai;

public interface Heuristic {
	int getCurrentHeuristic(State state);
	int getOpponentHeuristic(State state);
}
