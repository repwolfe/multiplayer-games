package com.wolfe.robbie.common.ai;

public class AINode {
	public enum Type { MAX, MIN };
	
	public State state;
	public Action action;
	public Type type;
	public int value = 0;
	
	public AINode(State state, Action action) {
		this.state = state;
		this.action = action;
	}
	
	@Override
	public int hashCode() {
		return state.hashCode() + action.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AINode)) {
			return false;
		}
		
		AINode node = (AINode) o;
		return node.state.equals(state) && node.action.equals(action);
	}
	
	@Override
	public String toString() {
		return action.toString() + "| Value: " + value;
	}
}
