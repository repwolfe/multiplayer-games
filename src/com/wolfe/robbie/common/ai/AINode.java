package com.wolfe.robbie.common.ai;

/**
 * Represents a Node in the AI Algorithm tree
 * Contains a State and the move used to get to this State
 * Also contains the heuristic value for this State
 * @author Robbie
 *
 */
public class AINode {	
	public State state;
	public Action action;
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
