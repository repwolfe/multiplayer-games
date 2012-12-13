package com.wolfe.robbie.common;

import com.wolfe.robbie.common.ai.Action;

public class Point extends Action {
	public int x, y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point)) {
			return false;
		}
		Point other = (Point) o;
		return x == other.x && y == other.y;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
	    hash = 71 * hash + this.x;
	    hash = 71 * hash + this.y;
	    return hash;
	}
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
