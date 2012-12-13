package com.wolfe.robbie.common.ai;

public abstract class State {
	public int value;
	public boolean isDeadState;
	
	public State() {
		value = 0;
		isDeadState = false;
	}
}
