package com.wolfe.robbie.common.ai;

import java.util.LinkedList;

public interface ProductionManager {
	LinkedList<AINode> createNextMoves(State initialState);
}
