package com.nautilus.itmc;

import java.util.List;

public class Layer {
	private List<Node> nodes;
	
	public Node getNode(int idx) {
		return nodes.get(idx);
	}
	
	public int size() {
		return nodes.size();
	}
}
