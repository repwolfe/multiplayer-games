package com.wolfe.robbie.common.ai;

import java.util.List;

public interface ProductionManager {
	List<AINode> createNextMoves(State initialState);
}
